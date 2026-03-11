package controller;

import DAO.EmployeeDAO;
import DAO.RoleDAO;
import entity.Employee;
import entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/register")
public class RegisterController extends HttpServlet {

    private EmployeeDAO employeeDAO;
    private RoleDAO roleDAO;

    @Override
    public void init() {
        employeeDAO = new EmployeeDAO();
        roleDAO = new RoleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Only expose role selection when current user is manager
        Object roleNameObj = request.getSession().getAttribute("roleName");
        if (roleNameObj != null) {
            String roleName = roleNameObj.toString().toLowerCase();
            if (roleName.contains("manager")) {
                List<Role> roles = roleDAO.getAllRoles();
                request.setAttribute("roles", roles);
            }
        }
        request.getRequestDispatcher("/AdminLTE-3.2.0/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String roleIdStr = request.getParameter("roleId");
        String hireDateStr = request.getParameter("hireDate");

        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            request.setAttribute("error", "Full name is required");
            doGet(request, response);
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Email is required");
            doGet(request, response);
            return;
        }
        if (password == null || password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters");
            doGet(request, response);
            return;
        }
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match");
            doGet(request, response);
            return;
        }
        if (employeeDAO.isEmailExists(email)) {
            request.setAttribute("error", "Email already exists");
            doGet(request, response);
            return;
        }

        // Create Employee
        Employee e = new Employee();
        e.setFullName(fullName);
        e.setEmail(email);
        e.setPhone(phone);
        e.setPassword(password);

        // determine assigning role/hire date based on current user
        boolean isManager = false;
        Object roleNameObj = request.getSession().getAttribute("roleName");
        if (roleNameObj != null && roleNameObj.toString().toLowerCase().contains("manager")) {
            isManager = true;
        }

        if (isManager && roleIdStr != null && !roleIdStr.isEmpty()) {
            Role r = new Role();
            r.setRoleId(Integer.parseInt(roleIdStr));
            e.setRole(r);
        } else {
            // Default to regular employee when not manager or no choice provided
            Role r = new Role();
            r.setRoleId(2); // 2 assumed to correspond to basic employee role
            e.setRole(r);
        }

        if (isManager && hireDateStr != null && !hireDateStr.isEmpty()) {
            e.setHireDate(Date.valueOf(hireDateStr));
        }

        if (employeeDAO.registerEmployee(e)) {
            response.sendRedirect(request.getContextPath() + "/login?success=register_pending");
        } else {
            request.setAttribute("error", "Registration failed");
            doGet(request, response);
        }
    }
}
