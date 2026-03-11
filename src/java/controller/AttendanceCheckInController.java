package controller;

import DAO.AttendanceDAO;
import entity.AttendanceView;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller xử lý check-in/check-out cho nhân viên.
 * Hỗ trợ nhiều ca/ngày, kiểm tra thời gian ca.
 *
 * shiftStatus:
 * NOT_YET - Chưa đến giờ bắt đầu ca
 * ACTIVE - Trong giờ ca (có thể check-in)
 * EXPIRED - Qua hạn, chưa check-in
 * WORKING - Đang làm (đã check-in, chưa check-out)
 * COMPLETED - Hoàn tất (đã check-in và check-out)
 */
@WebServlet(urlPatterns = { "/staff/attendance-checkin" })
public class AttendanceCheckInController extends HttpServlet {

    private AttendanceDAO dao = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("employeeId");

        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        List<AttendanceView> shifts = dao.getTodayShiftAssignments(employeeId);
        computeShiftStatus(shifts);

        request.setAttribute("shifts", shifts);
        request.getRequestDispatcher("/AdminLTE-3.2.0/attendance-checkin.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("employeeId");

        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String assignmentIdRaw = request.getParameter("assignmentId");

        try {
            if (assignmentIdRaw == null || assignmentIdRaw.trim().isEmpty()) {
                request.setAttribute("error", "Không xác định được ca làm việc. Vui lòng thử lại.");
            } else {
                int assignmentId = Integer.parseInt(assignmentIdRaw);
                AttendanceView shift = dao.getTodayShiftByAssignmentId(assignmentId);

                if (shift == null) {
                    request.setAttribute("error", "Không tìm thấy ca làm việc này.");
                } else {
                    // Kiểm tra trạng thái thời gian trước khi xử lý
                    String status = computeSingleShiftStatus(shift);

                    if ("checkin".equals(action)) {
                        if (shift.getCheckIn() != null) {
                            request.setAttribute("error",
                                    "Bạn đã check-in ca '" + shift.getShiftName() + "' lúc: " + shift.getCheckIn());
                        } else if ("NOT_YET".equals(status)) {
                            request.setAttribute("error",
                                    "Chưa đến giờ bắt đầu ca '" + shift.getShiftName() + "'. Giờ vào: "
                                            + shift.getStartTime());
                        } else if ("EXPIRED".equals(status)) {
                            request.setAttribute("error",
                                    "Đã quá hạn chấm công ca '" + shift.getShiftName() + "'. Ca đã kết thúc lúc: "
                                            + shift.getEndTime());
                        } else {
                            boolean success = dao.checkIn(assignmentId);
                            if (success) {
                                request.setAttribute("success",
                                        "Check-in ca '" + shift.getShiftName() + "' thành công!");
                            } else {
                                request.setAttribute("error",
                                        "Check-in thất bại. Vui lòng thử lại hoặc liên hệ quản lý.");
                            }
                        }
                    } else if ("checkout".equals(action)) {
                        if (shift.getCheckIn() == null) {
                            request.setAttribute("error", "Bạn phải check-in ca '" + shift.getShiftName() + "' trước.");
                        } else if (shift.getCheckOut() != null) {
                            request.setAttribute("error",
                                    "Bạn đã check-out ca '" + shift.getShiftName() + "' lúc: " + shift.getCheckOut());
                        } else {
                            boolean success = dao.checkOut(assignmentId);
                            if (success) {
                                request.setAttribute("success",
                                        "Check-out ca '" + shift.getShiftName() + "' thành công!");
                            } else {
                                request.setAttribute("error", "Check-out thất bại. Vui lòng thử lại.");
                            }
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Dữ liệu không hợp lệ.");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
        }

        // Luôn load lại toàn bộ danh sách ca sau khi xử lý
        List<AttendanceView> shifts = dao.getTodayShiftAssignments(employeeId);
        computeShiftStatus(shifts);
        request.setAttribute("shifts", shifts);
        request.getRequestDispatcher("/AdminLTE-3.2.0/attendance-checkin.jsp")
                .forward(request, response);
    }

    /**
     * Tính shiftStatus cho toàn bộ danh sách ca dựa vào giờ hiện tại.
     */
    private void computeShiftStatus(List<AttendanceView> shifts) {
        for (AttendanceView shift : shifts) {
            shift.setShiftStatus(computeSingleShiftStatus(shift));
        }
    }

    /**
     * Tính shiftStatus cho 1 ca:
     * COMPLETED → đã check-in và check-out
     * WORKING → đã check-in, chưa check-out
     * NOT_YET → chưa đến giờ bắt đầu
     * EXPIRED → qua giờ kết thúc, chưa check-in
     * ACTIVE → đang trong khoảng giờ ca, chưa check-in
     */
    private String computeSingleShiftStatus(AttendanceView shift) {
        if (shift.getCheckIn() != null && shift.getCheckOut() != null) {
            return "COMPLETED";
        }
        if (shift.getCheckIn() != null) {
            return "WORKING";
        }

        LocalTime now = LocalTime.now();
        LocalTime start = shift.getStartTime().toLocalTime();
        LocalTime end = shift.getEndTime().toLocalTime();

        if (now.isBefore(start)) {
            return "NOT_YET";
        } else if (now.isAfter(end)) {
            return "EXPIRED";
        } else {
            return "ACTIVE";
        }
    }
}
