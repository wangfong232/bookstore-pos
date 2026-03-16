/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.AttendanceDAO;
import entity.AttendanceView;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
@WebServlet(urlPatterns = { "/admin/attendance" })
public class AttendanceController extends HttpServlet {

    private AttendanceDAO dao = new AttendanceDAO();

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listAttendance(request, response);
        }
    }

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
}
