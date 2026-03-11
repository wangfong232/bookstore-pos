package controller;

import DAO.EmployeeDAO;
import entity.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private EmployeeDAO employeeDAO;

    @Override
    public void init() {
        employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/AdminLTE-3.2.0/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validation
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required");
            doGet(request, response);
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Password is required");
            doGet(request, response);
            return;
        }

        Employee employee = employeeDAO.login(email, password);
        if (employee != null) {
            HttpSession session = request.getSession();
            session.setAttribute("employee", employee);
            session.setAttribute("employeeId", employee.getEmployeeId());
            session.setAttribute("fullName", employee.getFullName());
            session.setAttribute("roleId", employee.getRole().getRoleId());
            session.setAttribute("roleName", employee.getRole().getRoleName());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Check if account exists but not active
            Employee existingEmployee = employeeDAO.getEmployeeByEmail(email);
            if (existingEmployee != null) {
                // Email exists, check password
                String hashedPassword = employeeDAO.hashPassword(password);
                // For now, we'll check if password matches by attempting login
                // If login returns null but employee exists, it means either wrong password or not active
                if (existingEmployee.getStatus() != null && existingEmployee.getStatus().equals("PENDING")) {
                    request.setAttribute("error", "Tài khoản của bạn chưa được kích hoạt. Vui lòng đợi manager phân quyền.");
                } else if (existingEmployee.getStatus() != null && existingEmployee.getStatus().equals("INACTIVE")) {
                    request.setAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ quản lý.");
                } else {
                    request.setAttribute("error", "Email hoặc mật khẩu không chính xác");
                }
            } else {
                request.setAttribute("error", "Email hoặc mật khẩu không chính xác");
            }
            doGet(request, response);
        }
    }
}
