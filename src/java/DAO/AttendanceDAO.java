package DAO;

import entity.AttendanceView;
import entity.AttendanceStats;
import java.sql.*;
import java.util.*;
import java.sql.Date;

public class AttendanceDAO extends DBContext {

    public List<AttendanceView> getAttendanceByDate(
            Date workDate,
            int page,
            int pageSize) {

        List<AttendanceView> list = new ArrayList<>();

        String sql = """
                    SELECT esa.AssignmentID,
                           e.FullName,
                           s.ShiftName,
                           s.StartTime,
                           s.EndTime,
                           a.CheckIn,
                           a.CheckOut
                    FROM EmployeeShiftAssignments esa
                    JOIN Employees e ON esa.EmployeeID = e.EmployeeID
                    JOIN Shifts s ON esa.ShiftID = s.ShiftID
                    LEFT JOIN Attendance a
                           ON esa.AssignmentID = a.AssignmentID
                    WHERE esa.WorkDate = ?
                    ORDER BY s.StartTime
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, workDate);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AttendanceView v = new AttendanceView();

                v.setAttendanceId(rs.getInt("AssignmentID"));
                v.setFullName(rs.getString("FullName"));
                v.setShiftName(rs.getString("ShiftName"));
                v.setStartTime(rs.getTime("StartTime"));
                v.setEndTime(rs.getTime("EndTime"));
                v.setCheckIn(rs.getTime("CheckIn"));
                v.setCheckOut(rs.getTime("CheckOut"));

                list.add(v);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countAttendanceByDate(Date workDate) {

        String sql = """
                    SELECT COUNT(*)
                    FROM EmployeeShiftAssignments
                    WHERE WorkDate = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, workDate);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String, Integer> getDashboardStats(Date workDate) {

        Map<String, Integer> stats = new HashMap<>();

        String sql = """
                    SELECT
                        COUNT(*) AS TotalAssigned,
                        SUM(CASE WHEN a.CheckIn IS NOT NULL THEN 1 ELSE 0 END) AS CheckedIn,
                        SUM(CASE WHEN a.CheckIn IS NULL THEN 1 ELSE 0 END) AS NotCheckedIn
                    FROM EmployeeShiftAssignments esa
                    LEFT JOIN Attendance a
                           ON esa.AssignmentID = a.AssignmentID
                    WHERE esa.WorkDate = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, workDate);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stats.put("total", rs.getInt("TotalAssigned"));
                stats.put("checkedIn", rs.getInt("CheckedIn"));
                stats.put("notCheckedIn", rs.getInt("NotCheckedIn"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Lấy TẤT CẢ ca làm việc hôm nay của nhân viên (hỗ trợ nhiều ca/ngày).
     */
    public List<AttendanceView> getTodayShiftAssignments(int employeeId) {
        List<AttendanceView> list = new ArrayList<>();

        String sql = """
                SELECT esa.AssignmentID,
                       e.FullName,
                       s.ShiftName,
                       s.StartTime,
                       s.EndTime,
                       a.CheckIn,
                       a.CheckOut
                FROM EmployeeShiftAssignments esa
                JOIN Employees e ON esa.EmployeeID = e.EmployeeID
                JOIN Shifts s ON esa.ShiftID = s.ShiftID
                LEFT JOIN Attendance a ON esa.AssignmentID = a.AssignmentID
                WHERE esa.EmployeeID = ? AND esa.WorkDate = CAST(GETDATE() AS DATE)
                ORDER BY s.StartTime
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                AttendanceView v = new AttendanceView();
                v.setAttendanceId(rs.getInt("AssignmentID"));
                v.setFullName(rs.getString("FullName"));
                v.setShiftName(rs.getString("ShiftName"));
                v.setStartTime(rs.getTime("StartTime"));
                v.setEndTime(rs.getTime("EndTime"));
                v.setCheckIn(rs.getTime("CheckIn"));
                v.setCheckOut(rs.getTime("CheckOut"));
                list.add(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy thông tin 1 ca cụ thể theo AssignmentID (dùng để refresh sau
     * check-in/out).
     */
    public AttendanceView getTodayShiftByAssignmentId(int assignmentId) {
        String sql = """
                SELECT esa.AssignmentID,
                       e.FullName,
                       s.ShiftName,
                       s.StartTime,
                       s.EndTime,
                       a.CheckIn,
                       a.CheckOut
                FROM EmployeeShiftAssignments esa
                JOIN Employees e ON esa.EmployeeID = e.EmployeeID
                JOIN Shifts s ON esa.ShiftID = s.ShiftID
                LEFT JOIN Attendance a ON esa.AssignmentID = a.AssignmentID
                WHERE esa.AssignmentID = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AttendanceView v = new AttendanceView();
                v.setAttendanceId(rs.getInt("AssignmentID"));
                v.setFullName(rs.getString("FullName"));
                v.setShiftName(rs.getString("ShiftName"));
                v.setStartTime(rs.getTime("StartTime"));
                v.setEndTime(rs.getTime("EndTime"));
                v.setCheckIn(rs.getTime("CheckIn"));
                v.setCheckOut(rs.getTime("CheckOut"));
                return v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Ghi nhận check-in.
     * Dùng MERGE để xử lý cả 2 trường hợp:
     * - Chưa có record → INSERT
     * - Đã có record nhưng CheckIn NULL → UPDATE
     * - Đã có record và CheckIn có giá trị → không làm gì (trả false)
     */
    public boolean checkIn(int assignmentId) {
        String sql = """
                MERGE Attendance AS target
                USING (SELECT AssignmentID, EmployeeID, ShiftID, WorkDate FROM EmployeeShiftAssignments WHERE AssignmentID = ?) AS source
                ON target.AssignmentID = source.AssignmentID
                WHEN MATCHED AND target.CheckIn IS NULL THEN
                    UPDATE SET CheckIn = CAST(GETDATE() AS TIME)
                WHEN NOT MATCHED THEN
                    INSERT (AssignmentID, EmployeeID, ShiftID, WorkDate, CheckIn, Status)
                    VALUES (source.AssignmentID, source.EmployeeID, source.ShiftID, source.WorkDate, CAST(GETDATE() AS TIME), 'Present');
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Ghi nhận check-out.
     */
    public boolean checkOut(int assignmentId) {
        String sql = """
                UPDATE Attendance
                SET CheckOut = CAST(GETDATE() AS TIME)
                WHERE AssignmentID = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy danh sách thống kê tổng giờ làm việc của từng nhân viên trong tháng.
     * Chỉ tính các record có cả CheckIn và CheckOut.
     */
    public List<AttendanceStats> getMonthlyStats(int month, int year, int page, int pageSize) {
        List<AttendanceStats> list = new ArrayList<>();

        String sql = """
                    SELECT
                        e.EmployeeID,
                        e.FullName,
                        COUNT(DISTINCT CAST(a.WorkDate AS DATE))               AS WorkDays,
                        SUM(DATEDIFF(MINUTE,
                                     CAST(a.CheckIn  AS DATETIME),
                                     CAST(a.CheckOut AS DATETIME)))             AS TotalMinutes
                    FROM Attendance a
                    JOIN EmployeeShiftAssignments esa ON a.AssignmentID = esa.AssignmentID
                    JOIN Employees e                  ON esa.EmployeeID  = e.EmployeeID
                    WHERE a.CheckIn  IS NOT NULL
                      AND a.CheckOut IS NOT NULL
                      AND MONTH(a.WorkDate) = ?
                      AND YEAR(a.WorkDate)  = ?
                    GROUP BY e.EmployeeID, e.FullName
                    ORDER BY TotalMinutes DESC
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ps.setInt(3, (page - 1) * pageSize);
            ps.setInt(4, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AttendanceStats s = new AttendanceStats();
                s.setEmployeeId(rs.getInt("EmployeeID"));
                s.setFullName(rs.getString("FullName"));
                s.setWorkDays(rs.getInt("WorkDays"));
                s.setTotalMinutes(rs.getLong("TotalMinutes"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Đếm số nhân viên khác nhau có dữ liệu chấm công (CheckIn + CheckOut) trong tháng.
     */
    public int countMonthlyStats(int month, int year) {
        String sql = """
                    SELECT COUNT(DISTINCT esa.EmployeeID)
                    FROM Attendance a
                    JOIN EmployeeShiftAssignments esa ON a.AssignmentID = esa.AssignmentID
                    WHERE a.CheckIn  IS NOT NULL
                      AND a.CheckOut IS NOT NULL
                      AND MONTH(a.WorkDate) = ?
                      AND YEAR(a.WorkDate)  = ?
                """;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
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
