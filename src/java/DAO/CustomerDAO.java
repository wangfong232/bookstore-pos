package DAO;

import entity.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

/*
*
*
*/
public class CustomerDAO extends DBContext {

    /**
     * Sinh mã khách hàng tiếp theo dạng KH001, KH002, ...
     */
    public String getNextCustomerId() {
        String sql = "SELECT MAX(CustomerID) FROM Customers WHERE CustomerID LIKE 'KH%'";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String maxId = rs.getString(1);
                if (maxId == null || maxId.isEmpty()) return "KH001";
                String numPart = maxId.length() > 2 ? maxId.substring(2) : "0";
                try {
                    int n = Integer.parseInt(numPart);
                    return String.format("KH%03d", n + 1);
                } catch (NumberFormatException e) {
                    return "KH001";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "KH001";
    }
    // CREATE
    public void insert(Customer c) {
        String sql = """
                    INSERT INTO Customers(CustomerID, FullName, Email, Birthday, Status, Note)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getCustomerID());
            ps.setString(2, c.getCustomerName());
            ps.setString(3, c.getEmail());
            ps.setDate(4, c.getBirthday() != null ? Date.valueOf(c.getBirthday()) : null);
            ps.setString(5, c.getStatus());
            ps.setString(6, c.getNote());
            ps.executeUpdate();

            // Also insert default points
            String pointSql = "INSERT INTO CustomerPoints(CustomerID, TotalPoints) VALUES (?, 0)";
            try (PreparedStatement ps2 = con.prepareStatement(pointSql)) {
                ps2.setString(1, c.getCustomerID());
                ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // READ by ID
    public Customer getById(String id) {
        String sql = """
                    SELECT c.*, p.TotalPoints
                    FROM Customers c
                    LEFT JOIN CustomerPoints p ON c.CustomerID = p.CustomerID
                    WHERE c.CustomerID = ?
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // READ by Email
    public Customer getByEmail(String email) {
        String sql = "SELECT * FROM Customers WHERE Email = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // READ ALL
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        String sql = """
                    SELECT c.*, p.TotalPoints
                    FROM Customers c
                    LEFT JOIN CustomerPoints p ON c.CustomerID = p.CustomerID
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // SEARCH
    public List<Customer> search(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = """
                    SELECT c.*, p.TotalPoints
                    FROM Customers c
                    LEFT JOIN CustomerPoints p ON c.CustomerID = p.CustomerID
                    WHERE c.CustomerID LIKE ? OR c.FullName LIKE ?
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE
    public void update(Customer c) {
        String sql = """
                    UPDATE Customers
                    SET FullName=?, Email=?, Birthday=?, Status=?, Note=?
                    WHERE CustomerID=?
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getCustomerName());
            ps.setString(2, c.getEmail());
            ps.setDate(3, c.getBirthday() != null ? Date.valueOf(c.getBirthday()) : null);
            ps.setString(4, c.getStatus());
            ps.setString(5, c.getNote());
            ps.setString(6, c.getCustomerID());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // DELETE
    public void delete(String id) {
        // Delete points first due to FK or logic
        String sqlPoints = "DELETE FROM CustomerPoints WHERE CustomerID=?";
        String sqlCust = "DELETE FROM Customers WHERE CustomerID=?";
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(sqlPoints);
                    PreparedStatement ps2 = con.prepareStatement(sqlCust)) {

                ps1.setString(1, id);
                ps1.executeUpdate();

                ps2.setString(1, id);
                ps2.executeUpdate();

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerID(rs.getString("CustomerID"));
        c.setCustomerName(rs.getString("FullName"));
        c.setEmail(rs.getString("Email"));

        Date birthday = rs.getDate("Birthday");
        if (birthday != null) {
            c.setBirthday(birthday.toLocalDate());
        }

        Timestamp regDate = rs.getTimestamp("RegisterDate");
        if (regDate != null) {
            c.setRegisterDate(regDate.toLocalDateTime());
        }

        c.setStatus(rs.getString("Status"));
        c.setNote(rs.getString("Note"));

        // Handle Points from JOIN
        try {
            c.setPoints(rs.getInt("TotalPoints"));
        } catch (SQLException e) {
        }


        return c;
    }
}
