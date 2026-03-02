package DAO;

import entity.EmployeeShiftAssignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftAssignmentDAO extends DBContext {

    public void assignShift(int empID, int shiftID, Date workDate, int assignedBy) {

        String sql = "INSERT INTO EmployeeShiftAssignments "
                + "(EmployeeID, ShiftID, WorkDate, AssignedBy, Status) "
                + "VALUES (?, ?, ?, ?, 'ASSIGNED')";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, empID);
            ps.setInt(2, shiftID);
            ps.setDate(3, workDate);
            ps.setInt(4, assignedBy);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EmployeeShiftAssignment> getHistoryByEmployee(int empID) {
        List<EmployeeShiftAssignment> list = new ArrayList<>();
        String sql = "SELECT a.*, s.ShiftName "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE a.EmployeeID = ? "
                + "ORDER BY WorkDate DESC";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, empID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();
                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setShiftName(rs.getString("ShiftName"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<EmployeeShiftAssignment> getAssignmentsByDate(Date date) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql
                = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, a.WorkDate, a.Status, "
                + "e.FullName, r.RoleName, "
                + "s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "LEFT JOIN Employees e ON a.EmployeeID = e.EmployeeID "
                + "LEFT JOIN Roles r ON e.RoleID = r.RoleID "
                + "LEFT JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE a.WorkDate = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();

                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));

                a.setFullName(rs.getString("FullName"));
                a.setRole(rs.getString("RoleName"));
                a.setShiftName(rs.getString("ShiftName"));
                a.setStartTime(rs.getString("StartTime"));
                a.setEndTime(rs.getString("EndTime"));

                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public EmployeeShiftAssignment getById(int assignmentID) {

        String sql = "SELECT * FROM EmployeeShiftAssignments WHERE AssignmentID = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, assignmentID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();
                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                return a;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public EmployeeShiftAssignment findAssignmentToSwap(int fromAssignmentID) {

        String sql = "SELECT a2.* "
                + "FROM EmployeeShiftAssignments a1 "
                + "JOIN EmployeeShiftAssignments a2 "
                + "ON a1.WorkDate = a2.WorkDate "
                + "AND a1.EmployeeID <> a2.EmployeeID "
                + "WHERE a1.AssignmentID = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, fromAssignmentID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();
                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                return a;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateShift(int assignmentID, int newShiftID, Connection conn)
            throws SQLException {

        String sql = "UPDATE EmployeeShiftAssignments "
                + "SET ShiftID = ? "
                + "WHERE AssignmentID = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, newShiftID);
        ps.setInt(2, assignmentID);
        ps.executeUpdate();
    }

    public void assignShiftByWeek(int empID, int shiftID,
            java.time.LocalDate startOfWeek,
            int assignedBy) {

        for (int i = 0; i < 7; i++) {

            java.sql.Date date
                    = java.sql.Date.valueOf(startOfWeek.plusDays(i));

            assignShift(empID, shiftID, date, assignedBy);
        }
    }

    public void assignShiftByMonth(int empID, int shiftID,
            java.time.YearMonth ym,
            int assignedBy) {

        for (int i = 1; i <= ym.lengthOfMonth(); i++) {

            java.sql.Date date
                    = java.sql.Date.valueOf(ym.atDay(i));

            assignShift(empID, shiftID, date, assignedBy);
        }
    }

    //Xem ca làm việc với giao diện lịch
    public List<EmployeeShiftAssignment> getAssignmentsByMonth(int month, int year) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql
                = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, a.WorkDate, a.Status, "
                + "e.FullName, s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Employees e ON a.EmployeeID = e.EmployeeID "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE MONTH(a.WorkDate) = ? AND YEAR(a.WorkDate) = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();
                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));
                a.setFullName(rs.getString("FullName"));
                a.setShiftName(rs.getString("ShiftName"));
                a.setStartTime(rs.getString("StartTime"));
                a.setEndTime(rs.getString("EndTime"));
                list.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    
    //Giới hạn 8 người 1 ca
    public int countEmployeesInShift(int shiftID, Date workDate) {

        String sql = "SELECT COUNT(*) FROM EmployeeShiftAssignments "
                + "WHERE ShiftID = ? AND WorkDate = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, shiftID);
            ps.setDate(2, workDate);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
