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
        
        request.getRequestDispatcher("/AdminLTE-3.2.0/dashboard.jsp").forward(request, response);
    }
}
