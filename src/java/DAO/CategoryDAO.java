/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DAO;

import entity.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends DBContext {
     private PreparedStatement stm;
    private ResultSet rs;
    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY DisplayOrder, CategoryName";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("CategoryID"),
                    rs.getString("CategoryName"),
                    rs.getString("Description"),
                    rs.getString("Icon"),
                    rs.getInt("DisplayOrder"),
                    rs.getBoolean("IsActive")
                );
                list.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Get categories with search, filter, sort and paging
    public List<Category> getCategories(String search, Boolean isActive, String sortBy, String sortOrder, int page, int pageSize) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Categories WHERE 1=1");
        
        // Search
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (CategoryName LIKE ? OR Description LIKE ?)");
        }
        
        // Filter by status
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        
        // Sort
        if (sortBy != null && !sortBy.isEmpty()) {
            sql.append(" ORDER BY ").append(sortBy);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        } else {
            sql.append(" ORDER BY DisplayOrder, CategoryName");
        }
        
        // Paging
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            // Set search parameters
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            // Set filter parameters
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            
            // Set paging parameters
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category category = new Category(
                    rs.getInt("CategoryID"),
                    rs.getString("CategoryName"),
                    rs.getString("Description"),
                    rs.getString("Icon"),
                    rs.getInt("DisplayOrder"),
                    rs.getBoolean("IsActive")
                );
                list.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Get total count for pagination
    public int getTotalCategories(String search, Boolean isActive) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Categories WHERE 1=1");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (CategoryName LIKE ? OR Description LIKE ?)");
        }
        
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get category by ID
    public Category getCategoryByID(int id) {
        String sql = "SELECT * FROM Categories WHERE CategoryID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(
                    rs.getInt("CategoryID"),
                    rs.getString("CategoryName"),
                    rs.getString("Description"),
                    rs.getString("Icon"),
                    rs.getInt("DisplayOrder"),
                    rs.getBoolean("IsActive")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Insert new category
    public boolean insertCategory(Category category) {
        String sql = "INSERT INTO Categories (CategoryName, Description, Icon, DisplayOrder, IsActive) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getIcon());
            ps.setInt(4, category.getDisplayOrder());
            ps.setBoolean(5, category.isIsActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Update category
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET CategoryName = ?, Description = ?, Icon = ?, DisplayOrder = ?, IsActive = ? WHERE CategoryID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.getCategoryName());
            ps.setString(2, category.getDescription());
            ps.setString(3, category.getIcon());
            ps.setInt(4, category.getDisplayOrder());
            ps.setBoolean(5, category.isIsActive());
            ps.setInt(6, category.getCategoryID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete category
    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM Categories WHERE CategoryID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Check if category name exists (for duplicate validation)
    public boolean isCategoryNameExists(String categoryName, Integer excludeCategoryID) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Categories WHERE CategoryName = ?");
        
        // Exclude current category when updating
        if (excludeCategoryID != null) {
            sql.append(" AND CategoryID != ?");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, categoryName);
            
            if (excludeCategoryID != null) {
                ps.setInt(2, excludeCategoryID);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
     public List<Category> getAllActiveCategories() {
        List<Category> list = new ArrayList<>();
        String sql = """
                     SELECT CategoryID, CategoryName, Description, Icon, DisplayOrder, IsActive
                     FROM Categories
                     WHERE IsActive = 1
                     ORDER BY DisplayOrder ASC, CategoryID ASC
                     """;
        try {
            Connection conn = getConnection();
            stm = conn.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(extractCategoryFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getAllActiveCategories: " + e.getMessage());
        }
        return list;
    }

    private Category extractCategoryFromResultSet(ResultSet rs) throws Exception {
        Category c = new Category();
        c.setCategoryID(rs.getInt("CategoryID"));
        c.setCategoryName(rs.getString("CategoryName"));
        c.setDescription(rs.getString("Description"));
        c.setIcon(rs.getString("Icon"));
        c.setDisplayOrder(rs.getObject("DisplayOrder") != null ? rs.getInt("DisplayOrder") : null);
        c.setIsActive(rs.getBoolean("IsActive"));
        return c;
    }
    
}

