/*
 * Attendance Controller – xử lý danh sách chấm công và thống kê tháng
 */
package controller;

import DAO.AttendanceDAO;
import entity.AttendanceStats;
import entity.AttendanceView;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = { "/admin/attendance" })
public class AttendanceController extends HttpServlet {

    private AttendanceDAO dao = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("stats".equals(action)) {
            showStats(request, response);
        } else {
            listAttendance(request, response);
        }
    }


    // -----------------------------------------------------------------
    // Danh sách chấm công theo ngày
    // -----------------------------------------------------------------
    private void listAttendance(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String dateRaw = request.getParameter("workDate");
        String pageRaw = request.getParameter("page");

        Date workDate = (dateRaw == null || dateRaw.isEmpty())
                ? new Date(System.currentTimeMillis())
                : Date.valueOf(dateRaw);

        int page = (pageRaw == null) ? 1 : Integer.parseInt(pageRaw);
        int pageSize = 10;
        int totalRecords = dao.countAttendanceByDate(workDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<AttendanceView> list = dao.getAttendanceByDate(workDate, page, pageSize);
        Map<String, Integer> stats = dao.getDashboardStats(workDate);

        request.setAttribute("attendanceList", list);
        request.setAttribute("workDate", workDate);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("stats", stats);

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/attendance-list.jsp")
                .forward(request, response);
    }

    // -----------------------------------------------------------------
    // Bảng thống kê tổng giờ làm theo tháng
    // -----------------------------------------------------------------
    private void showStats(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String monthRaw = request.getParameter("month");
        String yearRaw  = request.getParameter("year");
        String pageRaw  = request.getParameter("page");

        LocalDate today = LocalDate.now();
        int month = (monthRaw == null || monthRaw.isEmpty())
                ? today.getMonthValue()
                : Integer.parseInt(monthRaw);
        int year  = (yearRaw == null || yearRaw.isEmpty())
                ? today.getYear()
                : Integer.parseInt(yearRaw);
        int page  = (pageRaw == null || pageRaw.isEmpty()) ? 1 : Integer.parseInt(pageRaw);
        int pageSize = 10;

        int totalRecords = dao.countMonthlyStats(month, year);
        int totalPages   = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages < 1) totalPages = 1;

        List<AttendanceStats> statsList = dao.getMonthlyStats(month, year, page, pageSize);

        request.setAttribute("statsList",    statsList);
        request.setAttribute("month",        month);
        request.setAttribute("year",         year);
        request.setAttribute("totalPages",   totalPages);
        request.setAttribute("currentPage",  page);

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/attendance-stats.jsp")
                .forward(request, response);
    }
}
