package controller;

import DAO.CustomerDAO;
import DAO.CustomerTierDAO;
import entity.Customer;
import entity.CustomerTier;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CustomerController", urlPatterns = { "/customers" })
public class CustomerController extends HttpServlet {

    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "search":
                searchCustomers(request, response);
                break;
            case "delete":
                deleteCustomer(request, response);
                break;
            case "add":
                // Màn hình customer-management.jsp có nút thêm,
                // nhưng trong file jsp chưa thấy form thêm riêng.
                // Có thể dùng detail panel để thêm hoặc redirect sang trang khác.
                // Tạm thời quay về list.
                listCustomers(request, response);
                break;
            default:
                listCustomers(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateCustomer(request, response);
        } else if ("add".equals(action)) {
            addCustomer(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/customers");
        }
    }

    private void listCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Customer> list = customerDAO.getAll();
        calculateAndSetTiers(list);
        filterByTier(request, list);
        request.setAttribute("customers", list);
        loadTiersForFilter(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/customer-management.jsp").forward(request, response);
    }

    private void searchCustomers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Customer> list = customerDAO.search(keyword);
        calculateAndSetTiers(list);
        filterByTier(request, list);
        request.setAttribute("customers", list);
        loadTiersForFilter(request);
        request.getRequestDispatcher("/AdminLTE-3.2.0/customer-management.jsp").forward(request, response);
    }

    private void addCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String phone = request.getParameter("phone");
        String name = request.getParameter("customerName");
        String email = request.getParameter("email");
        String birthdayStr = request.getParameter("birthday");
        String status = request.getParameter("status");
        String note = request.getParameter("note");

        Customer c = new Customer();
        c.setCustomerID(phone); // Phone as ID
        c.setPhoneNumber(phone);
        c.setCustomerName(name);
        c.setEmail(email);
        if (birthdayStr != null && !birthdayStr.isEmpty()) {
            try {
                c.setBirthday(LocalDate.parse(birthdayStr));
            } catch (Exception e) {
            }
        }
        c.setStatus(status != null ? status : "ACTIVE");
        c.setNote(note);

        customerDAO.insert(c);
        response.sendRedirect(request.getContextPath() + "/customers?msg=add_success");
    }

    private void updateCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("customerID");
        String name = request.getParameter("customerName");
        String email = request.getParameter("email");
        String birthdayStr = request.getParameter("birthday");

        Customer c = customerDAO.getById(id);
        if (c != null) {
            if (name != null && !name.isEmpty()) {
                c.setCustomerName(name);
            }
            if (email != null) {
                c.setEmail(email);
            }
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                try {
                    c.setBirthday(LocalDate.parse(birthdayStr));
                } catch (Exception e) {
                }
            }
            String note = request.getParameter("note");
            if (note != null) {
                c.setNote(note);
            }

            customerDAO.update(c);
            response.sendRedirect(request.getContextPath() + "/customers?msg=update_success");
        } else {
            response.sendRedirect(request.getContextPath() + "/customers?msg=not_found");
        }
    }

    private void deleteCustomer(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String id = request.getParameter("id");
        customerDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/customers?msg=delete_success");
    }

    private void calculateAndSetTiers(List<Customer> customers) {
        CustomerTierDAO tierDAO = new CustomerTierDAO();
        List<CustomerTier> tiers = tierDAO.getAll();
        // Sort tiers descending by MinPoint
        tiers.sort((t1, t2) -> Double.compare(t2.getMinPoint(), t1.getMinPoint()));

        for (Customer c : customers) {
            String assignedTier = "Unknown";
            for (CustomerTier t : tiers) {
                if (c.getPoints() >= t.getMinPoint()) {
                    assignedTier = t.getTierName();
                    break;
                }
            }
            c.setTierName(assignedTier);
        }
    }

    private void filterByTier(HttpServletRequest request, List<Customer> list) {
        String tierFilter = request.getParameter("tier");
        if (tierFilter != null && !tierFilter.trim().isEmpty() && !tierFilter.equals("all")) {
            list.removeIf(c -> !tierFilter.equalsIgnoreCase(c.getTierName()));
        }
    }

    private void loadTiersForFilter(HttpServletRequest request) {
        CustomerTierDAO tierDAO = new CustomerTierDAO();
        List<CustomerTier> tiers = tierDAO.getAll();
        request.setAttribute("tiers", tiers);
    }
}
