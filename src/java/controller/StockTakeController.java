/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.StockTakeDAO;
import entity.StockTake;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author qp
 */
@WebServlet(name = "StockTakeController", urlPatterns = {"/stocktake"})
public class StockTakeController extends HttpServlet {

    private final StockTakeDAO stDAO = new StockTakeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "list";
        }

        switch (action) {
            case "list":
            case "search":
                showList(request, response);
                break;
            case "view":
                showView(request, response);
            default:
                showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        switch (action) {
            default:
                response.sendRedirect(request.getContextPath() + "/stocktake?action=list");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("key");
        String status = request.getParameter("status");
        String from = request.getParameter("from");
        String to = request.getParameter("to");

        LocalDate fromDate = parseLocalDate(from);
        LocalDate toDate = parseLocalDate(to);

        int page = 1;
        int pageSize = 10;
        try {
            String p = request.getParameter("page");
            if (p != null) {
                page = Integer.parseInt(p);
            }
        } catch (NumberFormatException ignored) {
        }

        int total = stDAO.count(keyword, status, fromDate, toDate);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        if (totalPages < 1) {
            totalPages = 1;
        }

        List<StockTake> list = stDAO.searchWithPaginated(keyword, status, fromDate, toDate, page, pageSize);

        request.setAttribute("lists", list);
        request.setAttribute("totalRecords", total);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);

        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/st-list.jsp").forward(request, response);
    }

    private void showView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String number = request.getParameter("number");
        if (number == null || number.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/stocktake?action=list");
            return;
        }
    
        StockTake st = stDAO.getSTByNumber(number);
        if(st==null){
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath()+"/stocktake?action=list");
            return;
        }
        
        request.setAttribute("st", st);
        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/st-view.jsp").forward(request, response);
    }

    private void resetSessionMsg(HttpServletRequest request) {
        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            request.getSession().removeAttribute("msg");
        }
        String err = (String) request.getSession().getAttribute("error");
        if (err != null) {
            request.setAttribute("error", err);
            request.getSession().removeAttribute("error");
        }
    }

    private LocalDate parseLocalDate(String value) {
        try {
            if (value != null && !value.trim().isEmpty()) {
                return LocalDate.parse(value);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private LocalDateTime parseLocalDateTime(String value) {
        try {
            if (value != null && !value.trim().isEmpty()) {
                return LocalDateTime.parse(value.length() == 16 ? value + ":00" : value);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private long parseLongSafe(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            return 0L;
        }
    }

    private BigDecimal parseBigDecimalSafe(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
