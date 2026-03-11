/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.StockDisposalDAO;
import entity.Product;
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
            case "view":
                showView(request, response);
                break;
            case "create":
                showCreateForm(request, response);
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
            case "approve":
                approve(request, response);
                break;
            case "reject":
                reject(request, response);
                break;
            case "complete":
                complete(request, response);
                break;
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

    private void showView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String number = request.getParameter("number");
        if (number == null || number.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
            return;
        }
        request.setAttribute("sd", sd);
        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/sd-view.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> productList = sdDAO.getAllActiveProducts();
        String sdNumber = sdDAO.generateNextSDNumber();
        String today = LocalDate.now().toString();

        request.setAttribute("productList", productList);
        request.setAttribute("sdNumber", sdNumber);
        request.setAttribute("today", today);

        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/sd-form.jsp").forward(request, response);
    }

    private void approve(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        int employeeId = getLoggedInEmployeeId(request);

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
            return;
        }

        if (sd.getCreatedBy() != null && sd.getCreatedBy() == employeeId) {
            request.getSession().setAttribute("msg", "fail_self_approve");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?aciton=view&number=" + number);
            return;
        }

        boolean ok = sdDAO.approveDisposal(sd.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_approve" : "fail_approve");
        response.sendRedirect(request.getContextPath() + "/stockdisposal?action=view&number=" + number);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        String reason = request.getParameter("rejectionReason");
        int employeeId = getLoggedInEmployeeId(request);

        if (reason == null || reason.trim().isEmpty()) {
            request.getSession().setAttribute("msg", "fail_reject_reason");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?aciton=view&number=" + number);
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
            return;
        }

        boolean ok = sdDAO.rejectDisposal(sd.getId(), employeeId, reason.trim());
        request.getSession().setAttribute("msg", ok ? "success_reject" : "fail_reject");
        response.sendRedirect(request.getContextPath() + "/stockdisposal?action=view&number=" + number);
    }

    private void complete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        String confirmed = request.getParameter("physicalConfirmed");
        int employeeId = getLoggedInEmployeeId(request);

        if (!"true".equals(confirmed)) {
            request.getSession().setAttribute("msg", "fail_no_physical_confirm");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=view&number=" + number);
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/stockdisposal?action=list");
            return;
        }
        boolean ok = sdDAO.completeDisposal(sd.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_complete" : "fail_complete");
        response.sendRedirect(request.getContextPath() + "/stockdisposal?action=view&number=" + number);
    }

    private int getLoggedInEmployeeId(HttpServletRequest request) {
        Object emp = request.getSession().getAttribute("employeeId");
        return (emp instanceof Integer) ? (Integer) emp : 1;    //hard code
    }

    private void resetSessionMsg(HttpServletRequest request) {
        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
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
