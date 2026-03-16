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
@WebServlet(name = "GoodsReceiptController", urlPatterns = {"/admin/goodsreceipt"})
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
            case "getPoItems":
                loadPoItemsJson(request, response);
                break;
            case "view":
                showDetail(request, response);
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

        String redirectUrl = request.getContextPath() + "/admin/goodsreceipt?action=list";

        switch (action) {
            case "save":
                saveGR(request, response);
                break;
            case "complete":
                completeGR(request, response);
                break;
            case "cancel":
                cancelGR(request, response);
                break;
            default:
                response.sendRedirect(redirectUrl);
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

        resetSessionMsg(request);
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

        String currentUserName = (String) request.getSession().getAttribute("fullName");
        request.setAttribute("currentUserName", currentUserName != null ? currentUserName : "Unknown User");

        request.setAttribute("currentUserName", currentUserName);
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/AdminLTE-3.2.0/gr-form.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptNumber = request.getParameter("receiptNumber");
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=list");
            return;
        }

        GoodsReceipt gr = grDAO.getGRByNumber(receiptNumber);
        if (gr == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=list");
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
        resetSessionMsg(request);

        request.getRequestDispatcher("/AdminLTE-3.2.0/gr-form.jsp").forward(request, response);
    }

    private void completeGR(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptNumber = request.getParameter("receiptNumber");
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            request.getSession().setAttribute("msg", "fail");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=list");
            return;
        }

        boolean success = grDAO.completeGR(receiptNumber);
        if (success) {
            request.getSession().setAttribute("msg", "success_complete");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=view&receiptNumber=" + receiptNumber);
        } else {
            request.getSession().setAttribute("error", "Không thể hoàn tất phiếu nhập. Phiếu có thể đã hoàn tất hoặc không tồn tại.");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=view&receiptNumber=" + receiptNumber);
        }
    }

    private void saveGR(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String poIdParam = request.getParameter("poId");
        String receiptDateParam = request.getParameter("receiptDate");
        String notesParam = request.getParameter("notes");
        String[] poLineIds = request.getParameterValues("poLineItemId");
        String[] productIds = request.getParameterValues("productId");
        String[] quantitiesReceived = request.getParameterValues("quantityReceived");
        String[] unitCosts = request.getParameterValues("unitCost");
        String[] maxQtys = request.getParameterValues("maxQty");
        String[] itemNotes = request.getParameterValues("itemNote");

        Validation valid = new Validation();
        valid.required("Đơn đặt hàng", poIdParam).required("Ngày nhập", receiptDateParam);

        long poId = 0;

        LocalDateTime receiptDate = parseLocalDateTime(receiptDateParam);
        if (receiptDate != null && receiptDate.isAfter(LocalDateTime.now())) {
            valid.addError("Ngày nhập không được là ngày trong tương lai.");
        }

        if (poIdParam != null && !poIdParam.trim().isEmpty()) {
            try {
                poId = Long.parseLong(poIdParam);
            } catch (NumberFormatException e) {
                valid.addError("Mã đơn đặt hàng không hợp lệ.");
            }
        }

        if (poId > 0 && !grDAO.isPoAvailableForGR(poId)) {
            valid.addError("Đơn đặt hàng không hợp lệ hoặc đã hoàn tất.");
        }

        if (poId > 0 && valid.isValid() && grDAO.hasPendingGRForPO(poId)) {
            valid.addError("Đơn đặt hàng này đã có phiếu nhập đang xử lý (PENDING). Vui lòng hoàn tất hoặc hủy phiếu đó trước.");
        }

        if (poLineIds == null || poLineIds.length == 0) {
            valid.addError("Không có sản phẩm nào để nhập kho.");
        }

        boolean hasPositiveQty = false;

        if (poLineIds != null) {
            for (int i = 0; i < poLineIds.length; i++) {
                int qty = parseIntSafe(quantitiesReceived != null && i < quantitiesReceived.length ? quantitiesReceived[i] : "0");
                if (qty < 0) {
                    valid.addError("Số lượng nhận không được âm.");
                    break;
                }

                if (maxQtys != null && i < maxQtys.length) {
                    int maxAllowed = parseIntSafe(maxQtys[i]);
                    if (qty > maxAllowed) {
                        valid.addError("Số lượng nhận vượt quá số lượng còn lại cần nhập (còn: " + maxAllowed + ").");
                        break;
                    }
                }

                if (qty > 0) {
                    hasPositiveQty = true;
                }
            }
        }

        if (!hasPositiveQty && valid.isValid()) {
            valid.addError("Phải nhập ít nhất 1 sản phẩm với số lượng > 0.");
        }

        if (!valid.isValid()) {
            request.getSession().setAttribute("error", valid.getFirstError());
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=create&poId=" + poIdParam);
            return;
        }

        //build gr
        GoodsReceipt gr = new GoodsReceipt();
        gr.setReceiptNumber(grDAO.generateNextGRNumber());
        gr.setPoId(poId);
        gr.setNotes(notesParam);
        gr.setReceiptDate(receiptDate != null ? receiptDate : LocalDateTime.now());

        int receivedBy = getLoggedInEmployeeId(request);
        gr.setReceivedBy(receivedBy);

        for (int i = 0; i < poLineIds.length; i++) {
            int qty = parseIntSafe(quantitiesReceived != null && i < quantitiesReceived.length
                    ? quantitiesReceived[i] : "0");
            if (qty <= 0) {
                continue;
            }

            GoodsReceiptDetail d = new GoodsReceiptDetail();
            d.setPoLineItemId(parseLongSafe(poLineIds[i]));
            d.setProductId(parseIntSafe(productIds != null && i < productIds.length ? productIds[i] : "0"));
            d.setQuantityReceived(qty);
            BigDecimal cost = parseBigDecimalSafe(unitCosts != null && i < unitCosts.length ? unitCosts[i] : "0");
            if (cost.compareTo(BigDecimal.ZERO) <= 0) {
                request.getSession().setAttribute("error", "Đơn giá sản phẩm phải lớn hơn 0.");
                response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=create&poId=" + poIdParam);
                return;
            }
            d.setUnitCost(cost);

            d.calculateLineTotal();
            d.setNotes(itemNotes != null && i < itemNotes.length ? itemNotes[i] : null);
            gr.addDetail(d);
        }

        gr.recalculateTotals();
        boolean success = grDAO.createGR(gr);

        if (success) {
            request.getSession().setAttribute("msg", "success_create");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=view&receiptNumber=" + gr.getReceiptNumber());
        } else {
            request.getSession().setAttribute("error", "Lỗi khi tạo phiếu nhập kho. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=create&poId=" + poIdParam);
        }
    }

    private void cancelGR(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String receiptNumber = request.getParameter("receiptNumber");
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=list");
            return;
        }
        boolean success = grDAO.cancelGR(receiptNumber);
        if (success) {
            request.getSession().setAttribute("msg", "success_cancel");
        } else {
            request.getSession().setAttribute("error", "Không thể hủy phiếu. Phiếu phải ở trạng thái 'Đang nhập'.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/goodsreceipt?action=list");
    }

    private int getLoggedInEmployeeId(HttpServletRequest request) {
        Object emp = request.getSession().getAttribute("employeeId");
        return (emp instanceof Integer) ? (Integer) emp : 1;
    }

    private boolean isManagerOrAdmin(HttpServletRequest request) {
        String role = (String) request.getSession().getAttribute("roleName");
        return "Manager".equals(role) || "Store Manager".equals(role) || "Admin".equals(role);
    }

    private void resetSessionMsg(HttpServletRequest request) {
        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            request.getSession().removeAttribute("msg");
        }
    }

    // --- Format Helpers ---
    private void loadPoItemsJson(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String poIdParam = request.getParameter("poId");
        if (poIdParam == null || poIdParam.trim().isEmpty()) {
            response.getWriter().write("[]");
            return;
        }
        try {
            long poId = Long.parseLong(poIdParam);
            List<GoodsReceiptDetail> items = grDAO.getPoItemsForGR(poId);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < items.size(); i++) {
                GoodsReceiptDetail d = items.get(i);
                if (i > 0) {
                    json.append(",");
                }
                json.append("{");
                json.append("\"poLineItemId\":").append(d.getPoLineItemId()).append(",");
                json.append("\"productId\":").append(d.getProductId()).append(",");
                json.append("\"productName\":\"").append(escapeJson(d.getProductName())).append("\",");
                json.append("\"quantityOrdered\":").append(d.getQuantityOrdered()).append(",");
                json.append("\"remaining\":").append(d.getQuantityReceived()).append(",");
                json.append("\"unitCost\":").append(d.getUnitCost() != null ? d.getUnitCost().toPlainString() : "0").append(",");
                json.append("\"lineTotal\":").append(d.getLineTotal() != null ? d.getLineTotal().toPlainString() : "0");
                json.append("}");
            }
            json.append("]");
            response.getWriter().write(json.toString());
        } catch (Exception e) {
            response.getWriter().write("[]");
        }
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
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
