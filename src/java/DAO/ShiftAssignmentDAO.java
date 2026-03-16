package DAO;

import entity.EmployeeShiftAssignment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftAssignmentDAO extends DBContext {

    /**
     * Phân công một nhân viên vào một ca làm việc cụ thể trong một ngày.
     * Trạng thái mặc định là 'ASSIGNED'.
     * @param empID ID của nhân viên được phân công.
     * @param shiftID ID của ca làm việc.
     * @param workDate Ngày làm việc.
     * @param assignedBy ID của người quản lý thực hiện phân công.
     */
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

    /**
     * Lấy lịch sử tất cả các ca làm việc đã được phân công của một nhân viên.
     * Sắp xếp theo ngày làm việc giảm dần (mới nhất lên trước).
     * @param empID ID của nhân viên.
     * @return Danh sách lịch sử ca làm việc của nhân viên đó.
     */
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

    /**
     * Lấy danh sách tất cả các nhân viên được phân công làm việc trong một ngày cụ thể.
     * Bao gồm thông tin về nhân viên, ca làm việc và vai trò.
     * @param date Ngày cần xem phân công.
     * @return Danh sách phân công ca làm việc trong ngày đó.
     */
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

    /**
     * Lấy thông tin chi tiết của một phân công ca làm việc dựa trên ID.
     * @param assignmentID ID của phân công ca.
     * @return Đối tượng EmployeeShiftAssignment, hoặc null nếu không tìm thấy.
     */
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

    /**
     * Tìm một ca làm việc khác trong CÙNG NGÀY của MỘT NHÂN VIÊN KHÁC
     * để có thể thực hiện đổi ca.
     * @param fromAssignmentID ID của ca làm việc hiện tại đang muốn đổi.
     * @return Đối tượng ca làm việc có thể đổi, hoặc null nếu không có.
     */
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

    /**
     * Cập nhật (đổi) ca làm việc cho một phân công đã tồn tại.
     * Sử dụng connection được truyền vào để nằm chung trong một Database Transaction.
     * @param assignmentID ID của phân công cần cập nhật.
     * @param newShiftID ID của ca làm việc mới sẽ đổi sang.
     * @param conn Connection (dùng trong transaction ngoại vi).
     * @throws SQLException nếu có lỗi cập nhật DB.
     */
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

    /**
     * Phân công một nhân viên vào cùng một ca làm việc cho cả một tuần (7 ngày).
     * Bắt đầu từ ngày startOfWeek.
     * @param empID ID của nhân viên.
     * @param shiftID ID của ca làm việc.
     * @param startOfWeek Ngày bắt đầu tuần.
     * @param assignedBy ID của người quản lý thực hiện phân công.
     */
    public void assignShiftByWeek(int empID, int shiftID,
            java.time.LocalDate startOfWeek,
            int assignedBy) {

        for (int i = 0; i < 7; i++) {

            java.sql.Date date
                    = java.sql.Date.valueOf(startOfWeek.plusDays(i));

            assignShift(empID, shiftID, date, assignedBy);
        }
    }

    /**
     * Phân công một nhân viên vào cùng một ca làm việc cho tất cả các ngày trong một tháng.
     * @param empID ID của nhân viên.
     * @param shiftID ID của ca làm việc.
     * @param ym Tháng và năm cần phân công (YearMonth).
     * @param assignedBy ID của người quản lý thực hiện phân công.
     */
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
    /**
     * Lấy danh sách tất cả các phân công ca làm việc trong một tháng và năm cụ thể.
     * Dùng để hiển thị trên giao diện lịch (Calendar View) cho Manager.
     * @param month Tháng cần xem (1-12).
     * @param year Năm cần xem.
     * @return Danh sách phân công ca làm việc trong tháng.
     */
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
    /**
     * Đếm số lượng nhân viên đã được phân công vào một ca làm việc cụ thể trong một ngày.
     * Có thể dùng để giới hạn số người (ví dụ: tối đa 8 người 1 ca).
     * @param shiftID ID của ca làm việc.
     * @param workDate Ngày làm việc.
     * @return Số lượng nhân viên đang làm ca đó.
     */
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

    /**
     * Lấy danh sách phân công ca làm việc của MỘT nhân viên trong MỘT ngày cụ thể.
     * @param empID ID của nhân viên.
     * @param date Ngày làm việc (chuỗi định dạng YYYY-MM-DD).
     * @return Danh sách các ca làm việc trong ngày đó.
     */
    public List<EmployeeShiftAssignment>
            getAssignmentsByEmployeeAndDate(int empID, String date) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, "
                + "a.WorkDate, a.Status, "
                + "s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE a.EmployeeID = ? "
                + "AND a.WorkDate = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);

            ps.setInt(1, empID);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                EmployeeShiftAssignment a = new EmployeeShiftAssignment();

                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));

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

    /**
     * Lấy danh sách phân công ca làm việc của TẤT CẢ NHÂN VIÊN TRỪ một nhân viên cụ thể,
     * trong MỘT ngày cụ thể. Dùng trong tính năng Đổi Ca để chọn người muốn đổi.
     * @param empID ID của nhân viên ĐANG tạo đơn (để loại trừ người này ra).
     * @param date Ngày làm việc (chuỗi định dạng YYYY-MM-DD).
     * @return Danh sách các ca làm việc của người khác trong ngày đó.
     */
    public List<EmployeeShiftAssignment>
            getAssignmentsByDateExceptEmployee(int empID, String date) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, "
                + "a.WorkDate, a.Status, "
                + "s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE a.EmployeeID <> ? "
                + "AND a.WorkDate = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);

            ps.setInt(1, empID);
            ps.setDate(2, java.sql.Date.valueOf(date));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                EmployeeShiftAssignment a = new EmployeeShiftAssignment();

                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));

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

    /**
     * Lấy toàn bộ danh sách phân công ca làm việc của một nhân viên (không lọc theo ngày).
     * @param empID ID của nhân viên.
     * @return Danh sách toàn bộ ca làm việc của nhân viên đó.
     */
    public List<EmployeeShiftAssignment> getAllAssignmentsByEmployee(int empID) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, "
                + "a.WorkDate, a.Status, "
                + "s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "WHERE a.EmployeeID = ? "
                + "ORDER BY a.WorkDate DESC";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, empID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                EmployeeShiftAssignment a = new EmployeeShiftAssignment();
                a.setAssignmentID(rs.getInt("AssignmentID"));
                a.setEmployeeId(rs.getInt("EmployeeID"));
                a.setShiftID(rs.getInt("ShiftID"));
                a.setWorkDate(rs.getDate("WorkDate"));
                a.setStatus(rs.getString("Status"));
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

    /**
     * Lấy toàn bộ danh sách phân công ca làm việc của những người khác (không lọc theo ngày).
     * Loại trừ nhân viên yêu cầu. Dùng khi dropdown Đổi Ca load mặc định tất cả dữ liệu.
     * @param empID ID của nhân viên đang tạo đơn đổi ca.
     * @return Danh sách toàn bộ ca làm việc của những người khác.
     */
    public List<EmployeeShiftAssignment> getAllAssignmentsExceptEmployee(int empID) {

        List<EmployeeShiftAssignment> list = new ArrayList<>();

        String sql = "SELECT a.AssignmentID, a.EmployeeID, a.ShiftID, "
                + "a.WorkDate, a.Status, "
                + "e.FullName, "
                + "s.ShiftName, s.StartTime, s.EndTime "
                + "FROM EmployeeShiftAssignments a "
                + "JOIN Shifts s ON a.ShiftID = s.ShiftID "
                + "JOIN Employees e ON a.EmployeeID = e.EmployeeID "
                + "WHERE a.EmployeeID <> ? "
                + "ORDER BY a.WorkDate DESC";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, empID);
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

    /** Lấy assignment bằng connection dùng chung trong transaction */
    public EmployeeShiftAssignment getByIdWithConn(int assignmentID, Connection conn) throws Exception {
        String sql = "SELECT * FROM EmployeeShiftAssignments WHERE AssignmentID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, assignmentID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            EmployeeShiftAssignment a = new EmployeeShiftAssignment();
            a.setAssignmentID(rs.getInt("AssignmentID"));
            a.setEmployeeId(rs.getInt("EmployeeID"));
            a.setShiftID(rs.getInt("ShiftID"));
            a.setWorkDate(rs.getDate("WorkDate"));
            a.setStatus(rs.getString("Status"));
            return a;
        }
        return null;
    }
}

