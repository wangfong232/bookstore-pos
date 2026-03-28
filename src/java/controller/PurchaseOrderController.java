/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAO.ProductDAO;
import DAO.PurchaseOrderDAO;
import DAO.PurchaseOrderItemDAO;
import DAO.SupplierDAO;
import entity.Product;
import entity.PurchaseOrder;
import entity.PurchaseOrderItem;
import entity.Supplier;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import util.Validation;

/**
 *
 * @author qp
 */
@WebServlet(name = "PurchaseOrderController", urlPatterns = {"/admin/purchaseorder"})
public class PurchaseOrderController extends HttpServlet {

    private PurchaseOrderDAO poDAO = new PurchaseOrderDAO();
    private PurchaseOrderItemDAO itemDAO = new PurchaseOrderItemDAO();
    private SupplierDAO supDAO = new SupplierDAO();
    private ProductDAO productDAO = new ProductDAO();

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
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "detail":
                showDetail(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("key");
        String status = request.getParameter("status");
        String from = request.getParameter("from");
        String to = request.getParameter("to");

        LocalDate fromDate = null;
        LocalDate toDate = null;
        if (from != null && !from.trim().isEmpty()) {
            fromDate = LocalDate.parse(from);
        }
        if (to != null && !to.trim().isEmpty()) {
            toDate = LocalDate.parse(to);
        }

        int page = 1;
        int pageSize = 10;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int totalRecords = poDAO.countPOs(keyword, status, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        List<PurchaseOrder> lists = poDAO.searchPOWithPaginated(keyword, status, fromDate, toDate, page, pageSize);
        request.setAttribute("lists", lists);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);

        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            request.getSession().removeAttribute("msg");
        }
        request.getRequestDispatcher("/AdminLTE-3.2.0/po-list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> productList = productDAO.getAllActiveProducts();
        request.setAttribute("productList", productList);

        List<Supplier> supList = supDAO.getAllActiveSuppliers();
        request.setAttribute("supList", supList);

        String poNumber = poDAO.generateNextPONumber();
        request.setAttribute("poNumber", poNumber);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        request.setAttribute("orderDate", today);

        request.setAttribute("mode", "add");

        String err = (String) request.getSession().getAttribute("error");

        if (err != null) {
            request.setAttribute("error", err);
            request.getSession().removeAttribute("error");
        }
        request.getRequestDispatcher("/AdminLTE-3.2.0/po-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String poNumber = request.getParameter("poNumber");

        if (poNumber == null || poNumber.trim().isEmpty()) {
            request.getSession().setAttribute("error", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
            return;
        }

        PurchaseOrder po = poDAO.getPurchaseOrderByCode(poNumber);

        if (po == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đơn hàng với mã: " + poNumber);
            response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
            return;
        }

        //xóa err
        String err = (String) request.getSession().getAttribute("error");
        if (err != null) {
            request.getSession().removeAttribute("error");
            request.setAttribute("error", err);
        }

        if (po != null) {
            List<Product> productList = productDAO.getAllActiveProducts();
            request.setAttribute("productList", productList);

            List<Supplier> supList = supDAO.getAllActiveSuppliers();
            request.setAttribute("supList", supList);

            request.setAttribute("po", po);
            request.setAttribute("poNumber", po.getPoNumber());
            request.setAttribute("orderDate", po.getOrderDate());
            request.setAttribute("expectedDate", po.getExpectedDate());
            request.setAttribute("notes", po.getNotes());

            List<PurchaseOrderItem> items = itemDAO.getItemsByPOId(po.getId());

            request.setAttribute("orderItems", items);

            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/AdminLTE-3.2.0/po-form.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String poNumber = request.getParameter("poNumber");

        if (poNumber == null || poNumber.trim().isEmpty()) {
            request.getSession().setAttribute("error", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
            return;
        }

        PurchaseOrder po = poDAO.getPurchaseOrderByCode(poNumber);

        if (po == null) {
            request.getSession().setAttribute("error", "Không tìm thấy đơn hàng với mã: " + poNumber);
            response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
            return;
        }

        //xóa err
        String err = (String) request.getSession().getAttribute("error");
        if (err != null) {
            request.getSession().removeAttribute("error");
            request.setAttribute("error", err);
        }

        List<PurchaseOrderItem> items = itemDAO.getItemsByPOId(po.getId());

        request.setAttribute("po", po);
        request.setAttribute("items", items);
        request.getRequestDispatcher("/AdminLTE-3.2.0/po-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (denyIfSaler(request, response)) {
            return;
        }
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }

        if ("approve".equals(action) || "reject".equals(action) || "cancel".equals(action)) {
            String roleName = (String) request.getSession().getAttribute("roleName");
            if (!("Manager".equals(roleName) || "Store Manager".equals(roleName)) && !"Admin".equals(roleName)) {
                request.getSession().setAttribute("error", "Bạn không có quyền thực hiện hành động này.");
                response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                return;
            }
        }

        String poNumber = request.getParameter("poNumber");
        Integer by = (Integer) request.getSession().getAttribute("employeeId");
        String reason = request.getParameter("reason");

        String msg = "";
        boolean success;
        switch (action) {
            case "delete":
                success = poDAO.deletePurchaseOrder(poNumber);
                msg = success ? "success_delete" : "fail";
                break;
            case "approve":
                if (poNumber == null || poNumber.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Mã đơn hàng không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (by == null) {
                    request.getSession().setAttribute("error", "Thông tin người duyệt không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                PurchaseOrder poToApprove = poDAO.getPurchaseOrderByCode(poNumber);
                if (poToApprove == null) {
                    request.getSession().setAttribute("error", "Không tìm thấy đơn hàng.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (!poToApprove.canBeApproved()) {
                    request.getSession().setAttribute("error", "Đơn hàng không thể duyệt. Chỉ có thể duyệt đơn đang chờ duyệt.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                success = poDAO.approvePO(poNumber, by);
                msg = success ? "success_approve" : "fail";
                break;
            case "reject":
                if (poNumber == null || poNumber.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Mã đơn hàng không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (by == null) {
                    request.getSession().setAttribute("error", "Thông tin người từ chối không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }
                if (reason == null || reason.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Vui lòng nhập lý do từ chối.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                PurchaseOrder poToReject = poDAO.getPurchaseOrderByCode(poNumber);
                if (poToReject == null) {
                    request.getSession().setAttribute("error", "Không tìm thấy đơn hàng.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (!poToReject.canBeApproved()) { // Reject cũng chỉ có thể khi status = PENDING_APPROVAL
                    request.getSession().setAttribute("error", "Đơn hàng không thể từ chối. Chỉ có thể từ chối đơn đang chờ duyệt.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                success = poDAO.rejectPO(poNumber, by, reason);
                msg = success ? "success_reject" : "fail";
                break;
            case "cancel":
                if (poNumber == null || poNumber.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Mã đơn hàng không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (by == null) {
                    request.getSession().setAttribute("error", "Thông tin người hủy không hợp lệ.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }
                if (reason == null || reason.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Vui lòng nhập lý do hủy đơn.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                PurchaseOrder poToCancel = poDAO.getPurchaseOrderByCode(poNumber);
                if (poToCancel == null) {
                    request.getSession().setAttribute("error", "Không tìm thấy đơn hàng.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");
                    return;
                }
                if (!poToCancel.canBeCancelled()) {
                    request.getSession().setAttribute("error", "Đơn hàng không thể hủy. Chỉ có thể hủy đơn đã được duyệt.");
                    response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=detail&poNumber=" + poNumber);
                    return;
                }

                success = poDAO.cancelPO(poNumber, by, reason);
                msg = success ? "success_cancel" : "fail";
                break;
            case "save":
                String supplierIdParam = request.getParameter("supplierId");
                String orderDateParam = request.getParameter("orderDate");
                String expectedDateParam = request.getParameter("expectedDate");

                String[] productIds = request.getParameterValues("productId");
                String[] quantities = request.getParameterValues("quantity");
                String[] unitPrices = request.getParameterValues("unitPrice");
                String[] discountTypes = request.getParameterValues("discountType");
                String[] discountValues = request.getParameterValues("discountValue");
                String[] itemNotes = request.getParameterValues("itemNote");

                String notes = request.getParameter("notes");
                String createdByParam = request.getParameter("createdBy");

                LocalDate orderDate = parseLocalDate(orderDateParam);
                LocalDate expectedDate = parseLocalDate(expectedDateParam);

                //validate
                Validation valid = new Validation();
                valid.required("Mã đơn đặt hàng", poNumber)
                        .required("Nhà cung cấp", supplierIdParam)
                        .required("Ngày đặt hàng", orderDateParam)
                        .validDates(orderDate, expectedDate);

                if (productIds == null || productIds.length == 0) {
                    valid.addError("Vui lòng thêm ít nhất 1 sản phẩm");
                }

                // Validate discount values
                if (discountValues != null && discountTypes != null) {
                    for (int i = 0; i < discountValues.length; i++) {
                        if (discountValues[i] != null && !discountValues[i].trim().isEmpty()) {
                            try {
                                BigDecimal discountVal = new BigDecimal(discountValues[i]);

                                if (discountVal.compareTo(BigDecimal.ZERO) < 0) {
                                    valid.addError("Giảm giá không được nhỏ hơn 0");
                                    break;
                                }

                                String discountType = (i < discountTypes.length) ? discountTypes[i] : "AMOUNT";
                                if ("PERCENT".equals(discountType) && discountVal.compareTo(new BigDecimal("100")) > 0) {
                                    valid.addError("Giảm giá theo phần trăm không được vượt quá 100%");
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                valid.addError("Giá trị giảm giá không hợp lệ");
                                break;
                            }
                        }
                    }
                }

                if (!valid.isValid()) {
                    List<Product> productList = productDAO.getAllActiveProducts();
                    request.setAttribute("productList", productList);

                    List<Supplier> supList = supDAO.getAllActiveSuppliers();
                    request.setAttribute("supList", supList);

                    PurchaseOrder poE = new PurchaseOrder();
                    poE.setPoNumber(poNumber);
                    if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
                        poE.setSupplierId(Integer.valueOf(supplierIdParam));
                    }

                    poE.setOrderDate(orderDate);
                    poE.setExpectedDate(expectedDate);

                    poE.setNotes(notes);

                    request.setAttribute("orderDate", orderDateParam);
                    request.setAttribute("expectedDate", expectedDateParam);

                    List<PurchaseOrderItem> items = new ArrayList<>();
                    if (productIds != null) {
                        for (int i = 0; i < productIds.length; i++) {
                            PurchaseOrderItem item = new PurchaseOrderItem();
                            try {
                                int pId = Integer.parseInt(productIds[i]);
                                item.setProductId(pId);

                                for (Product p : productList) {
                                    if (p.getId() == pId) {
                                        item.setProductName(p.getProductName());
                                        break;
                                    }
                                }

                                String qtyStr = (quantities != null && i < quantities.length) ? quantities[i] : "0";
                                item.setQuantityOrdered(parseIntSafe(qtyStr));

                                String priceStr = (unitPrices != null && i < unitPrices.length) ? unitPrices[i] : "0";
                                item.setUnitPrice(parseBigDecimalSafe(priceStr));

                                String discountStr = (discountValues != null && i < discountValues.length) ? discountValues[i] : "0";
                                item.setDiscountValue(parseBigDecimalSafe(discountStr));

                                String noteStr = (itemNotes != null && i < itemNotes.length) ? itemNotes[i] : "";
                                item.setNotes(noteStr);

                                item.calculateLineTotal();

                                items.add(item);

                            } catch (Exception e) {
                                System.out.println("Error parsing item at index " + i + ": " + e.getMessage());
                                continue;
                            }
                        }
                    }
                    request.setAttribute("po", poE);
                    request.setAttribute("orderItems", items);
                    request.setAttribute("notes", notes);
                    request.setAttribute("poNumber", poNumber);
                    request.setAttribute("error", valid.getFirstError());
                    String mode = poDAO.isCodeExist(poNumber) ? "edit" : "add";
                    request.setAttribute("mode", mode);

                    request.getRequestDispatcher("/AdminLTE-3.2.0/po-form.jsp").forward(request, response);
                    return;
                }

                //build item list
                PurchaseOrder po = new PurchaseOrder();
                po.setPoNumber(poNumber);
                po.setSupplierId(Integer.valueOf(supplierIdParam));
                po.setOrderDate(orderDate);
                po.setExpectedDate(expectedDate);
                po.setNotes(notes);
                Integer createdBy = (Integer) request.getSession().getAttribute("employeeId");
                po.setCreatedBy(createdBy);

                //add items to PO
                for (int i = 0; i < productIds.length; i++) {
                    PurchaseOrderItem item = new PurchaseOrderItem();
                    item.setProductId(Integer.valueOf(productIds[i]));
                    item.setQuantityOrdered(Integer.valueOf(quantities[i]));
                    item.setUnitPrice(new BigDecimal(unitPrices[i]));
                    item.setDiscountType(discountTypes != null && i < discountTypes.length
                            ? discountTypes[i] : "AMOUNT");
                    item.setDiscountValue(discountValues != null && i < discountValues.length
                            ? new BigDecimal(discountValues[i]) : BigDecimal.ZERO);
                    item.setNotes(itemNotes != null && i < itemNotes.length ? itemNotes[i] : null);

                    item.calculateLineTotal();
                    po.addItem(item);
                }
                po.recalculateTotals();

                boolean result;
                if (poDAO.isCodeExist(poNumber)) {
                    //update
                    PurchaseOrder existingPO = poDAO.getPurchaseOrderByCode(poNumber);

                    if (!PurchaseOrder.STATUS_PENDING_APPROVAL.equals(existingPO.getStatus()) && !PurchaseOrder.STATUS_REJECTED.equals(existingPO.getStatus())) {
                        request.getSession().setAttribute("error", "Không thể chỉnh sửa đơn hàng đã được duyệt hoặc đang nhập hàng!");
                        response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=edit&poNumber=" + poNumber);
                        return;
                    }

                    po.setId(existingPO.getId());

                    if (PurchaseOrder.STATUS_REJECTED.equals(existingPO.getStatus())) {
                        po.setStatus(PurchaseOrder.STATUS_PENDING_APPROVAL);
                    } else {
                        po.setStatus(existingPO.getStatus());
                    }

                    result = poDAO.updateOrderWithItems(po);
                    if (!result) {
                        List<Product> productList = productDAO.getAllActiveProducts();
                        request.setAttribute("productList", productList);

                        List<Supplier> supList = supDAO.getAllActiveSuppliers();
                        request.setAttribute("supList", supList);

                        request.setAttribute("po", po);
                        request.setAttribute("orderItems", po.getItems());
                        request.setAttribute("poNumber", poNumber);
                        request.setAttribute("orderDate", orderDateParam);
                        request.setAttribute("expectedDate", expectedDateParam);
                        request.setAttribute("error", "Lỗi khi cập nhật đơn hàng. Vui lòng kiểm tra lại thông tin.");
                        request.setAttribute("mode", "edit");
                        request.getRequestDispatcher("/AdminLTE-3.2.0/po-form.jsp").forward(request, response);
                        return;
                    }
                    msg = "success_edit";
                } else {
                    //add
                    result = poDAO.createOrderWithItems(po);
                    if (!result) {
                        List<Product> productList = productDAO.getAllActiveProducts();
                        request.setAttribute("productList", productList);

                        List<Supplier> supList = supDAO.getAllActiveSuppliers();
                        request.setAttribute("supList", supList);

                        request.setAttribute("po", po);
                        request.setAttribute("orderItems", po.getItems());
                        request.setAttribute("poNumber", poNumber);
                        request.setAttribute("orderDate", orderDateParam);
                        request.setAttribute("expectedDate", expectedDateParam);
                        request.setAttribute("error", "Lỗi khi tạo đơn hàng. Mã đơn có thể đã tồn tại hoặc dữ liệu không hợp lệ.");
                        request.setAttribute("mode", "add");
                        request.getRequestDispatcher("/AdminLTE-3.2.0/po-form.jsp").forward(request, response);
                        return;
                    }
                    msg = "success_add";
                }
                if (!result) {
                    request.getSession().setAttribute("error", "Lỗi hệ thống khi lưu đơn hàng.");
                }
                break;
        }

        request.getSession().setAttribute("msg", msg);
        response.sendRedirect(request.getContextPath() + "/admin/purchaseorder?action=list");

    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private BigDecimal parseBigDecimalSafe(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private LocalDate parseLocalDate(String value) {
        try {
            if (value != null && !value.isEmpty()) {
                return LocalDate.parse(value);
            }
        } catch (Exception e) {
        }
        return null;
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
