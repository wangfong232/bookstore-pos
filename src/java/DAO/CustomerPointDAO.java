package DAO;

import entity.CustomerPoint;
import java.sql.*;
import java.lang.*;
import java.util.*;
import java.io.*;
/*
*
*
*/

public class CustomerPointDAO extends DBContext {

    public void insert(CustomerPoint p) {
        String sql = "INSERT INTO CustomerPoints(CustomerID, TotalPoints) VALUES (?,?)";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getCustomerID());
            ps.setInt(2, p.getTotalPoints());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CustomerPoint getByCustomerId(String id) {
        String sql = "SELECT * FROM CustomerPoints WHERE CustomerID=?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CustomerPoint p = new CustomerPoint();
                p.setCustomerID(id);
                p.setTotalPoints(rs.getInt("TotalPoints"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update(CustomerPoint p) {
        String sql = "UPDATE CustomerPoints SET TotalPoints=? WHERE CustomerID=?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getTotalPoints());
            ps.setString(2, p.getCustomerID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tăng (hoặc giảm) điểm cho khách hàng
     * 
     * @param customerId  ID khách hàng
     * @param pointsToSum Số điểm cần cộng vào (có thể âm nếu muốn trừ)
     */
    public void addPoints(String customerId, int pointsToSum) {
        String sql = "UPDATE CustomerPoints SET TotalPoints = TotalPoints + ? WHERE CustomerID = ?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pointsToSum);
            ps.setString(2, customerId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
