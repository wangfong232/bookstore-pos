package controller;

import DAO.ShiftAssignmentDAO;
import DAO.ShiftSwapDAO;
import entity.EmployeeShiftAssignment;
import entity.ShiftSwapRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/staff/swap")
public class StaffSwapController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        int fromEmployeeID = (Integer) session.getAttribute("employeeId");

        ShiftAssignmentDAO assignDAO = new ShiftAssignmentDAO();
        String date = request.getParameter("workDate");
        String ajax = request.getParameter("ajax");

        if (date != null && !date.isEmpty()) {
            List<EmployeeShiftAssignment> myAssignments =
                    assignDAO.getAssignmentsByEmployeeAndDate(fromEmployeeID, date);
            List<EmployeeShiftAssignment> otherAssignments =
                    assignDAO.getAssignmentsByDateExceptEmployee(fromEmployeeID, date);

            if ("true".equals(ajax)) {
                // JSON response cho AJAX call
                response.setContentType("application/json;charset=UTF-8");
                StringBuilder sb = new StringBuilder();
                sb.append("{\"my\":[");
                for (int i = 0; i < myAssignments.size(); i++) {
                    EmployeeShiftAssignment a = myAssignments.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"id\":").append(a.getAssignmentID()).append(",")
                      .append("\"shiftName\":\"").append(escapeJson(a.getShiftName())).append("\",")
                      .append("\"workDate\":\"").append(a.getWorkDate()).append("\",")
                      .append("\"employeeId\":").append(a.getEmployeeId())
                      .append("}");
                }
                sb.append("],\"other\":[");
                for (int i = 0; i < otherAssignments.size(); i++) {
                    EmployeeShiftAssignment a = otherAssignments.get(i);
                    if (i > 0) sb.append(",");
                    sb.append("{")
                      .append("\"id\":").append(a.getAssignmentID()).append(",")
                      .append("\"shiftName\":\"").append(escapeJson(a.getShiftName())).append("\",")
                      .append("\"workDate\":\"").append(a.getWorkDate()).append("\",")
                      .append("\"employeeId\":").append(a.getEmployeeId()).append(",")
                      .append("\"fullName\":\"").append(escapeJson(a.getFullName())).append("\"")
                      .append("}");
                }
                sb.append("]}");
                response.getWriter().write(sb.toString());
                return;
            }

            request.setAttribute("myAssignments", myAssignments);
            request.setAttribute("otherAssignments", otherAssignments);
        } else {
            // Không có date → load tất cả assignments của nhân viên và tất cả của người khác
            List<EmployeeShiftAssignment> myAssignments =
                    assignDAO.getAllAssignmentsByEmployee(fromEmployeeID);
            List<EmployeeShiftAssignment> otherAssignments =
                    assignDAO.getAllAssignmentsExceptEmployee(fromEmployeeID);
            request.setAttribute("myAssignments", myAssignments);
            request.setAttribute("otherAssignments", otherAssignments);
        }

        request.getRequestDispatcher("/AdminLTE-3.2.0/staff-swap-form.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int fromEmployeeID = (Integer) session.getAttribute("employeeId");

            int fromAssignmentID =
                    Integer.parseInt(request.getParameter("fromAssignmentID"));
            int toAssignmentID =
                    Integer.parseInt(request.getParameter("toAssignmentID"));

            String reason = request.getParameter("reason");

            ShiftSwapDAO dao = new ShiftSwapDAO();
            
            // Duplicate check
            if (dao.hasDuplicatePendingRequest(fromAssignmentID, toAssignmentID, reason)) {
                response.sendRedirect("swap?error=duplicate");
                return;
            }

            ShiftAssignmentDAO assignDAO = new ShiftAssignmentDAO();

            EmployeeShiftAssignment toAssignment = assignDAO.getById(toAssignmentID);

            if (toAssignment == null) {
                response.sendRedirect("swap?error=invalidTarget");
                return;
            }

            int toEmployeeID = toAssignment.getEmployeeId();

            ShiftSwapRequest swap = new ShiftSwapRequest();
            swap.setFromEmployeeID(fromEmployeeID);
            swap.setFromAssignmentID(fromAssignmentID);
            swap.setToEmployeeID(toEmployeeID);
            swap.setToAssignmentID(toAssignmentID);
            swap.setReason(reason);
            swap.setStatus("PENDING");

            dao.insertSwapRequest(swap);

            response.sendRedirect("swap?success=true");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("swap?error=true");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
