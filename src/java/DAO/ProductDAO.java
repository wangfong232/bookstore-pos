/*
 * Product DAO for POS module and Product Management
 */
package DAO;
     
import entity.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends DBContext {

    private PreparedStatement stm;
    private ResultSet rs;

    // Used by PurchaseOrderController and elsewhere
    public List<Product> getAllActiveProducts() {
        List<Product> products = new ArrayList<>();

        String sql = "SELECT ProductID, ProductName, SellingPrice "
                   + "FROM Products "
                   + "WHERE IsActive = 1 "
                   + "ORDER BY ProductName";
        try (Connection connection = getConnection()) {
            if (connection == null) {
                return products;
            }
            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setSellingPrice(rs.getDouble("SellingPrice"));
                products.add(p);
            }
        } catch (Exception e) {
            System.out.println("ERR: getAllActiveProducts: " + e.getMessage());
        }
        return products;
    }

    // Used by POS (scan by SKU)
    public Product getProductBySku(String sku) {
        String sql = "SELECT TOP 1 * FROM Products WHERE SKU = ? AND IsActive = 1";
        try (Connection conn = getConnection()) {
            if (conn == null) {
                return null;
            }
            stm = conn.prepareStatement(sql);
            stm.setString(1, sku);
            rs = stm.executeQuery();
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("ERR: getProductBySku: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lấy sản phẩm từ DB: lọc theo từ khóa (tên/SKU) và/hoặc CategoryID, giới hạn số dòng.
     *
     * @param keyword    từ khóa tìm kiếm (null hoặc rỗng = không lọc theo tên/SKU)
     * @param categoryId ID danh mục (null = tất cả danh mục)
     * @param limit      số sản phẩm tối đa
     */
    public List<Product> getProducts(String keyword, Integer categoryId, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM Products WHERE IsActive = 1 ");
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (ProductName LIKE ? OR SKU LIKE ?) ");
        }
        if (categoryId != null) {
            sql.append("AND CategoryID = ? ");
        }
        sql.append("ORDER BY ProductID DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY");
        try (Connection conn = getConnection()) {
            if (conn == null) {
                return list;
            }
            stm = conn.prepareStatement(sql.toString());
            int idx = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim() + "%";
                stm.setString(idx++, pattern);
                stm.setString(idx++, pattern);
            }
            if (categoryId != null) {
                stm.setInt(idx++, categoryId);
            }
            stm.setInt(idx, limit);
            rs = stm.executeQuery();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getProducts: " + e.getMessage());
        }
        return list;
    }

    private Product extractProductFromResultSet(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("ProductID"));
        p.setProductName(rs.getString("ProductName"));
        p.setCategoryId(rs.getInt("CategoryID"));
        // BrandID, SupplierID may be nullable
        p.setBrandId(rs.getObject("BrandID") != null ? rs.getInt("BrandID") : null);
        p.setSupplierId(rs.getObject("SupplierID") != null ? rs.getInt("SupplierID") : null);
        p.setSku(rs.getString("SKU"));
        p.setDescription(rs.getString("Description"));
        p.setSpecifications(rs.getString("Specifications"));
        p.setImageURL(rs.getString("ImageURL"));
        p.setCostPrice(rs.getObject("CostPrice") != null ? rs.getDouble("CostPrice") : null);
        p.setSellingPrice(rs.getDouble("SellingPrice"));
        p.setCompareAtPrice(rs.getObject("CompareAtPrice") != null ? rs.getDouble("CompareAtPrice") : null);
        p.setStock(rs.getInt("Stock"));
        p.setReorderLevel(rs.getInt("ReorderLevel"));
        p.setActive(rs.getBoolean("IsActive"));
        p.setCreatedDate(rs.getTimestamp("CreatedDate"));
        p.setUpdatedDate(rs.getTimestamp("UpdatedDate"));
        return p;
    }
    
    // Get products with search, filter, sort and paging for admin
    public List<Product> getProducts(String search, Boolean isActive, Integer categoryId, Integer brandId, 
                                     String sortBy, String sortOrder, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE 1=1");
        
        // Search
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (ProductName LIKE ? OR SKU LIKE ? OR Description LIKE ?)");
        }
        
        // Filter by status
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        
        // Filter by category
        if (categoryId != null) {
            sql.append(" AND CategoryID = ?");
        }
        
        // Filter by brand
        if (brandId != null) {
            sql.append(" AND BrandID = ?");
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
            sql.append(" ORDER BY ProductID DESC");
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
                ps.setString(paramIndex++, searchPattern);
            }
            
            // Set filter parameters
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            
            if (categoryId != null) {
                ps.setInt(paramIndex++, categoryId);
            }
            
            if (brandId != null) {
                ps.setInt(paramIndex++, brandId);
            }
            
            // Set paging parameters
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getProducts: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Get products with stock status filter (for public page)
    public List<Product> getProducts(String search, Boolean isActive, Integer categoryId, Integer brandId, 
                                     String stockStatus, String sortBy, String sortOrder, int page, int pageSize) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE 1=1");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (ProductName LIKE ? OR SKU LIKE ? OR Description LIKE ?)");
        }
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        if (categoryId != null) {
            sql.append(" AND CategoryID = ?");
        }
        if (brandId != null) {
            sql.append(" AND BrandID = ?");
        }
        // Stock status filter
        if ("in_stock".equals(stockStatus)) {
            sql.append(" AND Stock > 0");
        } else if ("out_of_stock".equals(stockStatus)) {
            sql.append(" AND Stock <= 0");
        }
        
        if (sortBy != null && !sortBy.isEmpty()) {
            sql.append(" ORDER BY ").append(sortBy);
            if (sortOrder != null && sortOrder.equalsIgnoreCase("DESC")) {
                sql.append(" DESC");
            } else {
                sql.append(" ASC");
            }
        } else {
            sql.append(" ORDER BY ProductID DESC");
        }
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            if (categoryId != null) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (brandId != null) {
                ps.setInt(paramIndex++, brandId);
            }
            ps.setInt(paramIndex++, (page - 1) * pageSize);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractProductFromResultSet(rs));
            }
        } catch (Exception e) {
            System.out.println("ERR: getProducts(stockStatus): " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    
    // Get total count with stock status filter (for public page)
    public int getTotalProducts(String search, Boolean isActive, Integer categoryId, Integer brandId, String stockStatus) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE 1=1");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (ProductName LIKE ? OR SKU LIKE ? OR Description LIKE ?)");
        }
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        if (categoryId != null) {
            sql.append(" AND CategoryID = ?");
        }
        if (brandId != null) {
            sql.append(" AND BrandID = ?");
        }
        if ("in_stock".equals(stockStatus)) {
            sql.append(" AND Stock > 0");
        } else if ("out_of_stock".equals(stockStatus)) {
            sql.append(" AND Stock <= 0");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            if (categoryId != null) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (brandId != null) {
                ps.setInt(paramIndex++, brandId);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: getTotalProducts(stockStatus): " + e.getMessage());
        }
        return 0;
    }
    
    // Get total count for pagination
    public int getTotalProducts(String search, Boolean isActive, Integer categoryId, Integer brandId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE 1=1");
        
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND (ProductName LIKE ? OR SKU LIKE ? OR Description LIKE ?)");
        }
        
        if (isActive != null) {
            sql.append(" AND IsActive = ?");
        }
        
        if (categoryId != null) {
            sql.append(" AND CategoryID = ?");
        }
        
        if (brandId != null) {
            sql.append(" AND BrandID = ?");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            
            if (isActive != null) {
                ps.setBoolean(paramIndex++, isActive);
            }
            
            if (categoryId != null) {
                ps.setInt(paramIndex++, categoryId);
            }
            
            if (brandId != null) {
                ps.setInt(paramIndex++, brandId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERR: getTotalProducts: " + e.getMessage());
        }
        return 0;
    }
    
    // Get product by ID
    public Product getProductByID(int id) {
        String sql = "SELECT * FROM Products WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (Exception e) {
            System.out.println("ERR: getProductByID: " + e.getMessage());
        }
        return null;
    }
    
    // Insert new product
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO Products (ProductName, CategoryID, BrandID, SupplierID, SKU, Description, " +
                     "Specifications, ImageURL, CostPrice, SellingPrice, CompareAtPrice, Stock, ReorderLevel, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategoryId());
            
            if (product.getBrandId() != null) {
                ps.setInt(3, product.getBrandId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            if (product.getSupplierId() != null) {
                ps.setInt(4, product.getSupplierId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setString(5, product.getSku());
            ps.setString(6, product.getDescription());
            ps.setString(7, product.getSpecifications());
            ps.setString(8, product.getImageURL());
            
            if (product.getCostPrice() != null) {
                ps.setDouble(9, product.getCostPrice());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }
            
            ps.setDouble(10, product.getSellingPrice());
            
            if (product.getCompareAtPrice() != null) {
                ps.setDouble(11, product.getCompareAtPrice());
            } else {
                ps.setNull(11, Types.DECIMAL);
            }
            
            ps.setInt(12, product.getStock());
            ps.setInt(13, product.getReorderLevel());
            ps.setBoolean(14, product.isIsActive());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: insertProduct: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Update product
    public boolean updateProduct(Product product) {
        String sql = "UPDATE Products SET ProductName = ?, CategoryID = ?, BrandID = ?, SupplierID = ?, " +
                     "SKU = ?, Description = ?, Specifications = ?, ImageURL = ?, CostPrice = ?, " +
                     "SellingPrice = ?, CompareAtPrice = ?, Stock = ?, ReorderLevel = ?, IsActive = ? " +
                     "WHERE ProductID = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategoryId());
            
            if (product.getBrandId() != null) {
                ps.setInt(3, product.getBrandId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            
            if (product.getSupplierId() != null) {
                ps.setInt(4, product.getSupplierId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            ps.setString(5, product.getSku());
            ps.setString(6, product.getDescription());
            ps.setString(7, product.getSpecifications());
            ps.setString(8, product.getImageURL());
            
            if (product.getCostPrice() != null) {
                ps.setDouble(9, product.getCostPrice());
            } else {
                ps.setNull(9, Types.DECIMAL);
            }
            
            ps.setDouble(10, product.getSellingPrice());
            
            if (product.getCompareAtPrice() != null) {
                ps.setDouble(11, product.getCompareAtPrice());
            } else {
                ps.setNull(11, Types.DECIMAL);
            }
            
            ps.setInt(12, product.getStock());
            ps.setInt(13, product.getReorderLevel());
            ps.setBoolean(14, product.isIsActive());
            ps.setInt(15, product.getId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("ERR: updateProduct: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Check if SKU exists (for duplicate validation)
    public boolean isSkuExists(String sku, Integer excludeProductID) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Products WHERE SKU = ?");
        
        // Exclude current product when updating
        if (excludeProductID != null) {
            sql.append(" AND ProductID != ?");
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, sku);
            
            if (excludeProductID != null) {
                ps.setInt(2, excludeProductID);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.out.println("ERR: isSkuExists: " + e.getMessage());
        }
        return false;
    }
}
