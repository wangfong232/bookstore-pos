/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.StockDisposalDAO;
import entity.Product;
import entity.StockDisposal;
import entity.StockDisposalDetail;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qp
 */
@WebServlet(name = "StockDisposalController", urlPatterns = {"/admin/stockdisposal"})
public class StockDisposalController extends HttpServlet {

    private final StockDisposalDAO sdDAO = new StockDisposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (denyIfSaler(request, response)) {
            return;
        }

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
                break;
            default:
                showList(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (denyIfSaler(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        String redirectUrl = request.getContextPath() + "/admin/stockdisposal?action=list";

        switch (action) {
            case "save":
                saveDisposal(request, response);
                break;
            case "approve":
                if (!isManagerOrAdmin(request)) {
                    request.getSession().setAttribute("msg", "access_denied");
                    response.sendRedirect(redirectUrl);
                    return;
                }
                approve(request, response);
                break;
            case "reject":
                if (!isManagerOrAdmin(request)) {
                    request.getSession().setAttribute("msg", "access_denied");
                    response.sendRedirect(redirectUrl);
                    return;
                }
                reject(request, response);
                break;
            case "complete":
                complete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
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
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
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
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
            return;
        }

        if (sd.getCreatedBy() != null && sd.getCreatedBy() == employeeId) {
            request.getSession().setAttribute("msg", "fail_self_approve");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?aciton=view&number=" + number);
            return;
        }

        boolean ok = sdDAO.approveDisposal(sd.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_approve" : "fail_approve");
        response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=view&number=" + number);
    }

    private void reject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        String reason = request.getParameter("rejectionReason");
        int employeeId = getLoggedInEmployeeId(request);

        if (reason == null || reason.trim().isEmpty()) {
            request.getSession().setAttribute("msg", "fail_reject_reason");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?aciton=view&number=" + number);
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
            return;
        }

        boolean ok = sdDAO.rejectDisposal(sd.getId(), employeeId, reason.trim());
        request.getSession().setAttribute("msg", ok ? "success_reject" : "fail_reject");
        response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=view&number=" + number);
    }

    private void complete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        String confirmed = request.getParameter("physicalConfirmed");
        int employeeId = getLoggedInEmployeeId(request);

        if (!"true".equals(confirmed)) {
            request.getSession().setAttribute("msg", "fail_no_physical_confirm");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=view&number=" + number);
            return;
        }

        StockDisposal sd = sdDAO.getSDByNumber(number);
        if (sd == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=list");
            return;
        }
        boolean ok = sdDAO.completeDisposal(sd.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_complete" : "fail_complete");
        response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=view&number=" + number);
    }

    private void saveDisposal(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int createdBy = getLoggedInEmployeeId(request);
        String sdNumber = request.getParameter("sdNumber");
        String disposalReason = request.getParameter("disposalReason");
        String notes = request.getParameter("notes");

        if (disposalReason == null || disposalReason.isBlank()) {
            request.getSession().setAttribute("msg", "fail_noreason");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
            return;
        }

        List<Product> allProducts = sdDAO.getAllActiveProducts();
        Map<Integer, Product> productMap = new HashMap<>();
        for (Product p : allProducts) {
            productMap.put(p.getId(), p);
        }

        String[] pids = request.getParameterValues("pid[]");
        String[] dispQtys = request.getParameterValues("dispQty[]");
        String[] specificReasons = request.getParameterValues("specificReason[]");

        if (pids == null || pids.length == 0) {
            request.getSession().setAttribute("msg", "fail_noproduct");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
            return;
        }

        StockDisposal sd = new StockDisposal(sdNumber, disposalReason, createdBy);
        sd.setDisposalDate(LocalDateTime.now());
        sd.setNotes((notes != null && !notes.isBlank()) ? notes : null);

        for (int i = 0; i < pids.length; i++) {
            int productId = parseIntSafe(pids[i]);
            Product p = productMap.get(productId);
            if (p == null) {
                continue;
            }

            int qty = parseIntSafe(dispQtys != null && i < dispQtys.length ? dispQtys[i] : "0");
            if (qty <= 0) {
                request.getSession().setAttribute("msg", "fail_invalid_qty");
                response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
                return;
            }
            int availableStock = p.getAvailableStock();
            if (qty > availableStock) {
                request.getSession().setAttribute("msg", "fail_exceed_stock:" + p.getProductName());
                response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
                return;
            }

            BigDecimal unitCost = p.getCostPrice() != null
                    ? BigDecimal.valueOf(p.getCostPrice()) : BigDecimal.ZERO;

            StockDisposalDetail detail = new StockDisposalDetail(productId, qty, unitCost);
            detail.calculateLineTotal();

            String specReason = (specificReasons != null && i < specificReasons.length)
                    ? specificReasons[i] : null;
            if (specReason != null && !specReason.isBlank()) {
                detail.setSpecificReason(specReason.trim());
            }
            sd.addDetail(detail);
        }

        if (sd.getDetails().isEmpty()) {
            request.getSession().setAttribute("msg", "fail_noproduct");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
            return;
        }

        sd.recalculateTotals();

        boolean ok = sdDAO.createDisposalWithDetails(sd);
        if (ok) {
            request.getSession().setAttribute("msg", "success_save");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=view&number=" + sdNumber);
        } else {
            request.getSession().setAttribute("msg", "fail_save");
            response.sendRedirect(request.getContextPath() + "/admin/stockdisposal?action=create");
        }
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

    private boolean isManagerOrAdmin(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("roleName");
        return "Manager".equals(role) || "Store Manager".equals(role) || "Admin".equals(role);
    }

    private boolean denyIfSaler(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String role = (String) request.getSession().getAttribute("roleName");
        if ("Saler".equals(role)) {
            request.getSession().setAttribute("msg", "access_denied_saler");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return true;
        }
        return false;
    }
}
