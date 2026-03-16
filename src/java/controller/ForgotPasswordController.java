package controller;

import DAO.EmployeeDAO;
import entity.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ForgotPasswordController – xử lý 2 bước đặt lại mật khẩu.
 *
 * Bước 1 (GET / POST step=1): Kiểm tra email có tồn tại & ACTIVE không.
 * Bước 2 (POST step=2):       Đặt lại mật khẩu mới.
 */
@WebServlet("/forgot-password")
public class ForgotPasswordController extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() {
        employeeDAO = new EmployeeDAO();
    }

    private static final String JSP = "/AdminLTE-3.2.0/forgot-password.jsp";

    // ---------------------------------------------------------------
    // GET → hiển thị bước 1 (nhập email)
    // ---------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("step", "1");
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    // ---------------------------------------------------------------
    // POST → xử lý cả bước 1 và bước 2
    // ---------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String step = request.getParameter("step");

        if ("2".equals(step)) {
            handleResetPassword(request, response);
        } else {
            handleVerifyEmail(request, response);
        }
    }

    // ---------------------------------------------------------------
    // Bước 1: kiểm tra email
    // ---------------------------------------------------------------
    private void handleVerifyEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("step", "1");
            request.setAttribute("error", "Vui lòng nhập địa chỉ email.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        email = email.trim().toLowerCase();
        Employee emp = employeeDAO.getEmployeeByEmail(email);

        if (emp == null) {
            request.setAttribute("step", "1");
            request.setAttribute("emailValue", email);
            request.setAttribute("error", "Email không tồn tại trong hệ thống.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        if ("INACTIVE".equals(emp.getStatus())) {
            request.setAttribute("step", "1");
            request.setAttribute("emailValue", email);
            request.setAttribute("error", "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ quản lý.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        if ("PENDING".equals(emp.getStatus())) {
            request.setAttribute("step", "1");
            request.setAttribute("emailValue", email);
            request.setAttribute("error", "Tài khoản chưa được kích hoạt. Vui lòng liên hệ quản lý.");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }

        // Email hợp lệ → chuyển sang bước 2
        request.setAttribute("step", "2");
        request.setAttribute("emailValue", email);
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    // ---------------------------------------------------------------
    // Bước 2: đặt lại mật khẩu
    // ---------------------------------------------------------------
    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email       = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");
        String confirmPwd  = request.getParameter("confirmPassword");

        // Validation
        if (email == null || email.trim().isEmpty()) {
            forwardStep2Error(request, response, email, "Phiên làm việc không hợp lệ. Vui lòng thử lại.");
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            forwardStep2Error(request, response, email, "Vui lòng nhập mật khẩu mới.");
            return;
        }

        if (newPassword.length() < 6) {
            forwardStep2Error(request, response, email, "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPwd)) {
            forwardStep2Error(request, response, email, "Mật khẩu xác nhận không khớp.");
            return;
        }

        // Tìm employee theo email
        Employee emp = employeeDAO.getEmployeeByEmail(email.trim());
        if (emp == null) {
            forwardStep2Error(request, response, email, "Không tìm thấy tài khoản. Vui lòng thử lại.");
            return;
        }

        // Cập nhật mật khẩu
        boolean updated = employeeDAO.updatePassword(emp.getEmployeeId(), newPassword);
        if (updated) {
            // Thành công → redirect về trang login với thông báo
            response.sendRedirect(request.getContextPath()
                    + "/login?success=password_reset");
        } else {
            forwardStep2Error(request, response, email,
                    "Đã xảy ra lỗi khi cập nhật mật khẩu. Vui lòng thử lại.");
        }
    }

    private void forwardStep2Error(HttpServletRequest request, HttpServletResponse response,
            String email, String message)
            throws ServletException, IOException {
        request.setAttribute("step", "2");
        request.setAttribute("emailValue", email);
        request.setAttribute("error", message);
        request.getRequestDispatcher(JSP).forward(request, response);
    }
}
