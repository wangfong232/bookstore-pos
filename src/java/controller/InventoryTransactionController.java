/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.InventoryTransactionDAO;
import entity.InventoryTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "InventoryTransactionController", urlPatterns = {"/admin/inventorytransaction"})
public class InventoryTransactionController extends HttpServlet {
    private final InventoryTransactionDAO itDAO = new InventoryTransactionDAO();

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
            default:
                showList(request, response);
        }
    }
    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword     = request.getParameter("key");
        String txType      = request.getParameter("txType");
        LocalDate from     = parseLocalDate(request.getParameter("from"));
        LocalDate to       = parseLocalDate(request.getParameter("to"));

        int page = parseIntSafe(request.getParameter("page"));
        if (page <= 0) {
            page = 1;
        }
        int pageSize = 20;

        int total      = itDAO.count(keyword, txType, null, from, to);
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        if (page > totalPages) {
            page = totalPages;
        }

        List<InventoryTransaction> list = itDAO.searchTransactionWithPaginated(keyword, txType, null, from, to, page, pageSize);

        String ctx = request.getContextPath();
        for (InventoryTransaction tx : list) {
            tx.setReferenceUrl(buildReferenceUrl(ctx, tx.getReferenceType(), tx.getReferenceCode()));
        }

        request.setAttribute("lists", list);
        request.setAttribute("totalRecords", total);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/it-list.jsp").forward(request, response);
    }

    private String buildReferenceUrl(String ctx, String refType, String refCode) {
        if (refCode == null || refCode.isBlank() || refType == null) {
            return "";
        }
        switch (refType) {
            case InventoryTransaction.REF_DISPOSAL:
                return ctx + "/admin/stockdisposal?action=view&number=" + refCode;
            case InventoryTransaction.REF_STOCK_TAKE:
                return ctx + "/admin/stocktake?action=view&number=" + refCode;
            case InventoryTransaction.REF_GOODS_RECEIPT:
                return ctx + "/admin/goodsreceipt?action=view&receiptNumber=" + refCode;
//            case InventoryTransaction.REF_SALE:
//                return ctx + "/pos?action=view&number=" + refCode;
            default: 
                return "";
        }
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
