/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.StockDisposalDAO;
import entity.StockDisposal;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author qp
 */
@WebServlet(name = "StockDisposalController", urlPatterns = {"/stockdisposal"})
public class StockDisposalController extends HttpServlet {

    private final StockDisposalDAO sdDAO = new StockDisposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }
        switch (action) {
            case "list":
            case "search":
                showList(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        switch (action) {
            default:
                response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("key");
        String status = request.getParameter("status");
        String reason = request.getParameter("reason");
        LocalDate from = parseLocalDate(request.getParameter("from"));
        LocalDate to = parseLocalDate(request.getParameter("to"));

        int page = parseIntSafe(request.getParameter("page"));
        if (page <= 0) {
            page = 1;
        }
        int pageSize = 10;

        int total = sdDAO.countSD(keyword, status, reason, from, to);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));

        List<StockDisposal> list = sdDAO.searchSDWithPaginated(keyword, status, reason, from, to, page, pageSize);

        request.setAttribute("lists", list);
        request.setAttribute("totalRecords", total);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/sd-list.jsp").forward(request, response);
    }
    
    private int getLoggedInEmployeeId(HttpServletRequest request){
        Object emp = request.getSession().getAttribute("employeeId");
        return (emp instanceof Integer) ? (Integer) emp : 1;    //hard code
    }
    
    private void resetSessionMsg(HttpServletRequest request){
        String msg = (String) request.getSession().getAttribute("msg");
        if(msg!=null){
            request.setAttribute("msg", msg);
            request.getSession().removeAttribute("msg");
        }
    }
    private LocalDate parseLocalDate(String value) {
        try {
            if (value != null && !value.isBlank()) {
                return LocalDate.parse(value);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
        }
        return 0;
    }
}
