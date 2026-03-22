package DAO;

import entity.ComboProduct;
import entity.ComboProductItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComboProductDAO extends DBContext {

    // ==================== READ ====================

    /**
     * Get all combos with product info (for list page)
     */
    public List<ComboProduct> getAllCombos() {
        List<ComboProduct> list = new ArrayList<>();
        String sql = """
            SELECT cp.ComboID, cp.ProductID, cp.ComboQuantity, cp.IsActive,
                   cp.CreatedDate, cp.UpdatedDate,
                   p.ProductName, p.SKU, p.SellingPrice, p.ImageURL
            FROM ComboProducts cp
            JOIN Products p ON cp.ProductID = p.ProductID
            ORDER BY cp.CreatedDate DESC
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ComboProduct combo = extractComboFromResultSet(rs);
                combo.setComboItems(getComboItems(combo.getComboID()));
                list.add(combo);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getAllCombos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Get combo by ComboID
     */
    public ComboProduct getComboByID(int comboID) {
        String sql = """
            SELECT cp.ComboID, cp.ProductID, cp.ComboQuantity, cp.IsActive,
                   cp.CreatedDate, cp.UpdatedDate,
                   p.ProductName, p.SKU, p.SellingPrice, p.ImageURL
            FROM ComboProducts cp
            JOIN Products p ON cp.ProductID = p.ProductID
            WHERE cp.ComboID = ?
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ComboProduct combo = extractComboFromResultSet(rs);
                combo.setComboItems(getComboItems(comboID));
                return combo;
            }
        } catch (SQLException e) {
            System.out.println("ERR: getComboByID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get combo by ProductID
     */
    public ComboProduct getComboByProductID(int productID) {
        String sql = """
            SELECT cp.ComboID, cp.ProductID, cp.ComboQuantity, cp.IsActive,
                   cp.CreatedDate, cp.UpdatedDate,
                   p.ProductName, p.SKU, p.SellingPrice, p.ImageURL
            FROM ComboProducts cp
            JOIN Products p ON cp.ProductID = p.ProductID
            WHERE cp.ProductID = ?
        """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ComboProduct combo = extractComboFromResultSet(rs);
                combo.setComboItems(getComboItems(combo.getComboID()));
                return combo;
            }
        } catch (SQLException e) {
            System.out.println("ERR: getComboByProductID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get combo items (child products) for a combo
     */
    public List<ComboProductItem> getComboItems(int comboID) {
        List<ComboProductItem> list = new ArrayList<>();
        String sql = """
            SELECT ci.ComboItemID, ci.ComboID, ci.ChildProductID, ci.Quantity,
                   p.ProductName AS ChildProductName, p.SKU AS ChildProductSku,
                   p.Stock AS ChildProductStock
            FROM ComboProductItems ci
            JOIN Products p ON ci.ChildProductID = p.ProductID
            WHERE ci.ComboID = ?
            ORDER BY ci.ComboItemID
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comboID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ComboProductItem item = new ComboProductItem();
                item.setComboItemID(rs.getInt("ComboItemID"));
                item.setComboID(rs.getInt("ComboID"));
                item.setChildProductID(rs.getInt("ChildProductID"));
                item.setQuantity(rs.getInt("Quantity"));
                item.setChildProductName(rs.getString("ChildProductName"));
                item.setChildProductSku(rs.getString("ChildProductSku"));
                item.setChildProductStock(rs.getInt("ChildProductStock"));
                list.add(item);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getComboItems: " + e.getMessage());
        }
        return list;
    }

    // ==================== CREATE ====================

    /**
     * Create a new combo: insert into ComboProducts + ComboProductItems,
     * mark the product as IsCombo=1, and deduct stock from child products.
     * Uses transaction to ensure consistency.
     */
    public boolean createCombo(int productID, int comboQuantity, List<ComboProductItem> items) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // 1. Mark product as combo
            String updateProductSql = "UPDATE Products SET IsCombo = 1 WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateProductSql)) {
                ps.setInt(1, productID);
                ps.executeUpdate();
            }

            // 2. Insert into ComboProducts
            String insertComboSql = "INSERT INTO ComboProducts (ProductID, ComboQuantity, IsActive) VALUES (?, ?, 1)";
            int comboID;
            try (PreparedStatement ps = conn.prepareStatement(insertComboSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productID);
                ps.setInt(2, comboQuantity);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    comboID = keys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Insert combo items + deduct stock from child products
            String insertItemSql = "INSERT INTO ComboProductItems (ComboID, ChildProductID, Quantity) VALUES (?, ?, ?)";
            String deductStockSql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ? AND Stock >= ?";

            for (ComboProductItem item : items) {
                // Insert item
                try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                    ps.setInt(1, comboID);
                    ps.setInt(2, item.getChildProductID());
                    ps.setInt(3, item.getQuantity());
                    ps.executeUpdate();
                }

                // Deduct stock: quantity_in_combo * comboQuantity
                int totalDeduct = item.getQuantity() * comboQuantity;
                try (PreparedStatement ps = conn.prepareStatement(deductStockSql)) {
                    ps.setInt(1, totalDeduct);
                    ps.setInt(2, item.getChildProductID());
                    ps.setInt(3, totalDeduct);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        // Not enough stock
                        conn.rollback();
                        return false;
                    }
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("ERR: createCombo: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
        return false;
    }

    // ==================== UPDATE ====================

    /**
     * Update combo: change child products and their quantities.
     * Steps (all in one transaction):
     *   1. Return stock from OLD child items (oldQty × comboQuantity)
     *   2. Delete old ComboProductItems
     *   3. Insert new ComboProductItems
     *   4. Deduct stock for NEW child items (newQty × comboQuantity)
     *   5. Update product info (name, SKU, price, description)
     */
    public boolean updateCombo(int comboID, int productID, String comboName, String comboSku,
                                double sellingPrice, String description,
                                List<ComboProductItem> newItems) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get current combo quantity
            int comboQuantity = 0;
            String getQtySql = "SELECT ComboQuantity FROM ComboProducts WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(getQtySql)) {
                ps.setInt(1, comboID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    comboQuantity = rs.getInt("ComboQuantity");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // 1. Return stock from OLD child items
            String getOldItemsSql = "SELECT ChildProductID, Quantity FROM ComboProductItems WHERE ComboID = ?";
            String addStockSql = "UPDATE Products SET Stock = Stock + ? WHERE ProductID = ?";
            try (PreparedStatement psItems = conn.prepareStatement(getOldItemsSql)) {
                psItems.setInt(1, comboID);
                ResultSet rs = psItems.executeQuery();
                while (rs.next()) {
                    int childProductID = rs.getInt("ChildProductID");
                    int itemQty = rs.getInt("Quantity");
                    int returnStock = itemQty * comboQuantity;
                    try (PreparedStatement psStock = conn.prepareStatement(addStockSql)) {
                        psStock.setInt(1, returnStock);
                        psStock.setInt(2, childProductID);
                        psStock.executeUpdate();
                    }
                }
            }

            // 2. Delete old ComboProductItems
            String deleteItemsSql = "DELETE FROM ComboProductItems WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, comboID);
                ps.executeUpdate();
            }

            // 3. Insert new ComboProductItems + 4. Deduct stock
            String insertItemSql = "INSERT INTO ComboProductItems (ComboID, ChildProductID, Quantity) VALUES (?, ?, ?)";
            String deductStockSql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ? AND Stock >= ?";
            for (ComboProductItem item : newItems) {
                try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                    ps.setInt(1, comboID);
                    ps.setInt(2, item.getChildProductID());
                    ps.setInt(3, item.getQuantity());
                    ps.executeUpdate();
                }

                int totalDeduct = item.getQuantity() * comboQuantity;
                if (totalDeduct > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(deductStockSql)) {
                        ps.setInt(1, totalDeduct);
                        ps.setInt(2, item.getChildProductID());
                        ps.setInt(3, totalDeduct);
                        int rows = ps.executeUpdate();
                        if (rows == 0) {
                            conn.rollback();
                            return false; // Not enough stock
                        }
                    }
                }
            }

            // 5. Update product info
            String updateProductSql = "UPDATE Products SET ProductName = ?, SKU = ?, SellingPrice = ?, Description = ? WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateProductSql)) {
                ps.setString(1, comboName);
                ps.setString(2, comboSku);
                ps.setDouble(3, sellingPrice);
                ps.setString(4, description);
                ps.setInt(5, productID);
                ps.executeUpdate();
            }

            // Update combo timestamp
            String updateComboSql = "UPDATE ComboProducts SET UpdatedDate = GETDATE() WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateComboSql)) {
                ps.setInt(1, comboID);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("ERR: updateCombo: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
        return false;
    }

    // ==================== ADJUST QUANTITY ====================

    /**
     * Adjust combo quantity by delta (+N or -N).
     * Positive delta = add combos (deduct child stock).
     * Negative delta = remove combos (return child stock).
     */
    public boolean adjustComboQuantity(int comboID, int delta) {
        if (delta == 0) return true;

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get combo items
            List<ComboProductItem> items = new ArrayList<>();
            String getItemsSql = "SELECT ChildProductID, Quantity FROM ComboProductItems WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(getItemsSql)) {
                ps.setInt(1, comboID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ComboProductItem item = new ComboProductItem();
                    item.setChildProductID(rs.getInt("ChildProductID"));
                    item.setQuantity(rs.getInt("Quantity"));
                    items.add(item);
                }
            }

            // Verify current combo qty is enough for negative delta
            if (delta < 0) {
                String checkQtySql = "SELECT ComboQuantity FROM ComboProducts WHERE ComboID = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkQtySql)) {
                    ps.setInt(1, comboID);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int currentQty = rs.getInt("ComboQuantity");
                        if (currentQty + delta < 0) {
                            conn.rollback();
                            return false; // Can't go below 0
                        }
                    }
                }
            }

            // Update child product stock
            for (ComboProductItem item : items) {
                int stockChange = item.getQuantity() * Math.abs(delta);

                if (delta > 0) {
                    // Adding combos → deduct child stock
                    String deductSql = "UPDATE Products SET Stock = Stock - ? WHERE ProductID = ? AND Stock >= ?";
                    try (PreparedStatement ps = conn.prepareStatement(deductSql)) {
                        ps.setInt(1, stockChange);
                        ps.setInt(2, item.getChildProductID());
                        ps.setInt(3, stockChange);
                        int rows = ps.executeUpdate();
                        if (rows == 0) {
                            conn.rollback();
                            return false; // Not enough stock
                        }
                    }
                } else {
                    // Removing combos → return child stock
                    String addSql = "UPDATE Products SET Stock = Stock + ? WHERE ProductID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(addSql)) {
                        ps.setInt(1, stockChange);
                        ps.setInt(2, item.getChildProductID());
                        ps.executeUpdate();
                    }
                }
            }

            // Update combo quantity
            String updateQtySql = "UPDATE ComboProducts SET ComboQuantity = ComboQuantity + ?, UpdatedDate = GETDATE() WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateQtySql)) {
                ps.setInt(1, delta);
                ps.setInt(2, comboID);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("ERR: adjustComboQuantity: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
        return false;
    }

    // ==================== DELETE (DECOMPOSE) ====================

    /**
     * Delete combo: remove combo records, unmark product as combo,
     * and return all stock to child products.
     */
    public boolean deleteCombo(int comboID) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            // Get combo info
            int productID = 0;
            int comboQuantity = 0;
            String getComboSql = "SELECT ProductID, ComboQuantity FROM ComboProducts WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(getComboSql)) {
                ps.setInt(1, comboID);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    productID = rs.getInt("ProductID");
                    comboQuantity = rs.getInt("ComboQuantity");
                } else {
                    conn.rollback();
                    return false;
                }
            }

            // Return stock to child products
            String getItemsSql = "SELECT ChildProductID, Quantity FROM ComboProductItems WHERE ComboID = ?";
            String addStockSql = "UPDATE Products SET Stock = Stock + ? WHERE ProductID = ?";
            try (PreparedStatement psItems = conn.prepareStatement(getItemsSql)) {
                psItems.setInt(1, comboID);
                ResultSet rs = psItems.executeQuery();
                while (rs.next()) {
                    int childProductID = rs.getInt("ChildProductID");
                    int itemQty = rs.getInt("Quantity");
                    int returnStock = itemQty * comboQuantity;

                    try (PreparedStatement psStock = conn.prepareStatement(addStockSql)) {
                        psStock.setInt(1, returnStock);
                        psStock.setInt(2, childProductID);
                        psStock.executeUpdate();
                    }
                }
            }

            // Delete combo items
            String deleteItemsSql = "DELETE FROM ComboProductItems WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteItemsSql)) {
                ps.setInt(1, comboID);
                ps.executeUpdate();
            }

            // Delete combo
            String deleteComboSql = "DELETE FROM ComboProducts WHERE ComboID = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteComboSql)) {
                ps.setInt(1, comboID);
                ps.executeUpdate();
            }

            // Unmark product as combo
            String updateProductSql = "UPDATE Products SET IsCombo = 0 WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateProductSql)) {
                ps.setInt(1, productID);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("ERR: deleteCombo: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
        return false;
    }

    /**
     * Decrease combo quantity when sold at POS.
     * Does NOT return stock to child products (stock was already deducted when combo was created).
     */
    public boolean decreaseComboQuantity(int productID, int quantity) {
        String sql = "UPDATE ComboProducts SET ComboQuantity = ComboQuantity - ?, UpdatedDate = GETDATE() " +
                     "WHERE ProductID = ? AND ComboQuantity >= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productID);
            ps.setInt(3, quantity);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("ERR: decreaseComboQuantity: " + e.getMessage());
        }
        return false;
    }

    // ==================== HELPER ====================

    private ComboProduct extractComboFromResultSet(ResultSet rs) throws SQLException {
        ComboProduct combo = new ComboProduct();
        combo.setComboID(rs.getInt("ComboID"));
        combo.setProductID(rs.getInt("ProductID"));
        combo.setComboQuantity(rs.getInt("ComboQuantity"));
        combo.setIsActive(rs.getBoolean("IsActive"));
        combo.setCreatedDate(rs.getTimestamp("CreatedDate"));
        combo.setUpdatedDate(rs.getTimestamp("UpdatedDate"));
        combo.setProductName(rs.getString("ProductName"));
        combo.setProductSku(rs.getString("SKU"));
        combo.setSellingPrice(rs.getDouble("SellingPrice"));
        combo.setImageURL(rs.getString("ImageURL"));
        return combo;
    }
}
