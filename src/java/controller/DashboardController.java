package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        // Set attributes for dashboard
        String fullName = (String) session.getAttribute("fullName");
        String roleName = (String) session.getAttribute("roleName");
        
        request.setAttribute("fullName", fullName);
        request.setAttribute("roleName", roleName);
        
        // Fetch counts for Admin/Manager dashboard
        if (session.getAttribute("roleId") != null) {
            int roleId = (int) session.getAttribute("roleId");
            if (roleId == 1 || roleId == 2) {
                DAO.EmployeeDAO employeeDAO = new DAO.EmployeeDAO();
                DAO.ProductDAO productDAO = new DAO.ProductDAO();
                DAO.PurchaseOrderDAO purchaseOrderDAO = new DAO.PurchaseOrderDAO();
                DAO.SupplierDAO supplierDAO = new DAO.SupplierDAO();
                
                int totalEmployees = employeeDAO.getTotalEmployees(null, null, null);
                int totalProducts = productDAO.getTotalProducts(null, null, null, null);
                int totalPOs = purchaseOrderDAO.countPOs();
                int totalSuppliers = supplierDAO.countSuppliers();
                
                request.setAttribute("totalEmployees", totalEmployees);
                request.setAttribute("totalProducts", totalProducts);
                request.setAttribute("totalPOs", totalPOs);
                request.setAttribute("totalSuppliers", totalSuppliers);
            }
        }
        
        request.getRequestDispatcher("/AdminLTE-3.2.0/dashboard.jsp").forward(request, response);
    }
}
