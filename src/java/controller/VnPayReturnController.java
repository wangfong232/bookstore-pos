package controller;

import DAO.SalesInvoiceDAO;
import entity.CartItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "VnPayReturnController", urlPatterns = {"/vnpay-return"})
public class VnPayReturnController extends HttpServlet {

    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleReturn(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleReturn(request, response);
    }

    private void handleReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();

        String responseCode = request.getParameter("vnp_ResponseCode");
        if (!"00".equals(responseCode)) {
            session.setAttribute("error", "Thanh toán VNPAY không thành công. Mã lỗi: " + responseCode);
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("pendingCart");
        Double discountPercent = (Double) session.getAttribute("pendingDiscountPercent");
        String note = (String) session.getAttribute("pendingNote");
        String resolvedCustomerId = (String) session.getAttribute("pendingCustomerId");
        Integer staffId = (Integer) session.getAttribute("pendingStaffId");
        Integer shiftId = (Integer) session.getAttribute("pendingShiftId");

        // Dọn sạch thông tin tạm ngay sau khi lấy ra
        session.removeAttribute("pendingCart");
        session.removeAttribute("pendingDiscountPercent");
        session.removeAttribute("pendingNote");
        session.removeAttribute("pendingCustomerId");
        session.removeAttribute("pendingStaffId");
        session.removeAttribute("pendingShiftId");

        if (cart == null || cart.isEmpty() || staffId == null) {
            session.setAttribute("error", "Không tìm thấy thông tin đơn hàng đang chờ thanh toán.");
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }
        if (discountPercent == null) {
            discountPercent = 0.0;
        }

        String invoiceCode = salesInvoiceDAO.createInvoice(
                resolvedCustomerId,
                cart,
                staffId,
                shiftId,
                note,
                discountPercent,
                "VNPAY"
        );

        if (invoiceCode == null) {
            String technical = SalesInvoiceDAO.getLastErrorMessage();
            if (technical != null && !technical.isEmpty()) {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu hóa đơn sau khi thanh toán VNPAY: " + technical);
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu hóa đơn sau khi thanh toán VNPAY. Vui lòng kiểm tra lại.");
            }
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }

        // Xóa giỏ hàng chính thức sau khi đã lưu hóa đơn thành công
        session.removeAttribute("cart");
        session.setAttribute("msg", "Thanh toán VNPAY thành công. Mã hóa đơn: " + invoiceCode);
        response.sendRedirect(request.getContextPath() + "/pos");
    }
}

