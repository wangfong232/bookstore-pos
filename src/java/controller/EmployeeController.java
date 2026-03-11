package controller;

import DAO.EmployeeDAO;
import DAO.RoleDAO;
import entity.Employee;
import entity.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/admin/employees")
public class EmployeeController extends HttpServlet {

    private EmployeeDAO employeeDAO;
    private RoleDAO roleDAO;

    @Override
    public void init() {
        employeeDAO = new EmployeeDAO();
        roleDAO = new RoleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            listEmployees(request, response);
        } else if (action.equals("add")) {
            showAddForm(request, response);
        } else if (action.equals("edit")) {
            showEditForm(request, response);
        } else if (action.equals("toggle")) {
            toggleStatus(request, response);
        } else if (action.equals("delete")) {
            deleteEmployee(request, response);
        
        } else {
            listEmployees(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("insert".equals(action)) {
            insertEmployee(request, response);
        } else if ("update".equals(action)) {
            updateEmployee(request, response);
        
        }
    }

    // =====================================================
    // LIST + FILTER + PAGING
    // =====================================================
    private void listEmployees(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization: only Manager and Store Manager can access employee list
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            // Not authorized, redirect to dashboard with error
            response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
            return;
        }

        String search = request.getParameter("key");
        String roleParam = request.getParameter("roleId");
        String status = request.getParameter("status");

        Integer roleId = null;
        if (roleParam != null && !roleParam.isEmpty()) {
            roleId = Integer.parseInt(roleParam);
        }

        int page = 1;
        int pageSize = 5;

        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }

        int total = employeeDAO.getTotalEmployees(search, roleId, status);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        List<Employee> list
                = employeeDAO.getEmployees(search, roleId, status, page, pageSize);

        List<Role> roles = roleDAO.getAllRoles();
        request.setAttribute("lists", list);
        request.setAttribute("roleList", roles);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/admin-employee-list.jsp")
                .forward(request, response);
    }

    // =====================================================
    // SHOW ADD FORM
    // =====================================================
    private void showAddForm(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization: only Manager (roleId=1) and Store Manager (roleId=2) can add employees
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            // Not authorized, redirect to dashboard with error
            response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
            return;
        }

        List<Role> roles = roleDAO.getAllRoles();
        request.setAttribute("roles", roles);

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/admin-employee-detail.jsp")
                .forward(request, response);
    }

    // =====================================================
    // INSERT
    // =====================================================
    private void insertEmployee(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // Check authorization: only Manager (roleId=1) and Store Manager (roleId=2) can add
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?action=list&error=unauthorized");
            return;
        }

        try {
            Employee e = new Employee();

            e.setFullName(request.getParameter("fullName"));
            e.setEmail(request.getParameter("email"));
            e.setPhone(request.getParameter("phone"));

            String hireDate = request.getParameter("hireDate");
            if (hireDate != null && !hireDate.isEmpty()) {
                e.setHireDate(Date.valueOf(hireDate));
            }

            Role r = new Role();
            r.setRoleId(Integer.parseInt(request.getParameter("roleId")));
            e.setRole(r);

            // giả sử admin login lưu trong session
            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId == null) {
                adminId = 1;
            }

            boolean success
                    = employeeDAO.insertEmployee(e, adminId);

            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&success=insert"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&error=insert"
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/admin/employees?action=list&error=insert"
            );
        }
    }

    // =====================================================
    // SHOW EDIT FORM
    // =====================================================
    private void showEditForm(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Check authorization: only Manager (roleId=1) and Store Manager (roleId=2) can edit employees
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            // Not authorized, redirect to dashboard with error
            response.sendRedirect(request.getContextPath() + "/dashboard?error=unauthorized");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        Employee e = employeeDAO.getEmployeeByID(id);
        List<Role> roles = roleDAO.getAllRoles();

        request.setAttribute("employee", e);
        request.setAttribute("roles", roles);

        request.getRequestDispatcher(
                "/AdminLTE-3.2.0/admin-employee-detail.jsp")
                .forward(request, response);
    }

    // =====================================================
    // UPDATE
    // =====================================================
    private void updateEmployee(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // Check authorization: only Manager (roleId=1) and Store Manager (roleId=2) can update
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?action=list&error=unauthorized");
            return;
        }

        try {
            Employee e = new Employee();

            e.setEmployeeId(
                    Integer.parseInt(request.getParameter("employeeId")));

            e.setFullName(request.getParameter("fullName"));
            e.setEmail(request.getParameter("email"));
            e.setPhone(request.getParameter("phone"));

            String hireDate = request.getParameter("hireDate");
            if (hireDate != null && !hireDate.isEmpty()) {
                e.setHireDate(Date.valueOf(hireDate));
            }

            String status = request.getParameter("status");
            if (status == null) {
                e.setStatus("INACTIVE");
            } else {
                e.setStatus("ACTIVE");
            }

            Role r = new Role();
            r.setRoleId(Integer.parseInt(request.getParameter("roleId")));
            e.setRole(r);

            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId == null) {
                adminId = 1;
            }

            boolean success
                    = employeeDAO.updateEmployee(e, adminId);

            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&success=update"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&error=update"
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/admin/employees?action=list&error=update"
            );
        }
    }

    // =====================================================
// DELETE
// =====================================================
    private void deleteEmployee(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // Check authorization: only Manager (roleId=1) can delete employees
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || currentUserRoleId != 1) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?action=list&error=unauthorized");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            session = request.getSession();
            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId == null) {
                adminId = 1;
            }

            boolean success
                    = employeeDAO.deleteEmployee(id, adminId);

            if (success) {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&success=delete"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/admin/employees?action=list&error=delete"
                );
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/admin/employees?action=list&error=delete"
            );
        }
    }

    // =====================================================
    // TOGGLE STATUS
    // =====================================================
    private void toggleStatus(HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        // Check authorization: only Manager (roleId=1) and Store Manager (roleId=2) can toggle status
        HttpSession session = request.getSession();
        Integer currentUserRoleId = (Integer) session.getAttribute("roleId");
        
        if (currentUserRoleId == null || (currentUserRoleId != 1 && currentUserRoleId != 2)) {
            response.sendRedirect(request.getContextPath() + "/admin/employees?action=list&error=unauthorized");
            return;
        }

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            session = request.getSession();
            Integer adminId = (Integer) session.getAttribute("adminId");
            if (adminId == null) {
                adminId = 1;
            }

            boolean success
                    = employeeDAO.toggleStatus(id, adminId);

            if (success) {
                response.sendRedirect("employees?action=list&success=toggle");
            } else {
                response.sendRedirect("employees?action=list&error=toggle");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            response.sendRedirect("employees?action=list&error=toggle");
        }
    }

    
}
