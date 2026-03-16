/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.Category;
import entity.Product;
import entity.StockTake;
import entity.StockTakeDetail;
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
public class StockTakeDAO extends DBContext {

    public List<StockTake> searchWithPaginated(String keyword, String status,
            LocalDate from, LocalDate to, int page, int pageSize) {
        List<StockTake> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("""
                    select st.*,
                                e1.FullName AS CreatedByName,
                                e2.FullName AS ApprovedByName,
                                e3.FullName AS RecountByName
                    from   StockTakes st
                    left join Employees e1 ON st.CreatedBy = e1.EmployeeID
                    left join Employees e2 ON st.ApprovedBy = e2.EmployeeID
                    left join Employees e3 ON st.RecountRequestedBy = e3.EmployeeID
                    where 1 = 1
                   """);

        appendFilters(sql, keyword, status, from, to);
        sql.append(" ORDER BY st.StockTakeID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            int idx = bindFilters(stm, 1, keyword, status, from, to);
            stm.setInt(idx++, offset);
            stm.setInt(idx, pageSize);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSTFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: StockTakeDAO.searchWithPaginated: " + e.getMessage());
        }
        return list;

    }

    public int count(String keyword, String status, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("select COUNT(*) from StockTakes st WHERE 1=1");
        appendFilters(sql, keyword, status, from, to);
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql.toString())) {
            bindFilters(stm, 1, keyword, status, from, to);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: StockTakeDAO.count: " + e.getMessage());
        }
        return 0;
    }

    public StockTake getSTById(long id) {
        String sql = """
              select st.*,
                                                       e1.FullName AS CreatedByName,
                                                       e2.FullName AS ApprovedByName,
                                                       e3.FullName AS RecountByName
                                                from   StockTakes st
                                                left join Employees e1 ON st.CreatedBy      = e1.EmployeeID
                                                left join Employees e2 ON st.ApprovedBy     = e2.EmployeeID
                                                left join Employees e3 ON st.RecountRequestedBy = e3.EmployeeID
                                                where st.StockTakeID = ? 
             """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    StockTake st = extractSTFromResultSet(rs);
                    st.setDetails(getSTDetailsById(id));
                    return st;
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getSTById: " + e.getMessage());
        }
        return null;
    }

    public StockTake getSTByNumber(String stNumber) {
        String sql = """
              select st.*,
                        e1.FullName AS CreatedByName,
                        e2.FullName AS ApprovedByName,
                        e3.FullName AS RecountByName
                        from   StockTakes st
                        left join Employees e1 ON st.CreatedBy      = e1.EmployeeID
                        left join Employees e2 ON st.ApprovedBy     = e2.EmployeeID
                        left join Employees e3 ON st.RecountRequestedBy = e3.EmployeeID
                        where st.StockTakeNumber = ? 
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, stNumber);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    StockTake st = extractSTFromResultSet(rs);
                    st.setDetails(getSTDetailsById(st.getId()));
                    return st;
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getSTByNumber: " + e.getMessage());
        }
        return null;
    }

    public List<StockTakeDetail> getSTDetailsById(long stId) {
        List<StockTakeDetail> list = new ArrayList<>();
        String sql = """
                select d.*, p.ProductName, p.SKU
                from StockTakeDetails d
                join Products p ON d.ProductID = p.ProductID
                where d.StockTakeID = ?
                ORDER BY p.ProductName
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, stId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractSTDetailsFromResultSet(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getSTDetailsById: " + e.getMessage());
        }
        return list;
    }

    public List<Product> getAllActiveProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
                select ProductID, ProductName, SKU, Stock, CostPrice, CategoryID
                from Products
                where  IsActive = 1
                ORDER BY ProductName
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setSku(rs.getString("SKU"));
                p.setStock(rs.getInt("Stock"));
                p.setCostPrice(rs.getDouble("CostPrice"));
                p.setCategoryId(rs.getInt("CategoryID"));
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("ERR: getAllActiveProducts: " + e.getMessage());
        }
        return list;
    }

    public String generateNextSTNumber() {
        int currentYear = java.time.Year.now().getValue();
        String sql = """
                     select top 1 StockTakeNumber
                     from StockTakes
                     where StockTakeNumber like ?
                     order by StockTakeNumber desc
                     """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            String yearPrefix = "ST-" + currentYear + '-';
            stm.setString(1, yearPrefix + "%");
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    String lastSTNumber = rs.getString("StockTakeNumber");
                    String numberPart = lastSTNumber.substring(lastSTNumber.lastIndexOf("-") + 1);
                    int nextNumber = Integer.parseInt(numberPart) + 1;
                    return String.format("ST-%d-%04d", currentYear, nextNumber);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: GenerateNextSTNumber: " + e.getMessage());
        }
        return String.format("ST-%d-0001", currentYear);
    }

    public boolean createSTWithDetails(StockTake st) {
        String sqlST = """
                       insert into StockTakes
                       (StockTakeNumber, ScopeType, ScopeValue, StockTakeDate, Status, TotalItems, 
                       TotalVarianceQty, TotalVarianceValue, Notes, CreatedBy, CreatedAt)
                       values(?,?,?,?,?,?,?,?,?,?,?)
                       """;
        String sqlDetail = """
                           insert into StockTakeDetails
                            (StockTakeID, ProductID, SystemQuantity, ActualQuantity, UnitCost, VarianceReason, Notes)
                           values (?,?,?,?,?,?,?)
                            """;
        Connection con = getConnection();
        try {
            con.setAutoCommit(false);
            long stId;
            try (PreparedStatement stm = con.prepareStatement(sqlST, Statement.RETURN_GENERATED_KEYS)) {
                stm.setString(1, st.getStockTakeNumber());
                stm.setString(2, st.getScopeType());
                stm.setString(3, st.getScopeValue());
                stm.setDate(4, Date.valueOf(st.getStockTakeDate()));
                stm.setString(5, st.getStatus());
                stm.setInt(6, st.getTotalItems());
                stm.setInt(7, st.getTotalVarianceQty());
                stm.setBigDecimal(8, st.getTotalVarianceValue());
                stm.setString(9, st.getNotes());
                stm.setInt(10, st.getCreatedBy());
                stm.setTimestamp(11, Timestamp.valueOf(st.getCreatedAt()));
                stm.executeUpdate();
                try (ResultSet keys = stm.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("No generated key");
                    }
                    stId = keys.getLong(1);
                }

                if (st.getDetails() != null) {
                    try (PreparedStatement stmD = con.prepareStatement(sqlDetail)) {
                        for (StockTakeDetail detail : st.getDetails()) {
                            stmD.setLong(1, stId);
                            stmD.setInt(2, detail.getProductId());
                            stmD.setInt(3, detail.getSystemQuantity());
                            stmD.setInt(4, detail.getActualQuantity());
                            stmD.setBigDecimal(5, detail.getUnitCost());
                            stmD.setString(6, detail.getVarianceReason());
                            stmD.setString(7, detail.getNotes());
                            stmD.addBatch();
                        }
                        stmD.executeBatch();
                    }
                }

                con.commit();
                return true;
            } catch (Exception e) {
                System.out.println("ERR: createSTWithDetails: " + e.getMessage());
                try {
                    con.rollback();
                } catch (Exception ex) {
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public boolean submitSTForApproval(long id, int createdBy) {
        String sql = """
                     update StockTakes 
                     set Status = 'PENDING_APPROVAL',
                        SubmittedAt = GETDATE(),
                        TotalItems = (select COUNT(*) from StockTakeDetails where StockTakeID = ? ),
                        TotalVarianceQty = (select ISNULL(SUM(ActualQuantity - SystemQuantity), 0) from StockTakeDetails where StockTakeID = ?),
                        TotalVarianceValue = (select ISNULL(SUM((ActualQuantity - SystemQuantity)* UnitCost), 0) from StockTakeDetails where StockTakeID = ? )
                     where
                        StockTakeID = ? AND CreatedBy = ? AND Status = 'IN_PROGRESS'
                     """;

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            stm.setLong(2, id);
            stm.setLong(3, id);
            stm.setLong(4, id);
            stm.setInt(5, createdBy);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: submitSTForApproval: " + e.getMessage());
        }
        return false;
    }

    public boolean approveST(long id, int approvedBy) {
        String sqlApprove = """
                update StockTakes
                set    Status     = 'COMPLETED',
                       ApprovedBy = ?,
                       ApprovedAt = GETDATE()
                where  StockTakeID = ? AND Status = 'PENDING_APPROVAL' AND  CreatedBy <> ?
                """;
        String sqlGetDetails = """
                                 select ProductID, ActualQuantity, UnitCost, Notes
                                  from  StockTakeDetails
                               where  StockTakeID = ? AND ActualQuantity <> SystemQuantity
                               """;
        String sqlUpdateStock = """
                                update Products 
                                set Stock = ?
                                OUTPUT deleted.Stock AS StockBefore, inserted.Stock AS StockAfter
                                where ProductID = ?
                                """;
        String sqlInsertTx = """
                             insert into InventoryTransactions 
                             (ProductID, TransactionType, ReferenceType, ReferenceID, ReferenceCode,
                                                  QuantityChange, StockBefore, StockAfter, UnitCost, Notes, CreatedBy)
                             values (?, 'ADJUSTMENT', 'STOCK_TAKE', ?, ?, ?, ?, ?, ?, ?, ?)
                              """;

        String stockTakeNumber = null;
        String stockTakeNotes = null;

        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(
                "select StockTakeNumber, Notes  FROM StockTakes WHERE StockTakeID = ?")) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    stockTakeNumber = rs.getString("StockTakeNumber");
                    stockTakeNotes = rs.getString("Notes");
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: approveST – getSTnumber: " + e.getMessage());
            return false;
        }
        if (stockTakeNumber == null) {
            return false;
        }

        Connection con = getConnection();
        try {
            con.setAutoCommit(false);

            //update ST status
            try (PreparedStatement stm = con.prepareStatement(sqlApprove)) {
                stm.setInt(1, approvedBy);
                stm.setLong(2, id);
                stm.setInt(3, approvedBy);
                if (stm.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }

            //update stock (only variance)
            try (PreparedStatement stmGet = con.prepareStatement(sqlGetDetails)) {
                stmGet.setLong(1, id);
                try (ResultSet rs = stmGet.executeQuery()) {
                    try (PreparedStatement stmStock = con.prepareStatement(sqlUpdateStock); PreparedStatement stmTx = con.prepareStatement(sqlInsertTx);) {

                        while (rs.next()) {
                            int productId = rs.getInt("ProductID");
                            int actQty = rs.getInt("ActualQuantity");
                            BigDecimal cost = rs.getBigDecimal("UnitCost");
                            String detailNotes = rs.getString("Notes");

                            stmStock.setInt(1, actQty);
                            stmStock.setInt(2, productId);
                            int stockBefore, stockAfter, qtyChange;
                            try (ResultSet out = stmStock.executeQuery()) {
                                out.next();
                                stockBefore = out.getInt("StockBefore");
                                stockAfter = out.getInt("StockAfter");
                                qtyChange = stockAfter - stockBefore;
                            }

                            //update transaction
                            stmTx.setInt(1, productId);
                            stmTx.setLong(2, id);
                            stmTx.setString(3, stockTakeNumber);
                            stmTx.setInt(4, qtyChange);
                            stmTx.setInt(5, stockBefore);
                            stmTx.setInt(6, stockAfter);
                            stmTx.setBigDecimal(7, cost);

                            String txNote = detailNotes;
                            if (txNote == null || txNote.trim().isEmpty()) {
                                txNote = stockTakeNotes;
                            }
                            if (txNote == null || txNote.trim().isEmpty()) {
                                txNote = "Điều chỉnh kiểm kê theo phiếu " + stockTakeNumber;
                            }

                            stmTx.setString(8, txNote);
                            stmTx.setInt(9, approvedBy);
                            stmTx.addBatch();
                        }
                        stmTx.executeBatch();
                    }
                }
            }
            con.commit();
            return true;
        } catch (Exception e) {
            System.out.println("ERR: approveST: " + e.getMessage());
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

    //lock warehouse -- soft-freeze
    public boolean isWarehouseLocked() {
        String sql = "select TOP 1 1 from StockTakes where Status IN ('IN_PROGRESS','PENDING_APPROVAL')";
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            return rs.next();
        } catch (Exception e) {
            System.out.println("ERR: isWarehouseLocked: " + e.getMessage());
        }
        return false;
    }

    public String getActiveLockSTNumber() {
        String sql = """
                select TOP 1 StockTakeNumber FROM StockTakes
                where Status IN ('IN_PROGRESS','PENDING_APPROVAL')
                ORDER BY StockTakeID DESC
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getString(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: getActiveLockSTNumber: " + e.getMessage());
        }
        return null;
    }

    public boolean requestRecountST(long id, int requestedBy, String reason) {
        String sql = """
                update StockTakes
                set    Status                = 'IN_PROGRESS',
                       RecountRequestedBy    = ?,
                       RecountRequestedAt    = GETDATE(),
                       RecountReason         = ?,
                       SubmittedAt           = NULL
                where  StockTakeID = ? AND Status = 'PENDING_APPROVAL'
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, requestedBy);
            stm.setString(2, reason);
            stm.setLong(3, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: requestRecountST: " + e.getMessage());
        }
        return false;
    }

    public boolean updateSTDetails(StockTake st) {
        String sqlUpdateMaster = """
            UPDATE StockTakes 
            SET ScopeType = ?, ScopeValue = ?, Notes = ?, 
                TotalItems = ?, TotalVarianceQty = ?, TotalVarianceValue = ?
            WHERE StockTakeID = ?
            """;

        String sqlDel = "DELETE FROM StockTakeDetails WHERE StockTakeID = ?";

        String sqlIns = """
            INSERT INTO StockTakeDetails 
              (StockTakeID, ProductID, SystemQuantity, ActualQuantity, UnitCost, VarianceReason, Notes) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement stmUpdate = connection.prepareStatement(sqlUpdateMaster)) {
                stmUpdate.setString(1, st.getScopeType());
                stmUpdate.setString(2, st.getScopeValue());
                stmUpdate.setString(3, st.getNotes());
                stmUpdate.setInt(4, st.getTotalItems());
                stmUpdate.setInt(5, st.getTotalVarianceQty());
                stmUpdate.setBigDecimal(6, st.getTotalVarianceValue());
                stmUpdate.setLong(7, st.getId());
                stmUpdate.executeUpdate();
            }

            try (PreparedStatement stmDel = connection.prepareStatement(sqlDel)) {
                stmDel.setLong(1, st.getId());
                stmDel.executeUpdate();
            }

            try (PreparedStatement stmIns = connection.prepareStatement(sqlIns)) {
                for (StockTakeDetail detail : st.getDetails()) {
                    stmIns.setLong(1, st.getId());
                    stmIns.setInt(2, detail.getProductId());
                    stmIns.setInt(3, detail.getSystemQuantity());
                    stmIns.setInt(4, detail.getActualQuantity());
                    stmIns.setBigDecimal(5, detail.getUnitCost());
                    stmIns.setString(6, detail.getVarianceReason());
                    stmIns.setString(7, detail.getNotes());
                    stmIns.addBatch();
                }
                stmIns.executeBatch();
            }

            connection.commit();
            return true;

        } catch (Exception e) {
            System.out.println("ERR: updateSTDetails: " + e.getMessage());
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception ex) {
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (Exception ex) {
                }
            }
        }
        return false;
    }
    
    
   public boolean cancelST(long id, int cancelledBy) {
        String sql = """
                update StockTakes
                set Status = 'CANCELLED',
                    Notes = CASE
                        WHEN Notes IS NULL OR Notes = '' THEN '(Đã bị hủy)'
                        ELSE CONCAT(Notes, ' (Đã bị hủy)')
                    END
                where StockTakeID = ? AND Status IN ('IN_PROGRESS', 'PENDING_APPROVAL')
                """;
        try (Connection connection = getConnection(); PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setLong(1, id);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: cancelST: " + e.getMessage());
        }
        return false;
    }

    private void appendFilters(StringBuilder sql, String keyword, String status,
            LocalDate from, LocalDate to) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("""
                        AND (
                           st.StockTakeNumber LIKE ?
                           OR st.ScopeValue LIKE ?
                           OR st.Notes LIKE ?
                       ) """);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND st.Status = ? ");
        }
        if (from != null) {
            sql.append(" AND st.StockTakeDate >= ? ");
        }
        if (to != null) {
            sql.append(" AND st.StockTakeDate <= ? ");
        }
    }

    private int bindFilters(PreparedStatement stm, int idx,
            String keyword, String status, LocalDate from, LocalDate to) throws SQLException {
        if (keyword != null && !keyword.trim().isEmpty()) {
            stm.setString(idx++, "%" + keyword + "%");
            stm.setString(idx++, "%" + keyword + "%");
            stm.setString(idx++, "%" + keyword + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            stm.setString(idx++, status);
        }
        if (from != null) {
            stm.setDate(idx++, Date.valueOf(from));
        }
        if (to != null) {
            stm.setDate(idx++, Date.valueOf(to));
        }
        return idx;
    }

    private StockTake extractSTFromResultSet(ResultSet rs) throws SQLException {
        StockTake st = new StockTake();
        st.setId(rs.getLong("StockTakeID"));
        st.setStockTakeNumber(rs.getString("StockTakeNumber"));
        st.setScopeType(rs.getString("ScopeType"));
        st.setScopeValue(rs.getString("ScopeValue"));
        Date d = rs.getDate("StockTakeDate");
        if (d != null) {
            st.setStockTakeDate(d.toLocalDate());
        }
        st.setStatus(rs.getString("Status"));
        st.setTotalItems(rs.getInt("TotalItems"));
        st.setTotalVarianceQty(rs.getInt("TotalVarianceQty"));
        BigDecimal totalVarianceValue = rs.getBigDecimal("TotalVarianceValue");
        st.setTotalVarianceValue(totalVarianceValue != null ? totalVarianceValue : BigDecimal.ZERO);
        st.setNotes(rs.getString("Notes"));

        st.setCreatedBy(rs.getInt("CreatedBy"));
        st.setCreatedAt(getLocalDateTime(rs, "CreatedAt"));

        st.setSubmittedAt(getLocalDateTime(rs, "SubmittedAt"));

        st.setApprovedBy(rs.getInt("ApprovedBy"));
        st.setApprovedAt(getLocalDateTime(rs, "ApprovedAt"));

        st.setRecountRequestedBy(rs.getInt("RecountRequestedBy"));
        st.setRecountRequestedAt(getLocalDateTime(rs, "RecountRequestedAt"));
        st.setRecountReason(rs.getString("RecountReason"));

        try {
            st.setCreatedByName(rs.getString("CreatedByName"));
        } catch (Exception ignored) {
        }
        try {
            st.setApprovedByName(rs.getString("ApprovedByName"));
        } catch (Exception ignored) {
        }
        try {
            st.setRecountByName(rs.getString("RecountByName"));
        } catch (Exception ignored) {
        }

        return st;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp ts = rs.getTimestamp(columnName);
        return (ts != null) ? ts.toLocalDateTime() : null;
    }

    private StockTakeDetail extractSTDetailsFromResultSet(ResultSet rs) throws SQLException {
        StockTakeDetail d = new StockTakeDetail();
        d.setId(rs.getLong("StockTakeDetailID"));
        d.setProductId(rs.getInt("ProductID"));
        d.setSystemQuantity(rs.getInt("SystemQuantity"));
        d.setActualQuantity(rs.getInt("ActualQuantity"));
        d.setUnitCost(rs.getBigDecimal("UnitCost"));
        d.setVarianceReason(rs.getString("VarianceReason"));
        d.setNotes(rs.getString("Notes"));
        try {
            d.setProductName(rs.getString("ProductName"));
        } catch (Exception ignored) {
        }
        try {
            d.setProductSku(rs.getString("SKU"));
        } catch (Exception ignored) {
        }
        return d;
    }
}
