package controller;

import DAO.ShiftAssignmentDAO;
import DAO.ShiftSwapDAO;
import entity.EmployeeShiftAssignment;
import entity.ShiftSwapRequest;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;

@WebServlet("/admin/swap-approval")
public class SwapApprovalController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        ShiftSwapDAO dao = new ShiftSwapDAO();
        request.setAttribute("requests", dao.getPendingRequests());

        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-swap-approval.jsp")
                .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String requestIDStr = request.getParameter("requestID");
        String action = request.getParameter("action");

        if (requestIDStr == null || action == null) {
            response.sendRedirect("swap-approval");
            return;
        }

        int requestID;
        try {
            requestID = Integer.parseInt(requestIDStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("swap-approval");
            return;
        }

        // Lấy managerID từ session (nếu có), fallback null
        HttpSession session = request.getSession(false);
        Integer managerID = (session != null && session.getAttribute("employeeId") != null)
                ? (Integer) session.getAttribute("employeeId")
                : null;

        if (action.equalsIgnoreCase("approve")) {
            doApprove(requestID, managerID);
            response.sendRedirect(request.getContextPath() + "/admin/shift-management");

        } else if (action.equalsIgnoreCase("reject")) {
            doReject(requestID, managerID);
            response.sendRedirect("swap-approval");

        } else {
            response.sendRedirect("swap-approval");
        }
    }

    private void doApprove(int requestID, Integer managerID) {
        Connection conn = null;
        try {
            ShiftSwapDAO swapDAO = new ShiftSwapDAO();
            ShiftAssignmentDAO assignDAO = new ShiftAssignmentDAO();

            conn = swapDAO.getConnection();
            if (conn == null) return;
            conn.setAutoCommit(false);

            // 1. Lấy thông tin swap request
            ShiftSwapRequest swap = swapDAO.getRequestByIdWithConn(requestID, conn);
            if (swap == null) {
                conn.rollback();
                return;
            }

            // 2. Lấy 2 assignment (dùng cùng connection trong transaction)
            EmployeeShiftAssignment fromA = assignDAO.getByIdWithConn(swap.getFromAssignmentID(), conn);
            EmployeeShiftAssignment toA   = assignDAO.getByIdWithConn(swap.getToAssignmentID(),   conn);

            if (fromA == null || toA == null) {
                conn.rollback();
                return;
            }

            // 3. Đổi ShiftID của 2 người
            int tempShift = fromA.getShiftID();
            assignDAO.updateShift(fromA.getAssignmentID(), toA.getShiftID(), conn);
            assignDAO.updateShift(toA.getAssignmentID(),  tempShift,        conn);

            // 4. Cập nhật trạng thái request
            swapDAO.approveSwapWithConn(requestID, managerID, conn);

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void doReject(int requestID, Integer managerID) {
        Connection conn = null;
        try {
            ShiftSwapDAO swapDAO = new ShiftSwapDAO();
            conn = swapDAO.getConnection();
            if (conn == null) return;
            conn.setAutoCommit(false);

            swapDAO.rejectSwapWithConn(requestID, managerID, conn);

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
