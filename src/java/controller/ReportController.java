/*
 * Controller for Reports - Báo cáo Doanh thu, Tồn kho, Sản phẩm bán chạy
 */
package controller;

import DAO.RevenueReportDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/report/revenue"})
public class ReportController extends HttpServlet {

    private final RevenueReportDAO revenueDAO = new RevenueReportDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fromStr = request.getParameter("fromDate");
        String toStr = request.getParameter("toDate");

        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(30);
        LocalDate to = today;
        try {
            if (fromStr != null && !fromStr.isBlank()) {
                from = LocalDate.parse(fromStr.trim());
            }
        } catch (DateTimeParseException ignored) {
        }
        try {
            if (toStr != null && !toStr.isBlank()) {
                to = LocalDate.parse(toStr.trim());
            }
        } catch (DateTimeParseException ignored) {
        }

        if (from.isAfter(to)) {
            from = to.minusDays(30);
        }

        Date fromDate = Date.valueOf(from);
        Date toDate = Date.valueOf(to);

        // 1. Overview stats
        Map<String, Object> overview = revenueDAO.getOverviewStats(fromDate, toDate);
        request.setAttribute("overview", overview);

        // 2. Revenue by date (for chart)
        List<Map<String, Object>> revenueByDate = revenueDAO.getRevenueByDate(fromDate, toDate);
        request.setAttribute("revenueByDate", revenueByDate);

        // 4. Revenue by staff
        List<Map<String, Object>> revenueByStaff = revenueDAO.getRevenueByStaff(fromDate, toDate);
        request.setAttribute("revenueByStaff", revenueByStaff);

        // 4. Revenue by shift
        List<Map<String, Object>> revenueByShift = revenueDAO.getRevenueByShift(fromDate, toDate);
        request.setAttribute("revenueByShift", revenueByShift);

        // 6. Revenue by category
        List<Map<String, Object>> revenueByCategory = revenueDAO.getRevenueByCategory(fromDate, toDate);
        request.setAttribute("revenueByCategory", revenueByCategory);

        request.setAttribute("fromDate", from);
        request.setAttribute("toDate", to);

        request.getRequestDispatcher("/AdminLTE-3.2.0/report/revenue-report.jsp").forward(request, response);
    }
}
