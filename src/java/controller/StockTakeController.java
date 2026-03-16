/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.CategoryDAO;
import DAO.StockTakeDAO;
import entity.Category;
import entity.Product;
import entity.StockTake;
import entity.StockTakeDetail;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author qp
 */
@WebServlet(name = "StockTakeController", urlPatterns = {"/admin/stocktake"})
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
                break;
            case "create":
                showCreateStep1(request, response);
                break;
            case "step2":
                showCreateStep2(request, response);
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
                saveStockTake(request, response);
                break;
            case "submit":
                submitForApproval(request, response);
                break;
            case "approve":
                if (!requireManager(request, response, request.getContextPath() + "/admin/stocktake?action=view&number=" + request.getParameter("number"))) {
                    return;
                }
                approve(request, response);
                break;
            case "recount":
                if (!requireManager(request, response, request.getContextPath() + "/admin/stocktake?action=view&number=" + request.getParameter("number"))) {
                    return;
                }
                requestRecount(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
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
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }

        StockTake st = stDAO.getSTByNumber(number);
        if (st == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }
        String scopeValue = st.getScopeValue();
        if ("CATEGORY".equals(st.getScopeType()) && scopeValue != null && !scopeValue.isBlank()) {
            try {
                Category category = new CategoryDAO().getCategoryByID(Integer.parseInt(scopeValue));
                if (category != null) {
                    request.setAttribute("scopeValue", category.getCategoryName());
                }
            } catch (NumberFormatException ignored) {
            }
        }
        request.setAttribute("st", st);
        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/st-view.jsp").forward(request, response);
    }

    private void showCreateStep1(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> productList = stDAO.getAllActiveProducts();
        String stNumber = stDAO.generateNextSTNumber();
        String today = LocalDate.now().toString();

        request.setAttribute("productList", productList);
        request.setAttribute("categoryList", new CategoryDAO().getAllCategories());
        request.setAttribute("stNumber", stNumber);
        request.setAttribute("today", today);

        resetSessionMsg(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/st-form.jsp").forward(request, response);
    }

    private void showCreateStep2(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String scope = request.getParameter("scopeType");
        String stNumber = request.getParameter("stNumber");
        String dateStr = request.getParameter("stockTakeDate");
        String notes = request.getParameter("notes");
        String[] productIds = request.getParameterValues("selectedProducts");

        List<Product> allProducts = stDAO.getAllActiveProducts();

        List<Product> selected = new ArrayList<>();

        if (stNumber != null && !stNumber.isBlank() && productIds == null) {
            StockTake existingST = stDAO.getSTByNumber(stNumber);
            if (existingST != null && "IN_PROGRESS".equals(existingST.getStatus())) {
                request.setAttribute("productList", allProducts);
                request.setAttribute("categoryList", new CategoryDAO().getAllCategories());
                
                request.setAttribute("st", existingST);
                request.setAttribute("selectedProducts", existingST.getDetails());
                request.setAttribute("stNumber", existingST.getStockTakeNumber());
                request.setAttribute("stockTakeDate", existingST.getStockTakeDate());
                request.setAttribute("notes", existingST.getNotes());
                request.setAttribute("scopeType", existingST.getScopeType());
                request.setAttribute("mode", "edit");
                request.getRequestDispatcher("/AdminLTE-3.2.0/st-form.jsp").forward(request, response);
                return;
            }
        }

        if ("ALL".equals(scope)) {
            selected = allProducts;
        } else if (productIds != null) {
            for (String pid : productIds) {
                for (Product p : allProducts) {
                    if (String.valueOf(p.getId()).equals(pid)) {
                        selected.add(p);
                    }
                }
            }
        }

        request.setAttribute("selectedProducts", selected);
        request.setAttribute("stNumber", stNumber);
        request.setAttribute("stockTakeDate", dateStr);
        request.setAttribute("notes", notes);
        request.setAttribute("scopeType", scope);

        request.getRequestDispatcher("/AdminLTE-3.2.0/st-form.jsp").forward(request, response);
    }

    private void saveStockTake(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        int createdBy = getLoggedInEmployeeId(request);
        String stNumber = request.getParameter("stNumber");
        String dateStr = request.getParameter("stockTakeDate");
        String scope = request.getParameter("scopeType");
        String notes = request.getParameter("notes");

        LocalDate date = parseLocalDate(dateStr);
        if (date == null || !date.equals(LocalDate.now())) {
            request.getSession().setAttribute("msg", "fail_date");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }

        List<Product> allProducts = stDAO.getAllActiveProducts();
        Map<Integer, Product> productMap = new HashMap<>();
        for (Product p : allProducts) {
            productMap.put(p.getId(), p);
        }

        StockTake st = new StockTake(stNumber, date, createdBy);
        st.setScopeType(scope != null ? scope : StockTake.SCOPE_ALL);
        st.setScopeValue(request.getParameter("scopeValue"));
        st.setNotes(notes);

        String[] pids = request.getParameterValues("pid[]");
        String[] actQtys = request.getParameterValues("actQty[]");
        String[] reasons = request.getParameterValues("reason[]");
        String[] detNotes = request.getParameterValues("detailNotes[]");

        if (pids != null) {
            for (int i = 0; i < pids.length; i++) {
                int productId = parseIntSafe(pids[i]);
                Product p = productMap.get(productId);
                if (p == null) {
                    continue;
                }

                int sysQty = p.getStock();
                int actQty = parseIntSafe(actQtys[i]);
                if (actQty < 0) {
                    request.getSession().setAttribute("msg", "fail_invalid_qty");
                    response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
                    return;
                }
                BigDecimal unitCost = (p.getCostPrice() != null)
                        ? BigDecimal.valueOf(p.getCostPrice()) : BigDecimal.ZERO;

                String reason = (reasons != null && i < reasons.length) ? reasons[i] : null;
                String detNote = (detNotes != null && i < detNotes.length) ? detNotes[i] : null;

                StockTakeDetail d = new StockTakeDetail(productId, sysQty, unitCost);
                d.setActualQuantity(actQty);
                if (reason != null && !reason.trim().isEmpty()) {
                    d.setVarianceReason(reason);
                }
                if (detNote != null && !detNote.trim().isEmpty()) {
                    d.setNotes(detNote);
                }
                st.addDetail(d);
            }
        }
        st.recalculateSummary();

        StockTake existingST = stDAO.getSTByNumber(stNumber);
        boolean isUpdate = false;
        if (existingST != null) {
            if ("IN_PROGRESS".equals(existingST.getStatus())) {
                isUpdate = true;
                st.setId(existingST.getId());
            } else {
                request.getSession().setAttribute("msg", "fail_duplicate");
                response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
                return;
            }
        } else {
            String lockNumber = stDAO.getActiveLockSTNumber();
            if (lockNumber != null) {
                request.getSession().setAttribute("msg", "Không thể tạo phiếu kiểm kho mới. Phiếu " + lockNumber + " đang hoạt động.");
                response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
                return;
            }
        }
        boolean ok;
        if (isUpdate) {
            ok = stDAO.updateSTDetails(st);
        } else {
            ok = stDAO.createSTWithDetails(st);
        }

        if (ok) {
            request.getSession().setAttribute("msg", isUpdate ? "success_update" : "success_save");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + stNumber);
        } else {
            request.getSession().setAttribute("msg", isUpdate ? "fail_update" : "fail_save");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
        }
    }

    private void submitForApproval(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        int employeeId = getLoggedInEmployeeId(request);

        StockTake st = stDAO.getSTByNumber(number);
        if (st == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }
        boolean ok = stDAO.submitSTForApproval(st.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_submit" : "fail_submit");
        response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + number);
    }

    private void approve(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String number = request.getParameter("number");
        int employeeId = getLoggedInEmployeeId(request);

        StockTake st = stDAO.getSTByNumber(number);
        if (st == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }

        if (st.getCreatedBy() != null && st.getCreatedBy() == employeeId) {
            request.getSession().setAttribute("msg", "fail_self_approve");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + number);
            return;
        }

        boolean ok = stDAO.approveST(st.getId(), employeeId);
        request.getSession().setAttribute("msg", ok ? "success_approve" : "fail_approve");
        response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + number);
    }

    private void requestRecount(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        String number = request.getParameter("number");
        String reason = request.getParameter("recountReason");
        int employeeId = getLoggedInEmployeeId(request);

        if (reason == null || reason.isBlank()) {
            request.getSession().setAttribute("msg", "fail_recount_noreason");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + number);
            return;
        }

        StockTake st = stDAO.getSTByNumber(number);
        if (st == null) {
            request.getSession().setAttribute("msg", "fail_notfound");
            response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=list");
            return;
        }

        boolean ok = stDAO.requestRecountST(st.getId(), employeeId, reason);
        request.getSession().setAttribute("msg", ok ? "success_recount" : "fail_recount");
        response.sendRedirect(request.getContextPath() + "/admin/stocktake?action=view&number=" + number);
    }

    private int getLoggedInEmployeeId(HttpServletRequest request) {
        Object emp = request.getSession().getAttribute("employeeId");
        if (emp instanceof Integer) {
            return (Integer) emp;
        }
        return 0;
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

    private boolean requireManager(HttpServletRequest request, HttpServletResponse response,
            String redirectUrl) throws IOException {
        String role = (String) request.getSession(false).getAttribute("roleName");
        if (!"Store Manager".equals(role) && !"Admin".equals(role) && !"Manager".equals(role)) {
            request.getSession().setAttribute("msg", "access_denied");
            response.sendRedirect(redirectUrl);
            return false;
        }
        return true;
    }
}
