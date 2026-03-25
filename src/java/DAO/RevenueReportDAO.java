/*
 * DAO for Revenue Report - Báo cáo doanh thu
 * Sections: 1. Overview, 2. By Time, 4. By Staff/Shift, 6. By Category
 */
package DAO;

import java.sql.*;
import java.util.*;

public class RevenueReportDAO extends DBContext {

    /**
     * 1. Thông số tổng quan: Tổng doanh thu, chiết khấu, số đơn, đơn TB
     */
    public Map<String, Object> getOverviewStats(java.sql.Date fromDate, java.sql.Date toDate) {
        Map<String, Object> stats = new HashMap<>();
        String sql = """
            SELECT 
                ISNULL(SUM(si.FinalAmount), 0) AS TotalRevenue,
                ISNULL(SUM(si.DiscountAmount), 0) AS TotalDiscount,
                COUNT(DISTINCT si.InvoiceID) AS OrderCount
            FROM SalesInvoice si
            INNER JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            WHERE si.PaymentStatus = 'PAID'
              AND CAST(p.PaidAt AS DATE) BETWEEN ? AND ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double totalRevenue = rs.getDouble("TotalRevenue");
                double totalDiscount = rs.getDouble("TotalDiscount");
                int orderCount = rs.getInt("OrderCount");
                stats.put("totalRevenue", totalRevenue);
                stats.put("totalDiscount", totalDiscount);
                stats.put("orderCount", orderCount);
                stats.put("avgOrder", orderCount > 0 ? totalRevenue / orderCount : 0);
            } else {
                stats.put("totalRevenue", 0.0);
                stats.put("totalDiscount", 0.0);
                stats.put("orderCount", 0);
                stats.put("avgOrder", 0.0);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getOverviewStats: " + e.getMessage());
        }
        return stats;
    }

    /**
     * 2. Doanh thu theo thời gian (theo ngày) - cho biểu đồ
     */
    public List<Map<String, Object>> getRevenueByDate(java.sql.Date fromDate, java.sql.Date toDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT CAST(p.PaidAt AS DATE) AS SaleDate,
                   SUM(si.FinalAmount) AS Revenue,
                   COUNT(DISTINCT si.InvoiceID) AS OrderCount
            FROM SalesInvoice si
            INNER JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            WHERE si.PaymentStatus = 'PAID'
              AND CAST(p.PaidAt AS DATE) BETWEEN ? AND ?
            GROUP BY CAST(p.PaidAt AS DATE)
            ORDER BY SaleDate
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("saleDate", rs.getDate("SaleDate"));
                row.put("revenue", rs.getDouble("Revenue"));
                row.put("orderCount", rs.getInt("OrderCount"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getRevenueByDate: " + e.getMessage());
        }
        return list;
    }

    /**
     * 4a. Doanh thu theo nhân viên
     */
    public List<Map<String, Object>> getRevenueByStaff(java.sql.Date fromDate, java.sql.Date toDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT e.FullName AS StaffName,
                   SUM(si.FinalAmount) AS Revenue,
                   COUNT(DISTINCT si.InvoiceID) AS OrderCount
            FROM SalesInvoice si
            INNER JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            INNER JOIN Employees e ON si.StaffID = e.EmployeeID
            WHERE si.PaymentStatus = 'PAID'
              AND CAST(p.PaidAt AS DATE) BETWEEN ? AND ?
            GROUP BY e.EmployeeID, e.FullName
            ORDER BY Revenue DESC
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("staffName", rs.getString("StaffName"));
                row.put("revenue", rs.getDouble("Revenue"));
                row.put("orderCount", rs.getInt("OrderCount"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getRevenueByStaff: " + e.getMessage());
        }
        return list;
    }

    /**
     * 4b. Doanh thu theo ca làm
     */
    public List<Map<String, Object>> getRevenueByShift(java.sql.Date fromDate, java.sql.Date toDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT s.ShiftName,
                   SUM(si.FinalAmount) AS Revenue,
                   COUNT(DISTINCT si.InvoiceID) AS OrderCount
            FROM SalesInvoice si
            INNER JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            LEFT JOIN Shifts s ON si.ShiftID = s.ShiftID
            WHERE si.PaymentStatus = 'PAID'
              AND CAST(p.PaidAt AS DATE) BETWEEN ? AND ?
            GROUP BY s.ShiftID, s.ShiftName
            ORDER BY Revenue DESC
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                String shiftName = rs.getString("ShiftName");
                row.put("shiftName", shiftName != null ? shiftName : "Chưa xác định");
                row.put("revenue", rs.getDouble("Revenue"));
                row.put("orderCount", rs.getInt("OrderCount"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getRevenueByShift: " + e.getMessage());
        }
        return list;
    }

    /**
     * 6. Doanh thu theo danh mục sản phẩm
     */
    public List<Map<String, Object>> getRevenueByCategory(java.sql.Date fromDate, java.sql.Date toDate) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
            SELECT c.CategoryName,
                   SUM(sid.TotalPrice) AS Revenue,
                   SUM(sid.Quantity) AS QuantitySold
            FROM SalesInvoiceDetail sid
            INNER JOIN SalesInvoice si ON sid.InvoiceID = si.InvoiceID
            INNER JOIN Payments p ON si.InvoiceID = p.InvoiceID AND p.Status = 'COMPLETED'
            INNER JOIN Products pr ON sid.ProductID = pr.ProductID
            INNER JOIN Categories c ON pr.CategoryID = c.CategoryID
            WHERE si.PaymentStatus = 'PAID'
              AND CAST(p.PaidAt AS DATE) BETWEEN ? AND ?
            GROUP BY c.CategoryID, c.CategoryName
            ORDER BY Revenue DESC
            """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, fromDate);
            ps.setDate(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("categoryName", rs.getString("CategoryName"));
                row.put("revenue", rs.getDouble("Revenue"));
                row.put("quantitySold", rs.getInt("QuantitySold"));
                list.add(row);
            }
        } catch (SQLException e) {
            System.out.println("ERR: getRevenueByCategory: " + e.getMessage());
        }
        return list;
    }
}
