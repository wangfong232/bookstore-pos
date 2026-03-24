/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.Product;
import entity.StockDisposal;
import entity.StockDisposalDetail;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;

/**
 *
 * @author qp
 */
public class StockDisposalDAO extends DBContext {

    public List<StockDisposal> searchSDWithPaginated(String keyword, String status, String reason,
            LocalDate from, LocalDate to, int page, int pageSize) {
        List<StockDisposal> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        StringBuilder sql = new StringBuilder("""
                select sd.*,
                    e1.FullName AS CreatedByName,
                    e2.FullName AS ApprovedByName,
                    e3.FullName AS DisposedByName
                from StockDisposals sd
                left join Employees e1 ON sd.CreatedBy  = e1.EmployeeID
                left join Employees e2 ON sd.ApprovedBy = e2.EmployeeID
                left join Employees e3 ON sd.DisposedBy = e3.EmployeeID
                where 1=1
                """);
        appendFilters(sql, keyword, status, reason, from, to);
        sql.append(" ORDER BY sd.DisposalID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql.toString())) {
            int idx = bindFilters(stm, 1, keyword, status, reason, from, to);
            stm.setInt(idx++, offset);
            stm.setInt(idx++, pageSize);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSDFromRS(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: searchSDWithPaginated: " + e.getMessage());
        }
        return list;
    }

    public int countSD(String keyword, String status, String reason, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("select COUNT(*) from StockDisposals sd WHERE 1=1");
        appendFilters(sql, keyword, status, reason, from, to);
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql.toString())) {
            bindFilters(stm, 1, keyword, status, reason, from, to);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR : countSD: " + e.getMessage());
        }
        return 0;
    }

    public StockDisposal getSDByNumber(String number) {
        String sql = """
                select sd.*, e1.FullName AS CreatedByName,
                     e2.FullName AS ApprovedByName,
                     e3.FullName AS DisposedByName
                from StockDisposals sd
                left join Employees e1 ON sd.CreatedBy = e1.EmployeeID
                left join Employees e2 ON sd.ApprovedBy = e2.EmployeeID
                left join Employees e3 ON sd.DisposedBy = e3.EmployeeID
                where sd.DisposalNumber = ?
                     """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setString(1, number);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    StockDisposal sd = extractSDFromRS(rs);
                    sd.setDetails(getSDDetailsById(sd.getId()));
                    return sd;
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getSDByNumber: " + e.getMessage());
        }
        return null;
    }

    public StockDisposal getSDById(long id) {
        String sql = """
                     select sd.*, e1.FullName AS CreatedByName,
                                  e2.FullName AS ApprovedByName,
                                  e3.FullName AS DisposedByName,
                     from StockDisposals sd
                     left join Employees e1 ON sd.CreatedBy = e1.EmployeeID
                     left join Employees e2 ON sd.ApprovedBy = e2.EmployeeID
                     left join Employees e3 ON sd.DisposedBy = e3.EmployeeID
                     where sd.DisposalID = ?
                     """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    StockDisposal sd = extractSDFromRS(rs);
                    sd.setDetails(getSDDetailsById(id));
                    return sd;
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getSDById: " + e.getMessage());
        }
        return null;
    }

    public List<StockDisposalDetail> getSDDetailsById(long sdId) {
        List<StockDisposalDetail> list = new ArrayList<>();
        String sql = """
                select d.*, p.ProductName, p.SKU, p.Stock AS CurrentStock
                from StockDisposalDetails d
                join Products p ON d.ProductID = p.ProductID
                where d.DisposalID = ?
                ORDER BY p.ProductName
                """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, sdId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSDDetailFromRS(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("ERR :getSDDetailsById: " + e.getMessage());
        }
        return list;
    }

    public String generateNextSDNumber() {
        int currentYear = java.time.Year.now().getValue();
        String sql = """
                      select top 1 DisposalNumber
                      from StockDisposals 
                      where DisposalNumber like ? 
                      order by DisposalNumber desc 
                      """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql)) {
            String yearPrefix = "SD-" + currentYear + '-';
            stm.setString(1, yearPrefix + "%");
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String lastSDNumber = rs.getString("DisposalNumber");
                    String numberPart = lastSDNumber.substring(lastSDNumber.lastIndexOf("-") + 1);
                    int nextNumber = Integer.parseInt(numberPart) + 1;
                    return String.format("SD-%d-%04d", currentYear, nextNumber);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: generateNextSDNumber: " + e.getMessage());
        }
        return String.format("SD-%d-0001", currentYear);
    }

    public boolean createDisposalWithDetails(StockDisposal sd) {
        String sqlSD = """
                         insert into StockDisposals
                         (DisposalNumber, DisposalDate, DisposalReason, Status, TotalQuantity, 
                         TotalValue, Notes, CreatedBy, CreatedAt)
                         values (?,?,?,?,?,?,?,?,?)
                         """;
        String sqlDetail = """
                             insert into StockDisposalDetails
                             (DisposalID, ProductID, Quantity, UnitCost, LineTotal, SpecificReason, Notes)
                             values (?,?,?,?,?,?,?)
                             """;
        Connection con = getConnection();
        try {
            con.setAutoCommit(false);
            long sdId;
            try (PreparedStatement stm = con.prepareStatement(sqlSD, Statement.RETURN_GENERATED_KEYS)) {
                stm.setString(1, sd.getDisposalNumber());
                stm.setTimestamp(2, Timestamp.valueOf(sd.getDisposalDate() != null ? sd.getDisposalDate() : LocalDateTime.now()));
                stm.setString(3, sd.getDisposalReason());
                stm.setString(4, sd.getStatus());
                stm.setInt(5, sd.getTotalQuantity());
                stm.setBigDecimal(6, sd.getTotalValue());
                stm.setString(7, sd.getNotes());
                stm.setInt(8, sd.getCreatedBy());
                stm.setTimestamp(9, Timestamp.valueOf(
                        sd.getCreatedAt() != null ? sd.getCreatedAt() : LocalDateTime.now()));
                stm.executeUpdate();
                try (ResultSet keys = stm.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No generated key returned");
                    }
                    sdId = keys.getLong(1);
                }
            }
            if (sd.getDetails() != null && !sd.getDetails().isEmpty()) {
                try (PreparedStatement stmD = con.prepareStatement(sqlDetail)) {
                    for (StockDisposalDetail detail : sd.getDetails()) {
                        stmD.setLong(1, sdId);
                        stmD.setInt(2, detail.getProductId());
                        stmD.setInt(3, detail.getQuantity());
                        stmD.setBigDecimal(4, detail.getUnitCost());
                        stmD.setBigDecimal(5, detail.getLineTotal());
                        stmD.setString(6, detail.getSpecificReason());
                        stmD.setString(7, detail.getNotes());
                        stmD.addBatch();
                    }
                    stmD.executeBatch();
                }
                //lock inventory
                String sqlReserve = "UPDATE Products SET ReservedStock = ReservedStock + ? WHERE ProductID = ?";
                try (PreparedStatement stmR = con.prepareStatement(sqlReserve)) {
                    for (StockDisposalDetail d : sd.getDetails()) {
                        stmR.setInt(1, d.getQuantity());
                        stmR.setInt(2, d.getProductId());
                        stmR.addBatch();
                    }
                    stmR.executeBatch();
                }
            }
            con.commit();
            return true;
        } catch (Exception e) {
            System.out.println("ERR: createDisposalWithDetails: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception e) {
            }
        }
        return false;
    }

    public boolean approveDisposal(long id, int approvedBy) {
        String sql = """
                        update StockDisposals
                        set Status = 'APPROVED', ApprovedBy = ?, ApprovedAt = GETDATE()
                        where DisposalID = ? AND Status = 'PENDING_APPROVAL' AND CreatedBy <> ?
                     """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, approvedBy);
            stm.setLong(2, id);
            stm.setInt(3, approvedBy);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: approveDisposal: " + e.getMessage());
        }
        return false;
    }

    public boolean rejectDisposal(long id, int rejectedBy, String reason) {
        String sqlReject = """
                        update StockDisposals
                        set Status = 'REJECTED', ApprovedBy = ?, ApprovedAt = GETDATE(), RejectionReason = ?
                        where DisposalID = ? AND Status = 'PENDING_APPROVAL'
                     """;
        String sqlGetDetails = "select ProductID, Quantity from StockDisposalDetails where DisposalID = ?";
        String sqlRelease = "update Products set ReservedStock = ReservedStock - ? where ProductID = ? AND ReservedStock >= ?";
        Connection con = getConnection();
        try {
            con.setAutoCommit(false);
            try (PreparedStatement stm = con.prepareStatement(sqlReject)) {
                stm.setInt(1, rejectedBy);
                stm.setString(2, reason);
                stm.setLong(3, id);
                if (stm.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }

            try (PreparedStatement stmD = con.prepareStatement(sqlGetDetails)) {
                stmD.setLong(1, id);
                try (ResultSet rs = stmD.executeQuery(); PreparedStatement stmR = con.prepareStatement(sqlRelease)) {
                    while (rs.next()) {
                        int qty = rs.getInt("Quantity");
                        stmR.setInt(1, qty);
                        stmR.setInt(2, rs.getInt("ProductID"));
                        stmR.setInt(3, qty);
                        stmR.addBatch();
                    }
                    stmR.executeBatch();
                }
            }
            con.commit();
            return true;
        } catch (Exception e) {
            System.out.println("ERR: rejectDisposal: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
        return false;
    }

    public boolean completeDisposal(long id, int disposedBy) {
        String sqlGetNumber = "select DisposalNumber, Notes from StockDisposals where DisposalID = ?";
        String sqlComplete = """
                         update StockDisposals
                         set Status = 'COMPLETED', PhysicalDisposalConfirmed = 1,
                         DisposedBy =?, DisposedAt = GETDATE()
                         where DisposalID = ? AND Status = 'APPROVED'
                         """;
        String sqlGetDetails = "select ProductID, Quantity, UnitCost, SpecificReason, Notes from StockDisposalDetails where DisposalID = ?";
        String sqlUpdateStock = """
             update Products
             set Stock = Stock - ? , ReservedStock = ReservedStock - ?
             OUTPUT deleted.Stock AS StockBefore, inserted.Stock AS StockAfter
             where ProductID = ? AND Stock >= ? AND ReservedStock >= ?
             """;
        String sqlInsertTx = """
                         insert into InventoryTransactions
                         (ProductID, TransactionType, ReferenceType, ReferenceID, ReferenceCode, 
                         QuantityChange, StockBefore, StockAfter, UnitCost, Notes, CreatedBy)
                          values (?, 'OUT', 'DISPOSAL', ?, ?, ?, ?, ?, ?, ?, ?)
                         """;

        String disposalNumber = null;
        String disposalNotes = null;

        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sqlGetNumber)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    disposalNumber = rs.getString("DisposalNumber");
                    disposalNotes = rs.getString("Notes");
                }
            }
        } catch (Exception e) {
            System.out.println("ERR : completeDisposal (getNumber): " + e.getMessage());
            return false;
        }
        if (disposalNumber == null) {
            return false;
        }

        Connection con = getConnection();
        try {
            con.setAutoCommit(false);

            //mark COMPLETED
            try (PreparedStatement stm = con.prepareStatement(sqlComplete)) {
                stm.setInt(1, disposedBy);
                stm.setLong(2, id);
                if (stm.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }

            //reduce stock + insert transaction
            try (PreparedStatement stmGet = con.prepareStatement(sqlGetDetails)) {
                stmGet.setLong(1, id);
                try (ResultSet rs = stmGet.executeQuery()) {
                    PreparedStatement stmStock = con.prepareStatement(sqlUpdateStock);
                    PreparedStatement stmTx = con.prepareStatement(sqlInsertTx);

                    while (rs.next()) {
                        int productId = rs.getInt("ProductID");
                        int qty = rs.getInt("Quantity");
                        BigDecimal cost = rs.getBigDecimal("UnitCost");
                        String detNote = rs.getString("Notes");
                        String specReason = rs.getString("SpecificReason");

                        stmStock.setInt(1, qty);
                        stmStock.setInt(2, qty);
                        stmStock.setInt(3, productId);
                        stmStock.setInt(4, qty);
                        stmStock.setInt(5, qty);

                        int stockBefore, stockAfter;
                        try (ResultSet out = stmStock.executeQuery()) {
                            if (!out.next()) {
                                con.rollback();
                                return false;
                            }
                            stockBefore = out.getInt("StockBefore");
                            stockAfter = out.getInt("StockAfter");
                        }

                        stmTx.setInt(1, productId);
                        stmTx.setLong(2, id);
                        stmTx.setString(3, disposalNumber);
                        stmTx.setInt(4, -qty);
                        stmTx.setInt(5, stockBefore);
                        stmTx.setInt(6, stockAfter);
                        stmTx.setBigDecimal(7, cost);
                        String txNote = "";
                        if (specReason != null && !specReason.trim().isEmpty()) {
                            txNote += "[" + specReason + "] ";
                        }
                        if (detNote != null && !detNote.trim().isEmpty()) {
                            txNote += detNote;
                        }
                        if (txNote == null || txNote.trim().isEmpty()) {
                            txNote = "Xuất hủy theo phiếu " + disposalNumber;
                        }
                        stmTx.setString(8, txNote);
                        stmTx.setInt(9, disposedBy);
                        stmTx.addBatch();
                    }
                    stmTx.executeBatch();
                }
            }
            con.commit();
            return true;
        } catch (Exception e) {
            System.out.println("ERR: completeDisposal: " + e.getMessage());
            try {
                con.rollback();
            } catch (Exception ex) {
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (Exception ex) {
            }
        }
        return false;
    }

    //product helper
    public List<Product> getAllActiveProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
                     select ProductID, ProductName, SKU, Stock, ReservedStock, CostPrice
                     from Products
                     where IsActive = 1
                     ORDER BY ProductName
                     """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setSku(rs.getString("SKU"));
                p.setStock(rs.getInt("Stock"));
                p.setReservedStock(rs.getInt("ReservedStock"));
                p.setCostPrice(rs.getDouble("CostPrice"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("ERR : getAllActiveProducts: " + e.getMessage());
        }
        return list;
    }

    private void appendFilters(StringBuilder sql, String keyword, String status, String reason, LocalDate from, LocalDate to) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (sd.DisposalNumber LIKE ? OR sd.Notes LIKE ?)");
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND sd.Status = ?");
        }
        if (reason != null && !reason.trim().isEmpty()) {
            sql.append(" AND sd.DisposalReason = ?");
        }
        if (from != null) {
            sql.append(" AND CAST(sd.DisposalDate AS DATE) >= ?");
        }
        if (to != null) {
            sql.append(" AND CAST(sd.DisposalDate AS DATE) <= ?");
        }
    }

    private int bindFilters(PreparedStatement stm, int idx, String keyword, String status,
            String reason, LocalDate from, LocalDate to) throws SQLException {
        if (keyword != null && !keyword.trim().isEmpty()) {
            stm.setString(idx++, "%" + keyword + "%");
            stm.setString(idx++, "%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            stm.setString(idx++, status);
        }
        if (reason != null && !reason.trim().isEmpty()) {
            stm.setString(idx++, reason);
        }
        if (from != null) {
            stm.setDate(idx++, Date.valueOf(from));
        }
        if (to != null) {
            stm.setDate(idx++, Date.valueOf(to));
        }
        return idx;
    }

    private StockDisposal extractSDFromRS(ResultSet rs) throws SQLException {
        StockDisposal sd = new StockDisposal();
        sd.setId(rs.getLong("DisposalID"));
        sd.setDisposalNumber(rs.getString("DisposalNumber"));
        sd.setDisposalDate(getLocalDateTime(rs, "DisposalDate"));
        sd.setDisposalReason(rs.getString("DisposalReason"));
        sd.setStatus(rs.getString("Status"));
        sd.setTotalQuantity(rs.getInt("TotalQuantity"));
        BigDecimal tv = rs.getBigDecimal("TotalValue");
        sd.setTotalValue(tv != null ? tv : BigDecimal.ZERO);
        sd.setApprovedBy(rs.getInt("ApprovedBy"));
        sd.setApprovedAt(getLocalDateTime(rs, "ApprovedAt"));
        sd.setRejectionReason(rs.getString("RejectionReason"));
        sd.setPhysicalDisposalConfirmed(rs.getBoolean("PhysicalDisposalConfirmed"));
        sd.setDisposedBy(rs.getInt("DisposedBy"));
        sd.setDisposedAt(getLocalDateTime(rs, "DisposedAt"));
        sd.setNotes(rs.getString("Notes"));
        sd.setCreatedBy(rs.getInt("CreatedBy"));
        sd.setCreatedAt(getLocalDateTime(rs, "CreatedAt"));
        try {
            sd.setCreatedByName(rs.getString("CreatedByName"));
        } catch (Exception ignored) {
        }
        try {
            sd.setApprovedByName(rs.getString("ApprovedByName"));
        } catch (Exception ignored) {
        }
        try {
            sd.setDisposedByName(rs.getString("DisposedByName"));
        } catch (Exception ignored) {
        }
        return sd;
    }

    private StockDisposalDetail extractSDDetailFromRS(ResultSet rs) throws SQLException {
        StockDisposalDetail d = new StockDisposalDetail();
        d.setId(rs.getLong("DisposalDetailID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setQuantity(rs.getInt("Quantity"));
        BigDecimal uc = rs.getBigDecimal("UnitCost");
        d.setUnitCost(uc != null ? uc : BigDecimal.ZERO);
        BigDecimal lt = rs.getBigDecimal("LineTotal");
        d.setLineTotal(lt != null ? lt : BigDecimal.ZERO);
        d.setSpecificReason(rs.getString("SpecificReason"));
        d.setNotes(rs.getString("Notes"));
        try {
            d.setProductName(rs.getString("ProductName"));
        } catch (Exception ignored) {
        }
        try {
            d.setProductSku(rs.getString("SKU"));
        } catch (Exception ignored) {
        }
        try {
            d.setCurrentStock(rs.getInt("CurrentStock"));
        } catch (Exception ignored) {
        }
        return d;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts != null ? ts.toLocalDateTime() : null;
    }

}
