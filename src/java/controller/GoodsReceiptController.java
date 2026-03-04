/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.GoodsReceiptDAO;
import entity.GoodsReceipt;
import entity.GoodsReceiptDetail;
import entity.PurchaseOrder;
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
import util.Validation;

/**
 *
 * @author qp
 */
@WebServlet(name = "GoodsReceiptController", urlPatterns = {"/goodsreceipt"})
public class GoodsReceiptController extends HttpServlet {

    private final GoodsReceiptDAO grDAO = new GoodsReceiptDAO();

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
            case "create":
                showCreateForm(request, response);
                break;
            case "detail":
                showDetail(request, response);
                break;
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
            case "save":
//                saveGR(request, response);
                break;
            case "complete":
//                completeGR(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=list");
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

        int totalRecords = grDAO.countGRs(keyword, status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages < 1) {
            totalPages = 1;
        }

        List<GoodsReceipt> list = grDAO.searchGRsWithPaginated(keyword, status, fromDate, toDate, page, pageSize);

        request.setAttribute("lists", list);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalRecords", totalRecords);

        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            request.getSession().removeAttribute("msg");
        }
        request.getRequestDispatcher("/AdminLTE-3.2.0/gr-list.jsp").forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<PurchaseOrder> poList = grDAO.getApprovedPOs();
        request.setAttribute("poList", poList);

        String grNumber = grDAO.generateNextGRNumber();
        request.setAttribute("grNumber", grNumber);

        String poId = request.getParameter("poId");
        if (poId != null && !poId.trim().isEmpty()) {
            try {
                long pid = Long.parseLong(poId);
                List<GoodsReceiptDetail> items = grDAO.getPoItemsForGR(pid);
                request.setAttribute("grItems", items);
                request.setAttribute("selectedPoId", pid);

                for (PurchaseOrder po : poList) {
                    if (po.getId().equals(pid)) {
                        request.setAttribute("selectedPo", po);
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }

        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            request.getSession().removeAttribute("error");
        }
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/AdminLTE-3.2.0/gr-form.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptNumber = request.getParameter("receiptNumber");
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            request.setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=list");
            return;
        }

        GoodsReceipt gr = grDAO.getGRByNumber(receiptNumber);
        if (gr == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=list");
            return;
        }

        List<GoodsReceiptDetail> items = grDAO.getGRDetailsByReceiptId(gr.getId());
        gr.setDetails(items);

        request.setAttribute("gr", gr);
        request.setAttribute("items", items);

        request.setAttribute("mode", GoodsReceipt.STATUS_COMPLETED.equals(gr.getStatus()) ? "view" : "pending");
        String error = (String) request.getSession().getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            request.getSession().removeAttribute("error");
        }

        request.getRequestDispatcher("/AdminLTE-3.2.0/gr-form.jsp").forward(request, response);
    }
    
    private void completeGR(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptNumber = request.getParameter("receiptNumber");
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            request.getSession().setAttribute("msg", "fail");
            response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=list");
            return;
        }

        boolean success = grDAO.completeGR(receiptNumber);
        if (success) {
            request.getSession().setAttribute("msg", "success_complete");
            response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=detail&receiptNumber=" + receiptNumber);
        } else {
            request.getSession().setAttribute("error", "Không thể hoàn tất phiếu nhập. Phiếu có thể đã hoàn tất hoặc không tồn tại.");
            response.sendRedirect(request.getContextPath() + "/goodsreceipt?action=detail&receiptNumber=" + receiptNumber);
        }
    }

//helper
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
