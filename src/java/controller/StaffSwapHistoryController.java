package controller;

import DAO.ShiftSwapDAO;
import entity.ShiftSwapRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/staff/my-swaps")
public class StaffSwapHistoryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("employeeId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int employeeId = (Integer) session.getAttribute("employeeId");

        ShiftSwapDAO dao = new ShiftSwapDAO();
        List<ShiftSwapRequest> myRequests = dao.getMySwapRequests(employeeId);
        request.setAttribute("myRequests", myRequests);

        request.getRequestDispatcher("/AdminLTE-3.2.0/staff-swap-history.jsp")
                .forward(request, response);
    }
}
