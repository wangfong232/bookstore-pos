/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.InventoryTransaction;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qp
 */
public class InventoryTransactionDAO extends DBContext {

    public List<InventoryTransaction> searchTransactionWithPaginated(String keyword, String transactionType, Integer productId,
            LocalDate from, LocalDate to, int page, int pageSize) {
        List<InventoryTransaction> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
                select t.*, 
                       p.ProductName, p.SKU,
                       e.FullName AS CreatedByName
                from InventoryTransactions t
                left join Products p on t.ProductID = p.ProductID
                left join Employees e on t.CreatedBy = e.EmployeeID
                where 1=1
                """);

        appendFilters(sql, keyword, transactionType, productId, from, to);
        sql.append(" ORDER BY t.TransactionDate DESC, t.TransactionID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql.toString())) {
            int idx = bindFilters(stm, 1, keyword, transactionType, productId, from, to);
            stm.setInt(idx++, offset);
            stm.setInt(idx, pageSize);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractFromRS(rs));
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: searchTransactionWithPaginated: " + e.getMessage());
        }
        return list;
    }

    public int count(String keyword, String transactionType, Integer productId, LocalDate from, LocalDate to) {
        StringBuilder sql = new StringBuilder("""
                select COUNT(*) 
                from InventoryTransactions t
                left join Products p on t.ProductID = p.ProductID
                where 1=1
                """);
        appendFilters(sql, keyword, transactionType, productId, from, to);

        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql.toString())) {
            bindFilters(stm, 1, keyword, transactionType, productId, from, to);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: countTransaction: " + e.getMessage());
        }
        return 0;
    }

    public InventoryTransaction getTransactionById(long id) {
        String sql = """
                select t.*, 
                       p.ProductName, p.SKU,
                       e.FullName AS CreatedByName
                from InventoryTransactions t
                left join Products p on t.ProductID = p.ProductID
                left join Employees e on t.CreatedBy = e.EmployeeID
                where t.TransactionID = ?
                """;

        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql)) {
            stm.setLong(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return extractFromRS(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getTransactionById: " + e.getMessage());
        }
        return null;
    }

    public boolean insertTransaction(InventoryTransaction tx) {
        String sql = """
                insert into InventoryTransactions
                (ProductID, TransactionType, ReferenceType, ReferenceID, ReferenceCode,
                 QuantityChange, StockBefore, StockAfter, UnitCost, Notes, CreatedBy, TransactionDate)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = getConnection(); PreparedStatement stm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stm.setInt(1, tx.getProductId());
            stm.setString(2, tx.getTransactionType());
            stm.setString(3, tx.getReferenceType());
            stm.setObject(4, tx.getReferenceId());
            stm.setString(5, tx.getReferenceCode());
            stm.setInt(6, tx.getQuantityChange());
            stm.setInt(7, tx.getStockBefore());
            stm.setInt(8, tx.getStockAfter());
            stm.setBigDecimal(9, tx.getUnitCost());
            stm.setString(10, tx.getNotes());
            stm.setInt(11, tx.getCreatedBy());
            stm.setTimestamp(12, Timestamp.valueOf(
                    tx.getTransactionDate() != null ? tx.getTransactionDate() : LocalDateTime.now()));

            int affectedRows = stm.executeUpdate();
            if(affectedRows>0){
                try(ResultSet keys = stm.getGeneratedKeys()){
                    if(keys.next()){
                        tx.setTransactionId(keys.getLong(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            System.out.println("ERR: insertTransaction: " + e.getMessage());
        }
        return false;
    }

    private void appendFilters(StringBuilder sql, String keyword, String transactionType, Integer productId,
            LocalDate from, LocalDate to) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (t.ReferenceCode LIKE ? OR t.Notes LIKE ? OR p.ProductName LIKE ? OR p.SKU LIKE ?)");
        }
        if (transactionType != null && !transactionType.trim().isEmpty()) {
            sql.append(" AND t.TransactionType = ?");
        }
        if (productId != null && productId > 0) {
            sql.append(" AND t.ProductID = ?");
        }
        if (from != null) {
            sql.append(" AND CAST(t.TransactionDate AS DATE) >= ?");
        }
        if (to != null) {
            sql.append(" AND CAST(t.TransactionDate AS DATE) <= ?");
        }
    }

    private int bindFilters(PreparedStatement stm, int idx, String keyword, String transactionType,
            Integer productId, LocalDate from, LocalDate to) throws SQLException {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String pattern = "%" + keyword.trim() + "%";
            stm.setString(idx++, pattern);
            stm.setString(idx++, pattern);
            stm.setString(idx++, pattern);
            stm.setString(idx++, pattern);
        }
        if (transactionType != null && !transactionType.trim().isEmpty()) {
            stm.setString(idx++, transactionType);
        }
        if (productId != null && productId > 0) {
            stm.setInt(idx++, productId);
        }
        if (from != null) {
            stm.setDate(idx++, Date.valueOf(from));
        }
        if (to != null) {
            stm.setDate(idx++, Date.valueOf(to));
        }
        return idx;
    }
    
    private InventoryTransaction extractFromRS(ResultSet rs) throws SQLException {
        InventoryTransaction t = new InventoryTransaction();
        t.setTransactionId(rs.getLong("TransactionID"));
        t.setProductId(rs.getInt("ProductID"));
        t.setTransactionDate(getLocalDateTime(rs, "TransactionDate"));
        t.setTransactionType(rs.getString("TransactionType"));
        t.setReferenceType(rs.getString("ReferenceType"));
        t.setReferenceId(rs.getLong("ReferenceID"));
        t.setReferenceCode(rs.getString("ReferenceCode"));
        t.setQuantityChange(rs.getInt("QuantityChange"));
        t.setStockBefore(rs.getInt("StockBefore"));
        t.setStockAfter(rs.getInt("StockAfter"));

        BigDecimal uc = rs.getBigDecimal("UnitCost");
        t.setUnitCost(uc != null ? uc : BigDecimal.ZERO);

        t.setNotes(rs.getString("Notes"));
        t.setCreatedBy(rs.getInt("CreatedBy"));

        try {
            t.setProductName(rs.getString("ProductName"));
        } catch (Exception ignored) {
        }
        try {
            t.setProductSku(rs.getString("SKU"));
        } catch (Exception ignored) {
        }
        try {
            t.setCreatedByName(rs.getString("CreatedByName"));
        } catch (Exception ignored) {
        }

        return t;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
