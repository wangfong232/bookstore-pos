/*
 * Main POS controller
 */
package controller;

import DAO.CategoryDAO;
import DAO.ProductDAO;
import DAO.CustomerDAO;
import DAO.SalesInvoiceDAO;
import DAO.EmployeeDAO;
import DAO.CustomerPointDAO;
import entity.Customer;
import entity.CartItem;
import entity.Category;
import entity.Product;
import entity.Employee;
import util.VnPayConfig;
import util.PointConfig;
import util.PromotionService;
import util.PromotionService.PromotionResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "PosController", urlPatterns = {"/pos"})
public class PosController extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final CustomerPointDAO customerPointDAO = new CustomerPointDAO();
    private final PromotionService promotionService = new PromotionService();
    
    // Tạm thời hard-code nhân viên & ca làm để dev POS nhanh
    private static final int DEFAULT_STAFF_ID = 5; // cashiers sample data
    private static final Integer DEFAULT_SHIFT_ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "view";
        }

        switch (action) {
            default:
                showPosPage(request, response);
                break;
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
            case "addItem":
                addItemToCart(request, response);
                break;
            case "updateQty":
                updateQuantity(request, response);
                break;
            case "removeItem":
                removeItem(request, response);
                break;
            case "clearCart":
                clearCart(request, response);
                break;
            case "checkout":
                checkout(request, response);
                break;
            default:
                response.sendRedirect("pos");
        }
    }

    private void showPosPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCart(session);

        double totalAmount = cart.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();

        // Danh mục từ DB (bảng Categories) - chỉ hiển thị filter nếu có dữ liệu
        List<Category> categories = categoryDAO.getAllActiveCategories();

        // Tính thuế VAT theo loại sách (Team's Update)
        double vatAmount = calculateVatAmount(cart, categories);

        // Tính khuyến mãi tự động (My Logic)
        PromotionResult promoResult = promotionService.calculatePromotionDiscount(cart);
        double autoPromoDiscount = promoResult.getTotalDiscount();

        request.setAttribute("cart", cart);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("autoPromoDiscount", autoPromoDiscount);
        request.setAttribute("vatAmount", vatAmount);
        request.setAttribute("categories", categories);

        // Sản phẩm lấy từ DB: lọc theo key (tên/SKU) và categoryId (Pagination Team's Update)
        String key = request.getParameter("key");
        String categoryIdStr = request.getParameter("categoryId");
        String pageStr = request.getParameter("page");
        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr.trim());
            } catch (NumberFormatException e) {}
        }
        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr.trim());
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }
        int pageSize = 12; 
        int totalProducts = productDAO.countProductsForPos(key, categoryId);
        int totalPages = (int) Math.ceil(totalProducts / (double) pageSize);
        if (totalPages <= 0) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        List<Product> products = productDAO.getProductsForPos(key, categoryId, page, pageSize);
        request.setAttribute("products", products);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchKey", key != null ? key : "");
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            request.setAttribute("msg", msg);
            session.removeAttribute("msg");
        }

        String error = (String) session.getAttribute("error");
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }

        request.getRequestDispatcher("/AdminLTE-3.2.0/pos.jsp").forward(request, response);
    }

    private double calculateVatAmount(List<CartItem> cart, List<Category> categories) {
        if (cart == null || cart.isEmpty()) return 0;
        Map<Integer, Category> categoryMap = new HashMap<>();
        if (categories != null) {
            for (Category c : categories) {
                categoryMap.put(c.getCategoryID(), c);
            }
        }
        double vat = 0;
        for (CartItem item : cart) {
            Product p = item.getProduct();
            if (p == null) continue;
            int catId = p.getCategoryID();
            Category cat = categoryMap.get(catId);
            String name = cat != null ? cat.getCategoryName() : null;
            double rate = getVatRateFromCategoryName(name);
            vat += item.getLineTotal() * rate;
        }
        return vat;
    }

    private double getVatRateFromCategoryName(String categoryName) {
        if (categoryName == null) return 0.05; 
        String name = categoryName.trim().toLowerCase();
        if (name.equals("sách giáo khoa") || name.equals("sach giao khoa")
                || name.equals("sách khoa học") || name.equals("sach khoa hoc")) {
            return 0.0;
        }
        return 0.05;
    }

    private void addItemToCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String sku = request.getParameter("sku");
        String quantityStr = request.getParameter("quantity");

        if (sku == null || sku.trim().isEmpty()) {
            session.setAttribute("error", "Vui lòng nhập mã SKU sản phẩm.");
            response.sendRedirect("pos");
            return;
        }

        int quantity = 1;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) quantity = 1;
        } catch (NumberFormatException e) {
            quantity = 1;
        }

        Product product = productDAO.getProductBySku(sku.trim());
        if (product == null) {
            session.setAttribute("error", "Không tìm thấy sản phẩm với SKU: " + sku);
            response.sendRedirect("pos");
            return;
        }

        List<CartItem> cart = getCart(session);
        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProduct().getProductID() == product.getProductID()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            CartItem newItem = new CartItem(product, quantity, product.getSellingPrice());
            cart.add(newItem);
        }

        session.setAttribute("cart", cart);
        response.sendRedirect("pos");
    }

    private void updateQuantity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String productIdStr = request.getParameter("productId");
        String quantityStr = request.getParameter("quantity");

        try {
            int productId = Integer.parseInt(productIdStr);
            int qty = Integer.parseInt(quantityStr);
            if (qty <= 0) qty = 1;

            List<CartItem> cart = getCart(session);
            for (CartItem item : cart) {
                if (item.getProduct().getProductID() == productId) {
                    item.setQuantity(qty);
                    break;
                }
            }
            session.setAttribute("cart", cart);
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Số lượng không hợp lệ.");
        }
        response.sendRedirect("pos");
    }

    private void removeItem(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String productIdStr = request.getParameter("productId");
        try {
            int productId = Integer.parseInt(productIdStr);
            List<CartItem> cart = getCart(session);
            cart.removeIf(item -> item.getProduct().getProductID() == productId);
            session.setAttribute("cart", cart);
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Sản phẩm không hợp lệ.");
        }
        response.sendRedirect("pos");
    }

    private void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        session.removeAttribute("cart");
        response.sendRedirect("pos");
    }

    private void checkout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            session.setAttribute("error", "Giỏ hàng đang trống, không thể thanh toán.");
            response.sendRedirect("pos");
            return;
        }
        
        String customerInput = request.getParameter("customerId");
        String customerNameInput = request.getParameter("customerName");
        String note = request.getParameter("note");
        String manualDiscountPercentStr = request.getParameter("discountPercent");
        String paymentMethod = request.getParameter("paymentMethod");
        String cashReceivedStr = request.getParameter("cashReceived");

        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "CASH";
        }

        // 1. Resolve customer: nhập mã KH (KH001...) hoặc SĐT (Team's Update)
        String resolvedCustomerId = null;
        if (customerInput != null && !customerInput.trim().isEmpty()) {
            String trimmed = customerInput.trim();
            Customer customer = customerDAO.getById(trimmed);
            if (customer == null) {
                customer = customerDAO.getByPhone(trimmed); // New support for phone
            }
            if (customer == null) {
                // Tự động tạo khách hàng mới
                if (customerNameInput == null || customerNameInput.trim().isEmpty()) {
                    session.setAttribute("error", "Khách hàng mới: vui lòng nhập Tên khách hàng.");
                    response.sendRedirect("pos");
                    return;
                }
                Customer newCustomer = new Customer();
                newCustomer.setCustomerID(trimmed); // Use phone as ID
                newCustomer.setFullName(customerNameInput.trim());
                newCustomer.setPhoneNumber(trimmed);
                newCustomer.setStatus("ACTIVE");
                customerDAO.insert(newCustomer);
                resolvedCustomerId = newCustomer.getCustomerID();
            } else {
                resolvedCustomerId = customer.getCustomerID();
            }
        }

        // 2. Resolve staff
        int staffId = DEFAULT_STAFF_ID;
        Employee staff = employeeDAO.getEmployeeByID(DEFAULT_STAFF_ID);
        if (staff == null) {
            List<Employee> employees = employeeDAO.getEmployees(null, null, null, 1, 1);
            if (!employees.isEmpty()) staffId = employees.get(0).getEmployeeId();
        }

        // 3. Tự động áp dụng promotion (My Logic)
        PromotionResult promoResult = promotionService.calculatePromotionDiscount(cart);
        double automaticDiscount = promoResult.getTotalDiscount();
        
        // Manual discount (Team's feature)
        double manualDiscountPercent = 0;
        try {
            if (manualDiscountPercentStr != null) manualDiscountPercent = Double.parseDouble(manualDiscountPercentStr);
        } catch (NumberFormatException e) {}
        
        double totalAmountOriginal = cart.stream().mapToDouble(CartItem::getLineTotal).sum();
        double manualDiscountAmount = totalAmountOriginal * (manualDiscountAmountPercentToAmount(manualDiscountPercent));

        double totalDiscount = automaticDiscount + manualDiscountAmount;
        if (totalDiscount > totalAmountOriginal) totalDiscount = totalAmountOriginal;

        double vatAmount = calculateVatAmount(cart, categoryDAO.getAllActiveCategories());
        double finalAmount = totalAmountOriginal - totalDiscount + vatAmount;
        if (finalAmount < 0) finalAmount = 0;

        // 4. Validate tiền khách đưa (Team's Feature)
        if ("CASH".equalsIgnoreCase(paymentMethod)) {
            double cashReceived = 0;
            try {
                if (cashReceivedStr != null) cashReceived = Double.parseDouble(cashReceivedStr);
            } catch (NumberFormatException e) {}
            if (cashReceived + 0.000001 < finalAmount) {
                session.setAttribute("error", "Tiền khách đưa phải lớn hơn hoặc bằng " + String.format("%,.0f", finalAmount) + "đ");
                response.sendRedirect("pos");
                return;
            }
        }

        // 5. Thanh toán Chuyển khoản (VNPAY)
        if ("TRANSFER".equalsIgnoreCase(paymentMethod)) {
            session.setAttribute("pendingCart", new ArrayList<>(cart));
            session.setAttribute("pendingNote", note);
            session.setAttribute("pendingPromotionDiscount", totalDiscount);
            session.setAttribute("pendingPromoName", promoResult.getAppliedPromotionName());
            session.setAttribute("pendingCustomerId", resolvedCustomerId);
            session.setAttribute("pendingVatAmount", vatAmount);
            session.setAttribute("pendingStaffId", staffId);
            session.setAttribute("pendingShiftId", DEFAULT_SHIFT_ID);

            String txnRef = "POS-" + System.currentTimeMillis();
            String paymentUrl = VnPayConfig.createPaymentUrl(request, finalAmount, "Thanh toan POS " + txnRef, txnRef);
            response.sendRedirect(paymentUrl);
            return;
        }

        // 6. Lưu hóa đơn (CASH)
        String invoiceCode = salesInvoiceDAO.createInvoice(
                resolvedCustomerId, cart, staffId, DEFAULT_SHIFT_ID, note, totalDiscount, vatAmount, paymentMethod);

        if (invoiceCode == null) {
            session.setAttribute("error", "Lỗi lưu hóa đơn: " + SalesInvoiceDAO.getLastErrorMessage());
            response.sendRedirect("pos");
            return;
        }

        session.removeAttribute("cart");

        // 7. Tích điểm & Thông báo simplified (My Logic)
        int pointsAdded = 0;
        if (resolvedCustomerId != null) {
            int pricePerPoint = PointConfig.getPricePerPoint(getServletContext());
            pointsAdded = (int) (totalAmountOriginal / pricePerPoint);
            customerPointDAO.addPoints(resolvedCustomerId, pointsAdded);
        }

        StringBuilder msg = new StringBuilder("Thanh toán thành công. Mã: " + invoiceCode);
        if (totalDiscount > 0) {
            msg.append(" (-").append(String.format("%,.0f", totalDiscount)).append("đ)");
        }
        if (pointsAdded > 0) {
            msg.append(". Được cộng ").append(pointsAdded).append(" điểm.");
        }
        session.setAttribute("msg", msg.toString());
        response.sendRedirect("pos");
    }

    private double manualDiscountAmountPercentToAmount(double percent) {
        if (percent < 0) return 0;
        if (percent > 100) return 1.0;
        return percent / 100.0;
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}
