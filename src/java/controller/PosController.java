/*
 * Main POS controller
 */
package controller;

import DAO.CategoryDAO;
import DAO.ProductDAO;
import DAO.CustomerDAO;
import DAO.SalesInvoiceDAO;
import DAO.EmployeeDAO;
import entity.Customer;
import entity.CartItem;
import entity.Category;
import entity.Product;
import entity.Employee;
import util.VnPayConfig;
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
                // TODO: implement saving order to database
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

        // Tính thuế VAT theo loại sách
        double vatAmount = calculateVatAmount(cart, categories);

        request.setAttribute("cart", cart);
        request.setAttribute("totalAmount", totalAmount);
        request.setAttribute("vatAmount", vatAmount);
        request.setAttribute("categories", categories);

        // Sản phẩm luôn lấy từ DB: lọc theo key (tên/SKU) và categoryId (Categories.CategoryID)
        String key = request.getParameter("key");
        String categoryIdStr = request.getParameter("categoryId");
        Integer categoryId = null;
        if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdStr.trim());
            } catch (NumberFormatException e) {
                // ignore invalid categoryId
            }
        }
        List<Product> products = productDAO.getProducts(key, categoryId, 24);
        request.setAttribute("products", products);
        request.setAttribute("selectedCategoryId", categoryId);
        request.setAttribute("searchKey", key != null ? key : "");

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

    /**
     * Tính tổng tiền thuế VAT cho giỏ hàng dựa trên loại sách.
     * Các nhóm sách sau được áp dụng 0% VAT:
     * - Sách giáo khoa
     * - Sách chính trị
     * - Sách pháp luật
     * - Sách khoa học kỹ thuật
     * Các loại sách thông thường khác áp dụng 5% VAT.
     */
    private double calculateVatAmount(List<CartItem> cart, List<Category> categories) {
        if (cart == null || cart.isEmpty()) {
            return 0;
        }
        Map<Integer, Category> categoryMap = new HashMap<>();
        if (categories != null) {
            for (Category c : categories) {
                categoryMap.put(c.getCategoryID(), c);
            }
        }
        double vat = 0;
        for (CartItem item : cart) {
            Product p = item.getProduct();
            if (p == null) {
                continue;
            }
            int catId = p.getCategoryID();
            Category cat = categoryMap.get(catId);
            String name = cat != null ? cat.getCategoryName() : null;
            double rate = getVatRateFromCategoryName(name);
            vat += item.getLineTotal() * rate;
        }
        return vat;
    }

    private double getVatRateFromCategoryName(String categoryName) {
        if (categoryName == null) {
            return 0.05; // mặc định 5% cho sách thông thường
        }
        String name = categoryName.trim().toLowerCase();

        // 0% VAT cho:
        // - "Sách giáo khoa"
        // - "Sách khoa học" (coi như sách khoa học kỹ thuật)
        if (name.equals("sách giáo khoa")
                || name.equals("sach giao khoa")
                || name.equals("sách khoa học")
                || name.equals("sach khoa hoc")) {
            return 0.0;
        }

        // Các loại còn lại trong hệ thống hiện tại:
        // Văn học Việt Nam, Văn học nước ngoài, Sách thiếu nhi,
        // Sách kỹ năng, Sách kinh tế, Văn phòng phẩm -> 5% VAT
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
            if (quantity <= 0) {
                quantity = 1;
            }
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
            if (qty <= 0) {
                qty = 1;
            }

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
        String note = request.getParameter("note");
        String discountPercentStr = request.getParameter("discountPercent");
        String paymentMethod = request.getParameter("paymentMethod");
        String cashReceivedStr = request.getParameter("cashReceived");

        double discountPercent = 0;
        if (discountPercentStr != null && !discountPercentStr.trim().isEmpty()) {
            try {
                discountPercent = Double.parseDouble(discountPercentStr.trim());
            } catch (NumberFormatException e) {
                discountPercent = 0;
            }
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            paymentMethod = "CASH";
        }

        // Tính tổng tiền phải trả để validate tiền khách đưa (CASH)
        double totalAmount = cart.stream()
                .mapToDouble(CartItem::getLineTotal)
                .sum();
        if (discountPercent < 0) {
            discountPercent = 0;
        }
        if (discountPercent > 100) {
            discountPercent = 100;
        }
        double discountAmount = totalAmount * discountPercent / 100.0;
        double vatAmount = calculateVatAmount(cart, categoryDAO.getAllActiveCategories());
        double finalAmount = totalAmount - discountAmount + vatAmount;
        if (finalAmount < 0) {
            finalAmount = 0;
        }

        if ("CASH".equalsIgnoreCase(paymentMethod)) {
            double cashReceived = 0;
            if (cashReceivedStr != null && !cashReceivedStr.trim().isEmpty()) {
                try {
                    cashReceived = Double.parseDouble(cashReceivedStr.trim());
                } catch (NumberFormatException ignored) {
                    cashReceived = 0;
                }
            }
            if (cashReceived + 0.000001 < finalAmount) {
                session.setAttribute("error",
                        "Tiền khách đưa phải lớn hơn hoặc bằng số tiền cần thanh toán ("
                        + String.format("%,.0f", finalAmount) + " đ).");
                response.sendRedirect("pos");
                return;
            }
        }

        // Resolve customer: nhập mã KH (KH001...) hoặc số điện thoại; nếu SĐT mới chưa có thì tự tạo khách hàng
        String resolvedCustomerId = null;
        if (customerInput != null && !customerInput.trim().isEmpty()) {
            String trimmed = customerInput.trim();
            Customer customer = customerDAO.getById(trimmed);
            if (customer == null) {
                customer = customerDAO.getByPhone(trimmed);
            }
            if (customer == null) {
                // Mã KH/SĐT mới chưa có trong hệ thống -> tự động tạo khách hàng (coi input là SĐT)
                Customer newCustomer = new Customer();
                newCustomer.setCustomerID(customerDAO.getNextCustomerId());
                newCustomer.setFullName("");
                newCustomer.setEmail(null);
                newCustomer.setPhoneNumber(trimmed);
                newCustomer.setBirthday(java.time.LocalDate.of(1990, 1, 1));
                newCustomer.setStatus("ACTIVE");
                newCustomer.setNote(null);
                customerDAO.insert(newCustomer);
                resolvedCustomerId = newCustomer.getCustomerID();
            } else {
                resolvedCustomerId = customer.getCustomerID();
            }
        }

        // Resolve staff: nếu DEFAULT_STAFF_ID không tồn tại, chọn 1 nhân viên bất kỳ
        int staffId = DEFAULT_STAFF_ID;
        Employee staff = employeeDAO.getEmployeeByID(DEFAULT_STAFF_ID);
        if (staff == null) {
            List<Employee> employees = employeeDAO.getEmployees(null, null, null, 1, 1);
            if (employees.isEmpty()) {
                session.setAttribute("error", "Không có nhân viên nào trong hệ thống. Vui lòng tạo nhân viên trước khi thanh toán.");
                response.sendRedirect("pos");
                return;
            }
            staffId = employees.get(0).getEmployeeId();
        }

        // Nếu chọn thanh toán chuyển khoản thì chuyển sang VNPAY sandbox
        if ("TRANSFER".equalsIgnoreCase(paymentMethod)) {
            // Lưu thông tin đơn hàng tạm thời để xử lý sau khi VNPAY callback
            session.setAttribute("pendingCart", new ArrayList<>(cart));
            session.setAttribute("pendingNote", note);
            session.setAttribute("pendingDiscountPercent", discountPercent);
            session.setAttribute("pendingCustomerId", resolvedCustomerId);
            session.setAttribute("pendingStaffId", staffId);
            session.setAttribute("pendingShiftId", DEFAULT_SHIFT_ID);

            // Mã tham chiếu cho VNPAY (không trùng lặp trong ngày)
            String txnRef = "POS-" + System.currentTimeMillis();
            String orderInfo = "Thanh toan don hang POS " + txnRef;

            String paymentUrl = VnPayConfig.createPaymentUrl(
                    request,
                    finalAmount,
                    orderInfo,
                    txnRef
            );

            response.sendRedirect(paymentUrl);
            return;
        }

        String invoiceCode = salesInvoiceDAO.createInvoice(
                resolvedCustomerId,
                cart,
                staffId,
                DEFAULT_SHIFT_ID,
                note,
                discountPercent,
                paymentMethod
        );

        if (invoiceCode == null) {
            String technical = SalesInvoiceDAO.getLastErrorMessage();
            if (technical != null && !technical.isEmpty()) {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu hóa đơn: " + technical);
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu hóa đơn. Vui lòng thử lại.");
            }
            response.sendRedirect("pos");
            return;
        }

        session.removeAttribute("cart");
        session.setAttribute("msg", "Thanh toán thành công. Mã hóa đơn: " + invoiceCode);
        response.sendRedirect("pos");
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

