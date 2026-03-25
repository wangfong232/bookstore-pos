package controller;

import DAO.SalesInvoiceDAO;
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

@WebServlet(urlPatterns = {"/sales-invoices"})
public class SalesInvoiceController extends HttpServlet {

    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fromStr = request.getParameter("fromDate");
        String toStr = request.getParameter("toDate");
        String keyword = request.getParameter("q");

        LocalDate today = LocalDate.now();
        LocalDate from = null;
        LocalDate to = null;

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

        if (from == null && to == null) {
            to = today;
            from = today.minusDays(30);
        } else if (from == null) {
            from = to.minusDays(30);
        } else if (to == null) {
            to = from.plusDays(30);
        }

        if (from.isAfter(to)) {
            LocalDate tmp = from;
            from = to;
            to = tmp;
        }

        Date fromDate = Date.valueOf(from);
        Date toDate = Date.valueOf(to);

        List<Map<String, Object>> invoices = salesInvoiceDAO.searchInvoices(fromDate, toDate, keyword, 100);
        request.setAttribute("invoices", invoices);

        request.setAttribute("fromDate", from.toString());
        request.setAttribute("toDate", to.toString());
        request.setAttribute("keyword", keyword != null ? keyword : "");

        request.getRequestDispatcher("/AdminLTE-3.2.0/sales-invoice-list.jsp").forward(request, response);
    }
}

