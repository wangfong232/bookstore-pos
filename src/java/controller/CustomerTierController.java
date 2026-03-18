package controller;

import DAO.CustomerTierDAO;
import entity.CustomerTier;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerTierController", urlPatterns = { "/customer-tiers" })
public class CustomerTierController extends HttpServlet {

    private final CustomerTierDAO tierDAO = new CustomerTierDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteTier(request, response);
                break;
            default:
                listTiers(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if ("add".equals(action)) {
            addTier(request, response);
        } else if ("update".equals(action)) {
            updateTier(request, response);
        } else {
            listTiers(request, response);
        }
    }

    private void listTiers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<CustomerTier> list = tierDAO.getAll();
        request.setAttribute("tiers", list);
        request.getRequestDispatcher("/AdminLTE-3.2.0/customer-tier-settings.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        CustomerTier tier = tierDAO.getTierById(id);
        request.setAttribute("selectedTier", tier);

        // Also load the list so the table is still populated
        List<CustomerTier> list = tierDAO.getAll();
        request.setAttribute("tiers", list);

        request.getRequestDispatcher("/AdminLTE-3.2.0/customer-tier-settings.jsp").forward(request, response);
    }

    private void addTier(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String name = request.getParameter("tierName");
        double minSpent = Double.parseDouble(request.getParameter("minTotalSpent").replace(",", ""));
        double pointRate = Double.parseDouble(request.getParameter("pointRate").replace("x", "").trim());
        double discountRate = Double.parseDouble(request.getParameter("discountRate").replace("%", "").trim());

        CustomerTier tier = new CustomerTier();
        tier.setTierName(name);
        tier.setMinTotalSpent(minSpent);
        tier.setPointRate(pointRate);
        tier.setDiscountRate(discountRate);

        tierDAO.insert(tier);
        response.sendRedirect(request.getContextPath() + "/admin/customer-tiers?msg=add_success");
    }

    private void updateTier(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("tierID"));
        String name = request.getParameter("tierName");
        // Remove formatting characters before parsing
        double minSpent = Double.parseDouble(request.getParameter("minTotalSpent").replace(",", ""));
        double pointRate = Double.parseDouble(request.getParameter("pointRate").replace("x", "").trim());
        double discountRate = Double.parseDouble(request.getParameter("discountRate").replace("%", "").trim());

        CustomerTier tier = new CustomerTier();
        tier.setTierID(id);
        tier.setTierName(name);
        tier.setMinTotalSpent(minSpent);
        tier.setPointRate(pointRate);
        tier.setDiscountRate(discountRate);

        tierDAO.update(tier);
        response.sendRedirect(
                request.getContextPath() + "/customer-tiers?msg=update_success&action=edit&id=" + id);
    }

    private void deleteTier(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        tierDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/admin/customer-tiers?msg=delete_success");
    }
}
