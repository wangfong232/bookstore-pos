/*
 * DAO for SalesInvoice and SalesInvoiceDetail
 */
package DAO;

import entity.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
     * @param discountPercent giảm giá theo % (0–100)
     * @param paymentMethod  CASH hoặc TRANSFER
     * @return mã hóa đơn (InvoiceCode) nếu thành công, null nếu thất bại
     */
    public String createInvoice(String customerId, List<CartItem> cart, int staffId, Integer shiftId,
                                String note, double discountPercent, String paymentMethod) {
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
        if (discountPercent < 0) discountPercent = 0;
        if (discountPercent > 100) discountPercent = 100;
        double discountAmount = totalAmount * discountPercent / 100.0;
        double finalAmount = totalAmount - discountAmount;

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

        Connection conn = null;
        PreparedStatement invoiceStm = null;
        PreparedStatement detailStm = null;
        PreparedStatement paymentStm = null;
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
                    // ignore
                }
            }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // ignore
                }
            }
        }
    }
}

