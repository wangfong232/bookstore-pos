package controller;

import DAO.EmployeeDAO;
import DAO.ShiftAssignmentDAO;
import DAO.ShiftDAO;
import entity.Employee;
import entity.EmployeeShiftAssignment;
import entity.Shift;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;

@WebServlet("/admin/shift-management")
public class ShiftManagementController extends HttpServlet {

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        EmployeeDAO empDAO = new EmployeeDAO();
        // ===== LẤY TOÀN BỘ NHÂN VIÊN ACTIVE CHO MODAL =====
        List<Employee> allEmployees = empDAO.getEmployees(null, null, "ACTIVE", 1, Integer.MAX_VALUE);
        request.setAttribute("allEmployees", allEmployees);
        ShiftDAO shiftDAO = new ShiftDAO();
        ShiftAssignmentDAO assignDAO = new ShiftAssignmentDAO();   // thêm dòng này

        //String search = request.getParameter("search");
        String status = request.getParameter("status");

        String viewMode = request.getParameter("view");

        if (viewMode == null) {
            viewMode = "daily";
        }
        if (status == null || status.isEmpty()) {
            status = "ACTIVE";
        }

//        int page = 1;
//        int pageSize = 5;
//
//        if (request.getParameter("page") != null) {
//            page = Integer.parseInt(request.getParameter("page"));
//        }
//
//        List<Employee> employees
//                = empDAO.getEmployees(search, null, status, page, pageSize);
//
//        int total = empDAO.getTotalEmployees(search, null, status);
//        int totalPage = (int) Math.ceil(total * 1.0 / pageSize);
//
//        request.setAttribute("employees", employees);
//        request.setAttribute("totalPages", totalPage);
//        request.setAttribute("currentPage", page);
        //View calendar
        request.setAttribute("viewMode", viewMode);

        // ===== LẤY DANH SÁCH SHIFT =====
        List<Shift> shifts = shiftDAO.getAllShifts();
        request.setAttribute("shifts", shifts);

        // ===== THÊM PHẦN NÀY =====
        Date today = new Date(System.currentTimeMillis());

        if (request.getParameter("viewDate") != null) {
            today = Date.valueOf(request.getParameter("viewDate"));
        }

        List<EmployeeShiftAssignment> assignments
                = assignDAO.getAssignmentsByDate(today);

        request.setAttribute("assignments", assignments);
        request.setAttribute("viewDate", today);
        // ==============================

        // ===== VIEW CALENDAR THEO THÁNG =====
        if ("calendar".equals(viewMode)) {

            LocalDate now = LocalDate.now();
            int month = now.getMonthValue();
            int year = now.getYear();

            String monthPicker = request.getParameter("monthPicker");

            if (monthPicker != null && !monthPicker.isEmpty()) {
                String[] parts = monthPicker.split("-");
                year = Integer.parseInt(parts[0]);
                month = Integer.parseInt(parts[1]);
            }

            YearMonth ym = YearMonth.of(year, month);

            int daysInMonth = ym.lengthOfMonth();
            int firstDayOfWeek = ym.atDay(1).getDayOfWeek().getValue();

            List<EmployeeShiftAssignment> monthAssignments
                    = assignDAO.getAssignmentsByMonth(month, year);

            request.setAttribute("calendarAssignments", monthAssignments);
            request.setAttribute("month", month);
            request.setAttribute("year", year);
            request.setAttribute("daysInMonth", daysInMonth);
            request.setAttribute("firstDayOfWeek", firstDayOfWeek);
        }

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/admin-shift-management.jsp")
                .forward(request, response);
    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String empIDsParam = request.getParameter("employeeIDs");

        if (empIDsParam == null || empIDsParam.isEmpty()) {
            response.sendRedirect("shift-management?error=noemployee");
            return;
        }

        String[] empIDs = empIDsParam.split(",");
        int shiftID = Integer.parseInt(request.getParameter("shiftID"));
        ShiftAssignmentDAO dao = new ShiftAssignmentDAO();
        Date today = new Date(System.currentTimeMillis());

// LẤY CÁC INPUT
        String dayParam = request.getParameter("workDate");
        String weekParam = request.getParameter("weekInput");
        String monthParam = request.getParameter("monthInput");

// ===== THEO NGÀY =====
        if (dayParam != null && !dayParam.isEmpty()) {

            Date workDate = Date.valueOf(dayParam);

            if (workDate.before(today)) {
                response.sendRedirect("shift-management?error=pastdate");
                return;
            }

            for (String idStr : empIDs) {

                int empID = Integer.parseInt(idStr.trim());

                // Giới hạn 8 người 1 ca
                if (dao.countEmployeesInShift(shiftID, workDate) >= 8) {
                    break;
                }

                dao.assignShift(empID, shiftID, workDate, 1);
            }

            response.sendRedirect("shift-management?viewDate=" + workDate + "&success=1");
            return;
        }

// ===== THEO TUẦN =====
        if (weekParam != null && !weekParam.isEmpty()) {

            String[] parts = weekParam.split("-W");
            int year = Integer.parseInt(parts[0]);
            int week = Integer.parseInt(parts[1]);

            LocalDate startOfWeek = LocalDate
                    .ofYearDay(year, 1)
                    .with(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR, week)
                    .with(java.time.DayOfWeek.MONDAY);

            for (int i = 0; i < 7; i++) {

                LocalDate date = startOfWeek.plusDays(i);

                if (!date.isBefore(LocalDate.now())) {

                    Date sqlDate = Date.valueOf(date);

                    for (String idStr : empIDs) {

                        int empID = Integer.parseInt(idStr.trim());

                        if (dao.countEmployeesInShift(shiftID, sqlDate) >= 8) {
                            break;
                        }

                        dao.assignShift(empID, shiftID, sqlDate, 1);
                    }
                }
            }

            response.sendRedirect("shift-management?success=1");
            return;
        }

// ===== THEO THÁNG =====
        if (monthParam != null && !monthParam.isEmpty()) {

            YearMonth ym = YearMonth.parse(monthParam);

            for (int i = 1; i <= ym.lengthOfMonth(); i++) {

                LocalDate date = ym.atDay(i);

                if (!date.isBefore(LocalDate.now())) {

                    Date sqlDate = Date.valueOf(date);

                    for (String idStr : empIDs) {

                        int empID = Integer.parseInt(idStr.trim());

                        if (dao.countEmployeesInShift(shiftID, sqlDate) >= 8) {
                            break;
                        }

                        dao.assignShift(empID, shiftID, sqlDate, 1);
                    }
                }
            }

            response.sendRedirect("shift-management?success=1");
            return;
        }

    }
}
