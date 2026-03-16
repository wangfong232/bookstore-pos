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

/**
 * Controller để chỉnh sửa thông tin cá nhân của user
 */
@WebServlet("/profile")
public class ProfileController extends HttpServlet {

    private EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("employeeId");
        
        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.equals("view")) {
            viewProfile(request, response, employeeId);
        } else if (action.equals("edit")) {
            editProfile(request, response, employeeId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer employeeId = (Integer) session.getAttribute("employeeId");
        
        if (employeeId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("update".equals(action)) {
            updateProfile(request, response, employeeId);
        } else if ("change_password".equals(action)) {
            changePassword(request, response, employeeId);
        }
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response, int employeeId)
            throws ServletException, IOException {
        
        Employee employee = employeeDAO.getEmployeeByID(employeeId);
        if (employee == null) {
            request.setAttribute("error", "Không tìm thấy thông tin nhân viên");
        }
        request.setAttribute("employee", employee);
        request.getRequestDispatcher("/AdminLTE-3.2.0/profile.jsp").forward(request, response);
    }

    private void editProfile(HttpServletRequest request, HttpServletResponse response, int employeeId)
            throws ServletException, IOException {
        
        Employee employee = employeeDAO.getEmployeeByID(employeeId);
        if (employee == null) {
            request.setAttribute("error", "Không tìm thấy thông tin nhân viên");
        }
        request.setAttribute("employee", employee);
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/AdminLTE-3.2.0/profile.jsp").forward(request, response);
    }

    private void updateProfile(HttpServletRequest request, HttpServletResponse response, int employeeId)
            throws IOException {
        
        try {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            Employee employee = employeeDAO.getEmployeeByID(employeeId);
            if (employee == null) {
                response.sendRedirect(request.getContextPath() + "/profile?error=not_found");
                return;
            }
            
            employee.setFullName(fullName);
            employee.setEmail(email);
            employee.setPhone(phone);
            
            boolean success = employeeDAO.updateProfile(employee, employeeId);
            
            if (success) {
                // Update session
                HttpSession session = request.getSession();
                session.setAttribute("fullName", fullName);
                
                response.sendRedirect(request.getContextPath() + "/profile?success=update");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=update");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?error=update");
        }
    }

    private void changePassword(HttpServletRequest request, HttpServletResponse response, int employeeId)
            throws IOException {
        
        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validation
            if (oldPassword == null || oldPassword.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/profile?error=old_password_required");
                return;
            }
            
            if (newPassword == null || newPassword.length() < 6) {
                response.sendRedirect(request.getContextPath() + "/profile?error=password_length");
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                response.sendRedirect(request.getContextPath() + "/profile?error=password_mismatch");
                return;
            }
            
            Employee employee = employeeDAO.getEmployeeByID(employeeId);
            
            // Verify old password
            String hashedOldPassword = employeeDAO.hashPassword(oldPassword);
            boolean passwordMatches = employeeDAO.verifyPassword(employeeId, hashedOldPassword);
            
            if (!passwordMatches) {
                response.sendRedirect(request.getContextPath() + "/profile?error=old_password_incorrect");
                return;
            }
            
            // Update password
            boolean success = employeeDAO.updatePassword(employeeId, newPassword);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/profile?success=password");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile?error=password_update");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/profile?error=password_update");
        }
    }
}
