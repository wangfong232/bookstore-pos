package DAO;

import entity.ShiftSwapRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftSwapDAO extends DBContext {

    /**
     * Gửi yêu cầu đổi ca làm việc (dành cho nhân viên).
     * @param empID ID của nhân viên tạo đơn.
     * @param assignmentID ID của ca làm việc muốn đổi đi.
     * @param reason Lý do muốn đổi ca.
     */
    public void requestSwap(int empID, int assignmentID, String reason) {
        String sql = "INSERT INTO ShiftSwapRequests "
                + "(FromEmployeeID, FromAssignmentID, Reason) "
                + "VALUES (?, ?, ?)";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, empID);
            ps.setInt(2, assignmentID);
            ps.setString(3, reason);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Duyệt đơn đổi ca (Manager). Cập nhật trạng thái thành APPROVED.
     * @param requestID ID của đơn đổi ca.
     * @param managerID ID của quản lý thực hiện duyệt đơn.
     * @param conn Connection (dùng trong transaction ngoại vi).
     * @throws Exception nếu có lỗi DB.
     */
    public void approveSwap(int requestID, int managerID, Connection conn)
            throws Exception {

        String sql = "UPDATE ShiftSwapRequests "
                + "SET Status='APPROVED', ApprovedBy=?, ApprovedAt=GETDATE() "
                + "WHERE SwapRequestID=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, managerID);
        ps.setInt(2, requestID);
        ps.executeUpdate();
    }

    /**
     * Lấy danh sách tất cả các đơn đổi ca đang ở trạng thái PENDING.
     * Thường dùng cho trang duyệt đơn của Manager.
     * @return Danh sách các yêu cầu đổi ca đang chờ duyệt.
     */
    public List<ShiftSwapRequest> getPendingRequests() {
        List<ShiftSwapRequest> list = new ArrayList<>();

        String sql = "SELECT r.*, "
                + "e1.FullName AS FromName, "
                + "s1.ShiftName AS FromShift, a1.WorkDate AS FromDate, "
                + "e2.FullName AS ToName, "
                + "s2.ShiftName AS ToShift, a2.WorkDate AS ToDate "
                + "FROM ShiftSwapRequests r "
                + "JOIN Employees e1 ON r.FromEmployeeID = e1.EmployeeID "
                + "JOIN EmployeeShiftAssignments a1 ON r.FromAssignmentID = a1.AssignmentID "
                + "JOIN Shifts s1 ON a1.ShiftID = s1.ShiftID "
                + "JOIN Employees e2 ON r.ToEmployeeID = e2.EmployeeID "
                + "JOIN EmployeeShiftAssignments a2 ON r.ToAssignmentID = a2.AssignmentID "
                + "JOIN Shifts s2 ON a2.ShiftID = s2.ShiftID "
                + "WHERE r.Status = 'PENDING' "
                + "ORDER BY r.RequestedAt DESC";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ShiftSwapRequest r = new ShiftSwapRequest();

                r.setSwapRequestID(rs.getInt("SwapRequestID"));
                r.setFullName(rs.getString("FromName"));
                r.setShiftName(rs.getString("FromShift"));
                r.setWorkDate(rs.getDate("FromDate"));

                r.setToFullName(rs.getString("ToName"));
                r.setToShiftName(rs.getString("ToShift"));
                r.setToWorkDate(rs.getDate("ToDate"));

                r.setReason(rs.getString("Reason"));
                r.setStatus(rs.getString("Status"));

                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Từ chối đơn đổi ca (Manager). Cập nhật trạng thái thành REJECTED.
     * Mở connection mới cục bộ.
     * @param requestID ID của đơn đổi ca.
     * @param managerID ID của quản lý thực hiện từ chối.
     */
    public void rejectSwap(int requestID, int managerID) {

        // Delegate to connection-aware method to keep behaviour consistent
        Connection conn = null;
        String sql = "UPDATE ShiftSwapRequests "
                + "SET Status='REJECTED', ApprovedBy=?, ApprovedAt=GETDATE() "
                + "WHERE SwapRequestID=?";

        try {
            conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, managerID);
            ps.setInt(2, requestID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Từ chối đơn đổi ca (Manager). Cập nhật trạng thái thành REJECTED.
     * Dùng connection được cung cấp để tham gia vào transaction lớn hơn.
     * @param requestID ID của đơn đổi ca.
     * @param managerID ID của quản lý thực hiện từ chối.
     * @param conn Connection (dùng trong transaction ngoại vi).
     * @throws Exception nếu có lỗi DB.
     */
    public void rejectSwap(int requestID, int managerID, Connection conn)
            throws Exception {

        String sql = "UPDATE ShiftSwapRequests "
                + "SET Status='REJECTED', ApprovedBy=?, ApprovedAt=GETDATE() "
                + "WHERE SwapRequestID=?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, managerID);
        ps.setInt(2, requestID);
        ps.executeUpdate();
    }

    /**
     * Lấy địa chỉ email của nhân viên tạo đơn đổi ca.
     * Thường dùng để gửi email thông báo sau khi đơn được duyệt hoặc từ chối.
     * @param requestID ID của đơn đổi ca.
     * @return Địa chỉ email của người tạo đơn.
     */
    public String getEmailByRequestID(int requestID) {

        String sql = "SELECT e.Email "
                + "FROM ShiftSwapRequests r "
                + "JOIN Employees e ON r.FromEmployeeID = e.EmployeeID "
                + "WHERE r.SwapRequestID = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, requestID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("Email");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy thông tin chi tiết của một đơn đổi ca dựa trên ID.
     * @param requestID ID của đơn đổi ca cần lấy.
     * @return Đối tượng ShiftSwapRequest chứa thông tin đơn đổi ca, hoặc null nếu không tìm thấy.
     */
    public ShiftSwapRequest getRequestById(int requestID) {

        String sql = "SELECT * FROM ShiftSwapRequests WHERE SwapRequestID = ?";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);

            ps.setInt(1, requestID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                ShiftSwapRequest r = new ShiftSwapRequest();

                r.setSwapRequestID(rs.getInt("SwapRequestID"));
                r.setFromEmployeeID(rs.getInt("FromEmployeeID"));
                r.setFromAssignmentID(rs.getInt("FromAssignmentID"));
                r.setToEmployeeID(rs.getInt("ToEmployeeID"));
                r.setToAssignmentID(rs.getInt("ToAssignmentID"));
                r.setReason(rs.getString("Reason"));
                r.setStatus(rs.getString("Status"));
                return r;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo một đơn đổi ca mới (insert vào database).
     * Trạng thái mặc định thường là PENDING.
     * @param swap Đối tượng phân tích chứa toàn bộ thông tin đơn đổi ca.
     */
    public void insertSwapRequest(ShiftSwapRequest swap) {

        String sql = "INSERT INTO ShiftSwapRequests "
                + "(FromEmployeeID, FromAssignmentID, Reason, Status, "
                + "ToEmployeeID, ToAssignmentID) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, swap.getFromEmployeeID());
            ps.setInt(2, swap.getFromAssignmentID());
            ps.setString(3, swap.getReason());
            ps.setString(4, swap.getStatus());
            ps.setInt(5, swap.getToEmployeeID());
            ps.setInt(6, swap.getToAssignmentID());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy danh sách lịch sử đơn đổi ca của một nhân viên cụ thể.
     * @param employeeID ID của nhân viên cần xem lịch sử.
     * @return Danh sách các đơn đổi ca do nhân viên này tạo.
     */
    public List<ShiftSwapRequest> getMySwapRequests(int employeeID) {
        List<ShiftSwapRequest> list = new ArrayList<>();

        String sql = "SELECT r.SwapRequestID, r.Status, r.Reason, "
                + "s1.ShiftName AS FromShift, a1.WorkDate AS FromDate, "
                + "e2.FullName AS ToName, "
                + "s2.ShiftName AS ToShift, a2.WorkDate AS ToDate "
                + "FROM ShiftSwapRequests r "
                + "JOIN EmployeeShiftAssignments a1 ON r.FromAssignmentID = a1.AssignmentID "
                + "JOIN Shifts s1 ON a1.ShiftID = s1.ShiftID "
                + "JOIN EmployeeShiftAssignments a2 ON r.ToAssignmentID = a2.AssignmentID "
                + "JOIN Shifts s2 ON a2.ShiftID = s2.ShiftID "
                + "JOIN Employees e2 ON r.ToEmployeeID = e2.EmployeeID "
                + "WHERE r.FromEmployeeID = ? "
                + "ORDER BY r.RequestedAt DESC";

        try {
            PreparedStatement ps = getConnection().prepareStatement(sql);
            ps.setInt(1, employeeID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ShiftSwapRequest r = new ShiftSwapRequest();
                r.setSwapRequestID(rs.getInt("SwapRequestID"));
                r.setStatus(rs.getString("Status"));
                r.setReason(rs.getString("Reason"));
                r.setShiftName(rs.getString("FromShift"));
                r.setWorkDate(rs.getDate("FromDate"));
                r.setToFullName(rs.getString("ToName"));
                r.setToShiftName(rs.getString("ToShift"));
                r.setToWorkDate(rs.getDate("ToDate"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //Tự động reject đơn đổi ca quá h
    public int rejectExpiredPendingRequests() {
    String sql =
        "UPDATE r " +
        "SET Status='REJECTED', ApprovedAt=GETDATE() " +
        "FROM ShiftSwapRequests r " +
        "JOIN EmployeeShiftAssignments a1 ON r.FromAssignmentID = a1.AssignmentID " +
        "JOIN EmployeeShiftAssignments a2 ON r.ToAssignmentID = a2.AssignmentID " +
        "WHERE r.Status = 'PENDING' " +
        "AND (a1.WorkDate < CAST(GETDATE() AS DATE) " +
        "     OR a2.WorkDate < CAST(GETDATE() AS DATE))";
    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        return ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return 0;
}


    /** Lấy swap request trong cùng một transaction connection */
    public ShiftSwapRequest getRequestByIdWithConn(int requestID, Connection conn) throws Exception {
        String sql = "SELECT * FROM ShiftSwapRequests WHERE SwapRequestID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, requestID);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            ShiftSwapRequest r = new ShiftSwapRequest();
            r.setSwapRequestID(rs.getInt("SwapRequestID"));
            r.setFromEmployeeID(rs.getInt("FromEmployeeID"));
            r.setFromAssignmentID(rs.getInt("FromAssignmentID"));
            r.setToEmployeeID(rs.getInt("ToEmployeeID"));
            r.setToAssignmentID(rs.getInt("ToAssignmentID"));
            r.setReason(rs.getString("Reason"));
            r.setStatus(rs.getString("Status"));
            return r;
        }
        return null;
    }

    /** Approve trong cùng một transaction connection. managerID có thể null */
    public void approveSwapWithConn(int requestID, Integer managerID, Connection conn) throws Exception {
        String sql = "UPDATE ShiftSwapRequests "
                + "SET Status='APPROVED', ApprovedBy=?, ApprovedAt=GETDATE() "
                + "WHERE SwapRequestID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        if (managerID != null) {
            ps.setInt(1, managerID);
        } else {
            ps.setNull(1, java.sql.Types.INTEGER);
        }
        ps.setInt(2, requestID);
        ps.executeUpdate();
    }

    /** Reject trong cùng một transaction connection. managerID có thể null */
    public void rejectSwapWithConn(int requestID, Integer managerID, Connection conn) throws Exception {
        String sql = "UPDATE ShiftSwapRequests "
                + "SET Status='REJECTED', ApprovedBy=?, ApprovedAt=GETDATE() "
                + "WHERE SwapRequestID=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        if (managerID != null) {
            ps.setInt(1, managerID);
        } else {
            ps.setNull(1, java.sql.Types.INTEGER);
        }
        ps.setInt(2, requestID);
        ps.executeUpdate();
    }
}


