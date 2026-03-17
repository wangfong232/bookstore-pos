package DAO;

import entity.CustomerTier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.*;

/*
*
*
*/

public class CustomerTierDAO extends DBContext {

    public void insert(CustomerTier t) {
        String sql = """
                    INSERT INTO CustomerTiers(TierName, MinPoint, DiscountRate)
                    VALUES (?, ?, ?)
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getTierName());
            ps.setDouble(2, t.getMinPoint());
            ps.setDouble(3, t.getDiscountRate());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CustomerTier> getAll() {
        List<CustomerTier> list = new ArrayList<>();
        String sql = "SELECT * FROM CustomerTiers";
        try (Connection con = getConnection();
                ResultSet rs = con.prepareStatement(sql).executeQuery()) {

            while (rs.next()) {
                CustomerTier t = new CustomerTier();
                t.setTierID(rs.getInt("TierID"));
                t.setTierName(rs.getString("TierName"));
                t.setMinPoint(rs.getDouble("MinPoint"));
                t.setDiscountRate(rs.getDouble("DiscountRate"));
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void delete(int id) {
        String sql = "DELETE FROM CustomerTiers WHERE TierID=?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(CustomerTier t) {
        String sql = """
                    UPDATE CustomerTiers
                    SET TierName = ?, MinPoint = ?, DiscountRate = ?
                    WHERE TierID = ?
                """;
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, t.getTierName());
            ps.setDouble(2, t.getMinPoint());
            ps.setDouble(3, t.getDiscountRate());
            ps.setInt(4, t.getTierID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CustomerTier getTierById(int id) {
        String sql = "SELECT * FROM CustomerTiers WHERE TierID = ?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CustomerTier t = new CustomerTier();
                    t.setTierID(rs.getInt("TierID"));
                    t.setTierName(rs.getString("TierName"));
                    t.setMinPoint(rs.getDouble("MinPoint"));
                    t.setDiscountRate(rs.getDouble("DiscountRate"));
                    return t;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
