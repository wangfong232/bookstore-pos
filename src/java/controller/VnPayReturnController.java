package controller;

import DAO.SalesInvoiceDAO;
import entity.CartItem;
import DAO.CustomerPointDAO;
import util.PointConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "VnPayReturnController", urlPatterns = { "/vnpay-return" })
public class VnPayReturnController extends HttpServlet {

    private final SalesInvoiceDAO salesInvoiceDAO = new SalesInvoiceDAO();
    private final CustomerPointDAO customerPointDAO = new CustomerPointDAO();

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
        Double promotionDiscount = (Double) session.getAttribute("pendingPromotionDiscount");
        String promoName = (String) session.getAttribute("pendingPromoName");
        String note = (String) session.getAttribute("pendingNote");
        String resolvedCustomerId = (String) session.getAttribute("pendingCustomerId");
        Double vatAmount = (Double) session.getAttribute("pendingVatAmount");
        Integer staffId = (Integer) session.getAttribute("pendingStaffId");
        Integer shiftId = (Integer) session.getAttribute("pendingShiftId");

        // Dọn sạch thông tin tạm ngay sau khi lấy ra
        session.removeAttribute("pendingCart");
        session.removeAttribute("pendingPromotionDiscount");
        session.removeAttribute("pendingPromoName");
        session.removeAttribute("pendingNote");
        session.removeAttribute("pendingCustomerId");
        session.removeAttribute("pendingVatAmount");
        session.removeAttribute("pendingStaffId");
        session.removeAttribute("pendingShiftId");

        if (cart == null || cart.isEmpty() || staffId == null) {
            session.setAttribute("error", "Không tìm thấy thông tin đơn hàng đang chờ thanh toán.");
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }
        if (promotionDiscount == null) {
            promotionDiscount = 0.0;
        }

        String invoiceCode = salesInvoiceDAO.createInvoice(
                resolvedCustomerId,
                cart,
                staffId,
                shiftId,
                note,
                promotionDiscount,
                vatAmount != null ? vatAmount : 0.0,
                "VNPAY");

        if (invoiceCode == null) {
            String technical = SalesInvoiceDAO.getLastErrorMessage();
            if (technical != null && !technical.isEmpty()) {
                session.setAttribute("error", "Có lỗi xảy ra khi lưu hóa đơn sau khi thanh toán VNPAY: " + technical);
            } else {
                session.setAttribute("error",
                        "Có lỗi xảy ra khi lưu hóa đơn sau khi thanh toán VNPAY. Vui lòng kiểm tra lại.");
            }
            response.sendRedirect(request.getContextPath() + "/pos");
            return;
        }

        // Xóa giỏ hàng chính thức sau khi đã lưu hóa đơn thành công
        session.removeAttribute("cart");

        // Tổng giá gốc (trước promotion) – dùng để tích điểm
        double totalAmountOriginal = cart.stream().mapToDouble(item -> item.getLineTotal()).sum();

        // Tích điểm theo giá GỐC (trước promotion)
        if (resolvedCustomerId != null) {
            int pricePerPoint = PointConfig.getPricePerPoint(getServletContext());
            int pointsAdded = (int) (totalAmountOriginal / pricePerPoint);

            StringBuilder msg = new StringBuilder();
            msg.append("Thanh toán VNPAY thành công. Mã hóa đơn: ").append(invoiceCode);
            if (promotionDiscount > 0) {
                msg.append(" (-").append(String.format("%,.0f", promotionDiscount)).append("đ)");
            }
            if (pointsAdded > 0) {
                customerPointDAO.addPoints(resolvedCustomerId, pointsAdded);
                msg.append(". Bạn được cộng ").append(pointsAdded).append(" điểm.");
            }
            session.setAttribute("msg", msg.toString());
        } else {
            StringBuilder msg = new StringBuilder();
            msg.append("Thanh toán VNPAY thành công. Mã hóa đơn: ").append(invoiceCode);
            if (promotionDiscount > 0) {
                msg.append(" (-").append(String.format("%,.0f", promotionDiscount)).append("đ)");
            }
            session.setAttribute("msg", msg.toString());
        }

        response.sendRedirect(request.getContextPath() + "/pos");
    }
}
