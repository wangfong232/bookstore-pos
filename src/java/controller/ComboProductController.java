package controller;

import DAO.ComboProductDAO;
import DAO.ProductDAO;
import entity.ComboProduct;
import entity.ComboProductItem;
import entity.Product;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ComboProductController", urlPatterns = {"/admin/combos"})
public class ComboProductController extends HttpServlet {

    private ComboProductDAO comboDAO;
    private ProductDAO productDAO;

    @Override
    public void init() {
        comboDAO = new ComboProductDAO();
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listCombos(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "view":
                viewCombo(request, response);
                break;
            case "delete":
                deleteCombo(request, response);
                break;
            default:
                listCombos(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        switch (action != null ? action : "") {
            case "add":
                addCombo(request, response);
                break;
            case "edit":
                editCombo(request, response);
                break;
            case "adjust":
                adjustQuantity(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/combos");
        }
    }

    private void listCombos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<ComboProduct> combos = comboDAO.getAllCombos();
        request.setAttribute("combos", combos);
        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load non-combo products for selection
        List<Product> products = productDAO.getAllActiveNonComboProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
    }

    private void viewCombo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int comboID = Integer.parseInt(request.getParameter("id"));
            ComboProduct combo = comboDAO.getComboByID(comboID);
            if (combo == null) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=not_found");
                return;
            }
            request.setAttribute("combo", combo);
            request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-view.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/combos");
        }
    }

    private void addCombo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get base product info
            String comboName = request.getParameter("comboName");
            String comboSku = request.getParameter("comboSku");
            String sellingPriceStr = request.getParameter("sellingPrice");
            String comboQuantityStr = request.getParameter("comboQuantity");
            String categoryIdStr = request.getParameter("categoryId");
            String description = request.getParameter("description");

            double sellingPrice = Double.parseDouble(sellingPriceStr);
            int comboQuantity = Integer.parseInt(comboQuantityStr);

            // Validate SKU uniqueness
            if (productDAO.isSkuExists(comboSku, null)) {
                request.setAttribute("error", "Mã SKU đã tồn tại! Vui lòng chọn mã khác.");
                List<Product> products = productDAO.getAllActiveNonComboProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            // Parse combo items
            String[] childProductIDs = request.getParameterValues("childProductID");
            String[] childQuantities = request.getParameterValues("childQuantity");

            if (childProductIDs == null || childProductIDs.length == 0) {
                request.setAttribute("error", "Combo phải có ít nhất 1 sản phẩm con!");
                List<Product> products = productDAO.getAllActiveNonComboProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            List<ComboProductItem> items = new ArrayList<>();
            for (int i = 0; i < childProductIDs.length; i++) {
                int childID = Integer.parseInt(childProductIDs[i]);
                int qty = Integer.parseInt(childQuantities[i]);
                if (childID > 0 && qty > 0) {
                    items.add(new ComboProductItem(childID, qty));
                }
            }

            // Validate stock availability
            for (ComboProductItem item : items) {
                Product child = productDAO.getProductByID(item.getChildProductID());
                int needed = item.getQuantity() * comboQuantity;
                if (child == null || child.getStock() < needed) {
                    request.setAttribute("error",
                            "Không đủ tồn kho cho sản phẩm " + (child != null ? child.getProductName() : "ID=" + item.getChildProductID()) +
                            " (cần " + needed + ", còn " + (child != null ? child.getStock() : 0) + ")");
                    List<Product> products = productDAO.getAllActiveNonComboProducts();
                    request.setAttribute("products", products);
                    request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                    return;
                }
            }

            // 1. Create product entry for combo
            Product comboProduct = new Product();
            comboProduct.setProductName(comboName);
            comboProduct.setSku(comboSku);
            comboProduct.setSellingPrice(sellingPrice);
            comboProduct.setDescription(description != null ? description : "");
            comboProduct.setIsCombo(true);
            comboProduct.setIsActive(true);
            comboProduct.setStock(comboQuantity);
            comboProduct.setReorderLevel(0);

            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                comboProduct.setCategoryId(Integer.parseInt(categoryIdStr));
            } else {
                comboProduct.setCategoryId(1); // Default category
            }

            boolean productInserted = productDAO.insertProduct(comboProduct);
            if (!productInserted) {
                request.setAttribute("error", "Không thể tạo sản phẩm combo!");
                List<Product> products = productDAO.getAllActiveNonComboProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            // Get the newly created product ID
            Product newProduct = productDAO.getProductBySku(comboSku);
            if (newProduct == null) {
                request.setAttribute("error", "Không tìm thấy sản phẩm combo vừa tạo!");
                List<Product> products = productDAO.getAllActiveNonComboProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            // 2. Create combo with stock deduction
            boolean comboCreated = comboDAO.createCombo(newProduct.getId(), comboQuantity, items);
            if (comboCreated) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=add_success");
            } else {
                request.setAttribute("error", "Không thể tạo combo! Có thể không đủ tồn kho sản phẩm con.");
                List<Product> products = productDAO.getAllActiveNonComboProducts();
                request.setAttribute("products", products);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            List<Product> products = productDAO.getAllActiveNonComboProducts();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int comboID = Integer.parseInt(request.getParameter("id"));
            ComboProduct combo = comboDAO.getComboByID(comboID);
            if (combo == null) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=not_found");
                return;
            }
            // Load combo data for editing
            Product comboProduct = productDAO.getProductByID(combo.getProductID());
            request.setAttribute("combo", combo);
            request.setAttribute("comboProduct", comboProduct);
            // Load available non-combo products for dropdown
            List<Product> products = productDAO.getAllActiveNonComboProducts();
            request.setAttribute("products", products);
            request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/combos");
        }
    }

    private void editCombo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int comboID = Integer.parseInt(request.getParameter("comboID"));
            ComboProduct combo = comboDAO.getComboByID(comboID);
            if (combo == null) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=not_found");
                return;
            }

            String comboName = request.getParameter("comboName");
            String comboSku = request.getParameter("comboSku");
            String sellingPriceStr = request.getParameter("sellingPrice");
            String description = request.getParameter("description");

            double sellingPrice = Double.parseDouble(sellingPriceStr);

            // Validate SKU uniqueness (exclude current product)
            if (productDAO.isSkuExists(comboSku, combo.getProductID())) {
                request.setAttribute("error", "Mã SKU đã tồn tại! Vui lòng chọn mã khác.");
                request.setAttribute("combo", combo);
                request.setAttribute("products", productDAO.getAllActiveNonComboProducts());
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            // Parse new combo items
            String[] childProductIDs = request.getParameterValues("childProductID");
            String[] childQuantities = request.getParameterValues("childQuantity");

            if (childProductIDs == null || childProductIDs.length == 0) {
                request.setAttribute("error", "Combo phải có ít nhất 1 sản phẩm con!");
                request.setAttribute("combo", combo);
                request.setAttribute("products", productDAO.getAllActiveNonComboProducts());
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
                return;
            }

            List<ComboProductItem> newItems = new ArrayList<>();
            for (int i = 0; i < childProductIDs.length; i++) {
                int childID = Integer.parseInt(childProductIDs[i]);
                int qty = Integer.parseInt(childQuantities[i]);
                if (childID > 0 && qty > 0) {
                    newItems.add(new ComboProductItem(childID, qty));
                }
            }

            boolean success = comboDAO.updateCombo(comboID, combo.getProductID(),
                    comboName, comboSku, sellingPrice,
                    description != null ? description : "", newItems);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=update_success");
            } else {
                request.setAttribute("error", "Không thể cập nhật combo! Có thể không đủ tồn kho sản phẩm con.");
                request.setAttribute("combo", combo);
                request.setAttribute("products", productDAO.getAllActiveNonComboProducts());
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-combo-detail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/combos?msg=update_error");
        }
    }

    private void adjustQuantity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int comboID = Integer.parseInt(request.getParameter("comboID"));
            int delta = Integer.parseInt(request.getParameter("delta"));

            boolean success = comboDAO.adjustComboQuantity(comboID, delta);

            if (success) {
                // Also update the Product.Stock to match ComboQuantity
                ComboProduct combo = comboDAO.getComboByID(comboID);
                if (combo != null) {
                    Product product = productDAO.getProductByID(combo.getProductID());
                    if (product != null) {
                        product.setStock(combo.getComboQuantity());
                        productDAO.updateProduct(product);
                    }
                }
                String msg = delta > 0 ? "increase_success" : "decrease_success";
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=" + msg);
            } else {
                String errorMsg = delta > 0 ? "Không đủ tồn kho sản phẩm con để tăng combo!" : "Không thể giảm số lượng combo!";
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=adjust_error&error=" + java.net.URLEncoder.encode(errorMsg, "UTF-8"));
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/combos?msg=adjust_error");
        }
    }

    private void deleteCombo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int comboID = Integer.parseInt(request.getParameter("id"));
            boolean success = comboDAO.deleteCombo(comboID);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/combos?msg=delete_error");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/combos");
        }
    }
}
