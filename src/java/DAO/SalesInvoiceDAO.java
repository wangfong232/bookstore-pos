/*
 * DAO for SalesInvoice and SalesInvoiceDetail
 */
package DAO;

import entity.CartItem;
import entity.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesInvoiceDAO extends DBContext {

    private static String lastErrorMessage;

    public static String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private static void setLastErrorMessage(String message) {
        lastErrorMessage = message;
    }

    /**
     * Tạo hóa đơn bán hàng và chi tiết từ giỏ hàng.
     *
     * @param customerId     mã khách hàng (có thể null / rỗng)
     * @param cart           danh sách CartItem
     * @param staffId        ID nhân viên thu ngân
     * @param shiftId        ID ca làm (có thể null)
     * @param note           ghi chú hóa đơn (có thể null)
     * @param discountAmount số tiền giảm giá (đã tính sẵn từ promotion)
     * @param paymentMethod  CASH hoặc TRANSFER
     * @return mã hóa đơn (InvoiceCode) nếu thành công, null nếu thất bại
     */
    public String createInvoice(String customerId, List<CartItem> cart, int staffId, Integer shiftId,
                                String note, double discountAmount, double vatAmount, String paymentMethod) {
        setLastErrorMessage(null);
        if (cart == null || cart.isEmpty()) {
            setLastErrorMessage("Giỏ hàng trống.");
            return null;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "CASH";
        }

        String invoiceCode = "INV-" + System.currentTimeMillis();

        double totalAmount = cart.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();
        if (discountAmount < 0) discountAmount = 0;
        if (discountAmount > totalAmount) discountAmount = totalAmount;
        if (vatAmount < 0) vatAmount = 0;
        double finalAmount = totalAmount - discountAmount + vatAmount;

        String insertInvoiceSql = """
                INSERT INTO SalesInvoice
                (InvoiceCode, StaffID, CustomerID, ShiftID,
                 TotalAmount, DiscountAmount, FinalAmount,
                 PaymentStatus, Note)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String insertDetailSql = """
                INSERT INTO SalesInvoiceDetail
                (InvoiceID, ProductID, Quantity, UnitPrice, Discount, TotalPrice)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertPaymentSql = """
                INSERT INTO Payments (InvoiceID, PaymentMethod, Amount, PaidAt, Status)
                VALUES (?, ?, ?, GETDATE(), 'COMPLETED')
                """;

        String updateStockSql = """
                UPDATE Products
                SET Stock = Stock - ?
                WHERE ProductID = ?
                  AND Stock >= ?
                """;

        Connection conn = null;
        PreparedStatement invoiceStm = null;
        PreparedStatement detailStm = null;
        PreparedStatement paymentStm = null;
        PreparedStatement stockStm = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn == null) {
                setLastErrorMessage("Không kết nối được tới cơ sở dữ liệu.");
                return null;
            }
            conn.setAutoCommit(false);

            invoiceStm = conn.prepareStatement(insertInvoiceSql, PreparedStatement.RETURN_GENERATED_KEYS);
            invoiceStm.setString(1, invoiceCode);
            invoiceStm.setInt(2, staffId);
            if (customerId != null && !customerId.trim().isEmpty()) {
                invoiceStm.setString(3, customerId.trim());
            } else {
                invoiceStm.setNull(3, java.sql.Types.NVARCHAR);
            }
            if (shiftId != null) {
                invoiceStm.setInt(4, shiftId);
            } else {
                invoiceStm.setNull(4, java.sql.Types.INTEGER);
            }
            invoiceStm.setDouble(5, totalAmount);
            invoiceStm.setDouble(6, discountAmount);
            invoiceStm.setDouble(7, finalAmount);
            invoiceStm.setString(8, "PAID");
            invoiceStm.setString(9, note);

            int affected = invoiceStm.executeUpdate();
            if (affected == 0) {
                setLastErrorMessage("Không thể chèn bản ghi hóa đơn (0 dòng bị ảnh hưởng).");
                conn.rollback();
                return null;
            }

            rs = invoiceStm.getGeneratedKeys();
            long invoiceId = -1;
            if (rs.next()) {
                invoiceId = rs.getLong(1);
            } else {
                // Fallback: một số driver SQL Server không trả về generated keys
                try (PreparedStatement findIdStm = conn.prepareStatement(
                        "SELECT TOP 1 InvoiceID FROM SalesInvoice WHERE InvoiceCode = ?")) {
                    findIdStm.setString(1, invoiceCode);
                    try (ResultSet rs2 = findIdStm.executeQuery()) {
                        if (rs2.next()) {
                            invoiceId = rs2.getLong(1);
                        } else {
                            setLastErrorMessage("Không lấy được InvoiceID sau khi chèn hóa đơn.");
                            conn.rollback();
                            return null;
                        }
                    }
                }
            }

            detailStm = conn.prepareStatement(insertDetailSql);
            for (CartItem item : cart) {
                detailStm.setLong(1, invoiceId);
                detailStm.setInt(2, item.getProduct().getProductID());
                detailStm.setInt(3, item.getQuantity());
                detailStm.setDouble(4, item.getUnitPrice());
                detailStm.setDouble(5, 0);
                detailStm.setDouble(6, item.getLineTotal());
                detailStm.addBatch();
            }
            detailStm.executeBatch();

            // Trừ tồn kho
            stockStm = conn.prepareStatement(updateStockSql);
            for (CartItem item : cart) {
                int qty = item.getQuantity();
                int productId = item.getProduct().getProductID();
                stockStm.setInt(1, qty);
                stockStm.setInt(2, productId);
                stockStm.setInt(3, qty);
                int updated = stockStm.executeUpdate();
                if (updated == 0) {
                    setLastErrorMessage("Tồn kho không đủ cho sản phẩm ID=" + productId + " (cần " + qty + ").");
                    conn.rollback();
                    return null;
                }
            }

            paymentStm = conn.prepareStatement(insertPaymentSql);
            paymentStm.setLong(1, invoiceId);
            paymentStm.setString(2, paymentMethod.trim().toUpperCase());
            paymentStm.setDouble(3, finalAmount);
            paymentStm.executeUpdate();

            conn.commit();
            return invoiceCode;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    // ignore
                }
            }
            setLastErrorMessage(ex.getMessage());
            System.out.println("ERR: createInvoice: " + ex.getMessage());
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (invoiceStm != null) {
                try {
                    invoiceStm.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (detailStm != null) {
                try {
                    detailStm.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
            if (paymentStm != null) {
                try {
                    paymentStm.close();
                } catch (SQLException e) {
                }
            }
            if (stockStm != null) {
                try {
                    stockStm.close();
                } catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public double calculateVatAmount(List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return 0;
        }

        List<Integer> categoryIds = new ArrayList<>();
        for (CartItem item : cart) {
            Product p = item.getProduct();
            if (p == null) {
                continue;
            }
            int catId = p.getCategoryID();
            if (!categoryIds.contains(catId)) {
                categoryIds.add(catId);
            }
        }

        Map<Integer, String> categoryNames = fetchCategoryNames(categoryIds);

        double vat = 0;
        for (CartItem item : cart) {
            Product p = item.getProduct();
            if (p == null) {
                continue;
            }
            String categoryName = categoryNames.get(p.getCategoryID());
            double rate = getVatRateFromCategoryName(categoryName);
            vat += item.getLineTotal() * rate;
        }
        return vat;
    }

    private Map<Integer, String> fetchCategoryNames(List<Integer> categoryIds) {
        Map<Integer, String> map = new HashMap<>();
        if (categoryIds == null || categoryIds.isEmpty()) {
            return map;
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < categoryIds.size(); i++) {
            if (i > 0) placeholders.append(",");
            placeholders.append("?");
        }

        String sql = "SELECT CategoryID, CategoryName FROM Categories WHERE CategoryID IN (" + placeholders + ")";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            for (Integer id : categoryIds) {
                ps.setInt(idx++, id);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("CategoryID"), rs.getString("CategoryName"));
                }
            }
        } catch (SQLException ignored) {
        }

        return map;
    }

    public double getVatRateFromCategoryName(String categoryName) {
        if (categoryName == null) {
            return 0.05;
        }
        String name = categoryName.trim().toLowerCase();
        if (name.equals("sách giáo khoa")
                || name.equals("sach giao khoa")
                || name.equals("sách khoa học")
                || name.equals("sach khoa hoc")
                || name.equals("sách chính trị")
                || name.equals("sach chinh tri")
                || name.equals("sách pháp luật")
                || name.equals("sach phap luat")
                || name.equals("sách khoa học kỹ thuật")
                || name.equals("sach khoa hoc ky thuat")) {
            return 0.0;
        }
        return 0.05;
    }

    public List<Map<String, Object>> searchInvoices(java.sql.Date fromDate,
                                                    java.sql.Date toDate,
                                                    String keyword,
                                                    int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (limit <= 0) {
            limit = 100;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP ").append(limit).append("""
                si.InvoiceID,
                si.InvoiceCode,
                si.TotalAmount,
                si.DiscountAmount,
                si.FinalAmount,
                si.PaymentStatus,
                c.FullName AS CustomerName,
                e.FullName AS StaffName,
                p.PaidAt AS PaidAt
            FROM SalesInvoice si
            LEFT JOIN Customers c ON si.CustomerID = c.CustomerID
            LEFT JOIN Employees e ON si.StaffID = e.EmployeeID
            LEFT JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            WHERE 1 = 1
            """);

        List<Object> params = new ArrayList<>();

        if (fromDate != null) {
            sql.append(" AND CAST(ISNULL(p.PaidAt, si.CreatedAt) AS DATE) >= ? ");
            params.add(fromDate);
        }
        if (toDate != null) {
            sql.append(" AND CAST(ISNULL(p.PaidAt, si.CreatedAt) AS DATE) <= ? ");
            params.add(toDate);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                    AND (
                        si.InvoiceCode LIKE ?
                        OR c.FullName LIKE ?
                        OR e.FullName LIKE ?
                    )
                    """);
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        sql.append(" ORDER BY ISNULL(p.PaidAt, GETDATE()) DESC, si.InvoiceID DESC");

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int idx = 1;
            for (Object p : params) {
                if (p instanceof java.sql.Date d) {
                    ps.setDate(idx++, d);
                } else if (p instanceof String s) {
                    ps.setString(idx++, s);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("invoiceId", rs.getLong("InvoiceID"));
                    row.put("invoiceCode", rs.getString("InvoiceCode"));
                    row.put("totalAmount", rs.getDouble("TotalAmount"));
                    row.put("discountAmount", rs.getDouble("DiscountAmount"));
                    row.put("finalAmount", rs.getDouble("FinalAmount"));
                    row.put("paymentStatus", rs.getString("PaymentStatus"));
                    row.put("customerName", rs.getString("CustomerName"));
                    row.put("staffName", rs.getString("StaffName"));
                    row.put("paidAt", rs.getTimestamp("PaidAt"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("ERR: searchInvoices: " + e.getMessage());
        }

        return list;
    }
}

