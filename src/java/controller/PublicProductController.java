package controller;

import DAO.BrandDAO;
import DAO.CategoryDAO;
import DAO.ProductDAO;
import entity.Brand;
import entity.Category;
import entity.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/products")
public class PublicProductController extends HttpServlet {

    private ProductDAO productDAO;
    private BrandDAO brandDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() {
        productDAO = new ProductDAO();
        brandDAO = new BrandDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Read filter parameters
        String search = request.getParameter("search");
        String categoryIdStr = request.getParameter("categoryId");
        String brandIdStr = request.getParameter("brandId");
        String stockStatus = request.getParameter("stockStatus");
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");

        Integer categoryId = null;
        Integer brandId = null;
        int page = 1;
        int pageSize = 12; // default 12 cards per page

        try {
            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
            }
        } catch (NumberFormatException e) { }

        try {
            if (brandIdStr != null && !brandIdStr.isEmpty()) {
                brandId = Integer.parseInt(brandIdStr);
            }
        } catch (NumberFormatException e) { }

        try {
            if (pageStr != null && !pageStr.isEmpty()) {
                page = Math.max(1, Integer.parseInt(pageStr));
            }
        } catch (NumberFormatException e) { }

        try {
            if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (NumberFormatException e) { }

        // Validate stockStatus
        if (stockStatus != null && !stockStatus.equals("in_stock") && !stockStatus.equals("out_of_stock")) {
            stockStatus = null;
        }

        // Only show active products
        Boolean isActive = true;

        // Get products with filters and paging (including stock status)
        List<Product> products = productDAO.getProducts(search, isActive, categoryId, brandId,
                stockStatus, "ProductName", "ASC", page, pageSize);
        int totalRecords = productDAO.getTotalProducts(search, isActive, categoryId, brandId, stockStatus);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // Get all active brands and categories for filter sidebar
        List<Brand> brands = brandDAO.getAllActiveBrands();
        List<Category> categories = categoryDAO.getAllActiveCategories();

        // Set attributes for JSP
        request.setAttribute("products", products);
        request.setAttribute("brands", brands);
        request.setAttribute("categories", categories);
        request.setAttribute("search", search);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("brandId", brandId);
        request.setAttribute("stockStatus", stockStatus);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/AdminLTE-3.2.0/public-product-list.jsp").forward(request, response);
    }
}
