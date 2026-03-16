/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import entity.Supplier;
import java.sql.Timestamp;

/**
 *
 * @author qp
 */
public class SupplierDAO extends DBContext {

    PreparedStatement stm;
    ResultSet rs;

    //view
    public List<Supplier> getAllSuppliers() {
        List<Supplier> lists = new ArrayList<>();

        String sql = "select * from Suppliers";
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                lists.add(extractSupplierFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getAllSuppliers: " + e.getMessage());
        }
        return lists;
    }

    public Supplier getSupplierByCode(String supplierCode) {//'SUP-001'
        String sql = "select * from Suppliers where supplierCode = ?";
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            stm.setString(1, supplierCode);
            rs = stm.executeQuery();
            if (rs.next()) {
                return extractSupplierFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("ERR: getSupplier: " + e.getMessage());
        }
        return null;
    }

    public Supplier getSupplierById(int id) {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            stm.setInt(1, id);
            rs = stm.executeQuery();
            if (rs.next()) {
                return extractSupplierFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("ERR: getSupplierById: " + e.getMessage());
        }
        return null;
    }

    public List<Supplier> getAllActiveSuppliers() {
        String sql = "select SupplierID, SupplierName from Suppliers where isActive='true' order by SupplierName";
        List<Supplier> suppliers = new ArrayList<>();

        try {
            Connection connection = getConnection();
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Supplier s = new Supplier();
                s.setId(rs.getInt("SupplierID"));
                s.setSupplierName(rs.getString("SupplierName"));
                suppliers.add(s);
            }
        } catch (Exception e) {
            System.out.println("ERR: getAllActiveSupplier: " + e.getMessage());
        }
        return suppliers;
    }

    //add
    public boolean addSupplier(Supplier sp) {
        String sql = """
                    INSERT INTO Suppliers (SupplierCode, SupplierName, ContactPerson, Phone, Email, Address) 
        VALUES(?,?,?,?,?,?)
                    """;
        try {
            Connection connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, sp.getSupplierCode());
            stm.setString(2, sp.getSupplierName());
            stm.setString(3, sp.getContactPerson());
            stm.setString(4, sp.getPhone());
            stm.setString(5, sp.getEmail());
            stm.setString(6, sp.getAddress());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: Add :" + e.getMessage());
        }
        return false;
    }

    //update
    public boolean updateSupplier(Supplier sp) {
        String sql = """
                UPDATE Suppliers 
                SET SupplierName = ?, ContactPerson = ?, Phone = ?, Email = ?, Address = ?, UpdatedAt = GETDATE()
                WHERE SupplierCode = ?
                """;
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            stm.setString(1, sp.getSupplierName());
            stm.setString(2, sp.getContactPerson());
            stm.setString(3, sp.getPhone());
            stm.setString(4, sp.getEmail());
            stm.setString(5, sp.getAddress());
            stm.setString(6, sp.getSupplierCode());
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: updateSupplier: " + e.getMessage());
        }
        return false;
    }

    //delete
    public boolean deleteSupplier(String code) {
        String sql
                = """
                delete from Suppliers where supplierCode = ?
                """;
        try {
            Connection connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, code);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: Delete :" + e.getMessage());
        }
        return false;
    }

    //deactive
    public boolean deactiveSupplier(String code) {
        String sql
                = """
                update Suppliers set IsActive = 0, UpdatedAt = GETDATE()
                  where supplierCode = ?
                """;
        try {
            Connection connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, code);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: Deactive :" + e.getMessage());
        }
        return false;
    }
    //active

    public boolean activeSupplier(String code) {
        String sql
                = """
                update Suppliers set IsActive = 1, UpdatedAt = GETDATE()
                  where supplierCode = ?
                """;
        try {
            Connection connection = getConnection();
            stm = connection.prepareStatement(sql);
            stm.setString(1, code);
            return stm.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: Active :" + e.getMessage());
        }
        return false;
    }

    public List<Supplier> searchSuppliers(String keyword, Boolean isActive) {
        List<Supplier> lists = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select * from Suppliers where 1 = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (supplierCode like ? OR SupplierName like ? OR ContactPerson like ?) ");
        }
        if (isActive != null) {
            sql.append("AND IsActive = ? ");
        }
        sql.append("order by supplierID desc ");
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
            }
            if (isActive != null) {
                stm.setBoolean(paramIndex++, isActive);
            }

            rs = stm.executeQuery();
            while (rs.next()) {
                lists.add(extractSupplierFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: searchSuppliers: " + e.getMessage());

        }
        return lists;
    }

    public List<Supplier> searchSuppliersWithPaginated(String keyword, Boolean isActive, int page, int pageSize) {
        List<Supplier> lists = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        StringBuilder sql = new StringBuilder();
        sql.append("select * from Suppliers where 1 = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (supplierCode like ? OR SupplierName like ? OR ContactPerson like ?) ");
        }
        if (isActive != null) {
            sql.append("AND IsActive = ? ");
        }
        sql.append("order by supplierID desc ");
        sql.append("offset ? rows fetch next ? rows only");
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
            }
            if (isActive != null) {
                stm.setBoolean(paramIndex++, isActive);
            }

            stm.setInt(paramIndex++, offset);
            stm.setInt(paramIndex++, pageSize);

            rs = stm.executeQuery();
            while (rs.next()) {
                lists.add(extractSupplierFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: searchSuppliersWithPagination: " + e.getMessage());

        }
        return lists;
    }

    //count (for pagination)
    public int countSuppliers() {
        String sql = "Select COUNT(*) from Suppliers ";
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: countSuppliers: " + e.getMessage());
        }
        return 0;
    }

    //count by conditions
    public int countSuppliers(String keyword, Boolean isActive) {
        StringBuilder sql = new StringBuilder();
        sql.append("select COUNT(*) from Suppliers where 1 = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (supplierCode like ? OR SupplierName like ? OR ContactPerson like ?) ");
        }
        if (isActive != null) {
            sql.append("AND IsActive = ? ");
        }
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql.toString());
            int paramIndex = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword + "%";
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
                stm.setString(paramIndex++, searchPattern);
            }
            if (isActive != null) {
                stm.setBoolean(paramIndex++, isActive);
            }

            rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: countSuppliers: " + e.getMessage());
        }
        return 0;
    }

    //check exist
    public boolean isCodeExist(String code) {
        String sql = "select 1 from Suppliers where SupplierCode = ?";
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql);
            stm.setString(1, code);
            rs = stm.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("ERR: isSupplierCodeExists: " + e.getMessage());
        }
        return false;
    }

    public String generateNextSupplierCode() {
        String sql = "select MAX(SupplierCode) from Suppliers where SupplierCode like 'SUP-%'";
        try {
            Connection connection = getConnection();

            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            if (rs.next()) {
                String maxCode = rs.getString(1);
                if (maxCode != null) {
                    //SUP-001
                    int num = Integer.parseInt(maxCode.substring(4)) + 1;
                    return String.format("SUP-%03d", num);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: generateNextSupplierCode: " + e.getMessage());
        }
        return "SUP-001";
    }

    //BR
    public boolean canDeleteSupplier(String supplierCode) {
        String sql = """
                SELECT COUNT(*) FROM PurchaseOrders po
                JOIN Suppliers s ON po.SupplierID = s.SupplierID
                WHERE s.SupplierCode = ?
                """;
        try (Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, supplierCode);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: canDeleteSupplier: " + e.getMessage());
        }
        return false;
    }

    public boolean canLockSupplier(String supplierCode) {
        return getBlockingOrdersCount(supplierCode) == 0;
    }
    
    public int getBlockingOrdersCount(String supplierCode) {
        String sql = """
                SELECT COUNT(*) FROM PurchaseOrders po
                JOIN Suppliers s ON po.SupplierID = s.SupplierID
                WHERE s.SupplierCode = ?
                AND po.Status IN ('PENDING_APPROVAL', 'APPROVED', 'PARTIAL_RECEIVED')
                """;
        try (Connection conn = getConnection(); PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1, supplierCode);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("ERR: getBlockingOrdersCount: " + e.getMessage());
        }
        return 0;
    }
    
    private Supplier extractSupplierFromResultSet(ResultSet rs) throws Exception {
        Supplier sup = new Supplier();
        sup.setId(rs.getInt("SupplierID"));
        sup.setSupplierCode(rs.getString("SupplierCode"));
        sup.setSupplierName(rs.getString("SupplierName"));
        sup.setContactPerson(rs.getString("ContactPerson"));
        sup.setPhone(rs.getString("Phone"));
        sup.setEmail(rs.getString("Email"));
        sup.setAddress(rs.getString("Address"));
        sup.setIsActive(rs.getBoolean("IsActive"));
        Timestamp timeStamp = rs.getTimestamp("CreatedAt");
        if (timeStamp != null) {
            LocalDateTime completedAt = timeStamp.toLocalDateTime();
            sup.setCreatedAt(completedAt);
        }

        timeStamp = rs.getTimestamp("UpdatedAt");
        if (timeStamp != null) {
            LocalDateTime updatedAt = timeStamp.toLocalDateTime();
            sup.setCreatedAt(updatedAt);
        }
        return sup;
    }
}
