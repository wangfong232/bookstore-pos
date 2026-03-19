package controller;

import DAO.BrandDAO;
import DAO.CategoryDAO;
import DAO.ProductDAO;
import DAO.SupplierDAO;
import entity.Brand;
import entity.Category;
import entity.Product;
import entity.Supplier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(name = "ProductController", urlPatterns = {"/admin/products"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ProductController extends HttpServlet {
    
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private BrandDAO brandDAO;
    private SupplierDAO supplierDAO;
    
    private static final String UPLOAD_DIR = "uploads/products";
    
    @Override
    public void init() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
        brandDAO = new BrandDAO();
        supplierDAO = new SupplierDAO();
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
                listProducts(request, response);
                break;
            case "add":
                showAddForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            case "toggle":
                toggleProductStatus(request, response);
                break;
            default:
                listProducts(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            addProduct(request, response);
        } else if ("edit".equals(action)) {
            updateProduct(request, response);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get parameters
        String search = request.getParameter("search");
        String statusParam = request.getParameter("status");
        String categoryParam = request.getParameter("categoryId");
        String brandParam = request.getParameter("brandId");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        String pageParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");
        
        // Default values
        int page = 1;
        int pageSize = 10;
        Boolean isActive = null;
        Integer categoryId = null;
        Integer brandId = null;
        
        // Parse parameters
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
            } catch (NumberFormatException e) {
                pageSize = 10;
            }
        }
        
        if ("active".equals(statusParam)) {
            isActive = true;
        } else if ("inactive".equals(statusParam)) {
            isActive = false;
        }
        
        if (categoryParam != null && !categoryParam.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryParam);
            } catch (NumberFormatException e) {
                categoryId = null;
            }
        }
        
        if (brandParam != null && !brandParam.isEmpty()) {
            try {
                brandId = Integer.parseInt(brandParam);
            } catch (NumberFormatException e) {
                brandId = null;
            }
        }
        
        // Get data
        List<Product> products = productDAO.getProducts(search, isActive, categoryId, brandId, sortBy, sortOrder, page, pageSize);
        int totalRecords = productDAO.getTotalProducts(search, isActive, categoryId, brandId);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        
        // Get categories and brands for filter
        List<Category> categories = categoryDAO.getAllActiveCategories();
        List<Brand> brands = brandDAO.getAllBrands();
        
        // Set attributes
        request.setAttribute("products", products);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("search", search);
        request.setAttribute("status", statusParam);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("brandId", brandId);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        // Forward to JSP
        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-list.jsp").forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Load categories, brands, suppliers for dropdowns
        List<Category> categories = categoryDAO.getAllActiveCategories();
        List<Brand> brands = brandDAO.getAllBrands();
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();
        
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.setAttribute("suppliers", suppliers);
        
        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.getProductByID(id);
        
        // Load categories, brands, suppliers for dropdowns
        List<Category> categories = categoryDAO.getAllActiveCategories();
        List<Brand> brands = brandDAO.getAllBrands();
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();
        
        request.setAttribute("product", product);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.setAttribute("suppliers", suppliers);
        
        request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get form data
            String productName = request.getParameter("productName");
            String sku = request.getParameter("sku");
            String description = request.getParameter("description");
            String specifications = request.getParameter("specifications");
            
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String brandIdParam = request.getParameter("brandId");
            String supplierIdParam = request.getParameter("supplierId");
            
            boolean isActive = "on".equals(request.getParameter("isActive"));
            
            // Handle image upload
            String imageURL = handleImageUpload(request);
            
            // Check if SKU already exists
            if (productDAO.isSkuExists(sku, null)) {
                Product product = createProductFromRequest(request, imageURL);
                request.setAttribute("error", "Mã SKU đã tồn tại! Vui lòng chọn mã khác.");
                request.setAttribute("product", product);
                loadFormData(request);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
                return;
            }
            
            // Create product object
            Product product = new Product();
            product.setProductName(productName);
            product.setSku(sku);
            product.setDescription(description);
            product.setSpecifications(specifications);
            product.setCategoryId(categoryId);
            
            if (brandIdParam != null && !brandIdParam.isEmpty()) {
                product.setBrandId(Integer.parseInt(brandIdParam));
            }
            
            if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
                product.setSupplierId(Integer.parseInt(supplierIdParam));
            }
            
            // Parse price fields from form
            String costPriceParam = request.getParameter("costPrice");
            if (costPriceParam != null && !costPriceParam.isEmpty()) {
                product.setCostPrice(Double.parseDouble(costPriceParam));
            } else {
                product.setCostPrice(0.0);
            }
            
            String sellingPriceParam = request.getParameter("sellingPrice");
            if (sellingPriceParam != null && !sellingPriceParam.isEmpty()) {
                product.setSellingPrice(Double.parseDouble(sellingPriceParam));
            } else {
                product.setSellingPrice(0.0);
            }
            
            String compareAtPriceParam = request.getParameter("compareAtPrice");
            if (compareAtPriceParam != null && !compareAtPriceParam.isEmpty()) {
                product.setCompareAtPrice(Double.parseDouble(compareAtPriceParam));
            } else {
                product.setCompareAtPrice(null);
            }
            
            product.setStock(0);
            product.setReorderLevel(10);
            
            product.setImageURL(imageURL);
            product.setIsActive(isActive);
            
            boolean success = productDAO.insertProduct(product);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=add_success");
            } else {
                request.setAttribute("error", "Thêm sản phẩm thất bại!");
                request.setAttribute("product", product);
                loadFormData(request);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
        }
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            String sku = request.getParameter("sku");
            
            // Check if SKU already exists (excluding current product)
            if (productDAO.isSkuExists(sku, productId)) {
                Product product = createProductFromRequest(request, null);
                product.setId(productId);
                request.setAttribute("error", "Mã SKU đã tồn tại! Vui lòng chọn mã khác.");
                request.setAttribute("product", product);
                loadFormData(request);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
                return;
            }
            
            // Get existing product
            Product product = productDAO.getProductByID(productId);
            String oldImageURL = product.getImageURL();
            
            // Handle image upload (keep old image if no new upload)
            String imageURL = handleImageUpload(request);
            if (imageURL == null || imageURL.isEmpty()) {
                imageURL = oldImageURL;
            } else {
                // Delete old image if new one uploaded
                deleteOldImage(oldImageURL);
            }
            
            // Update product data (keep existing price and stock values)
            product.setProductName(request.getParameter("productName"));
            product.setSku(sku);
            product.setDescription(request.getParameter("description"));
            product.setSpecifications(request.getParameter("specifications"));
            product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            
            String brandIdParam = request.getParameter("brandId");
            if (brandIdParam != null && !brandIdParam.isEmpty()) {
                product.setBrandId(Integer.parseInt(brandIdParam));
            } else {
                product.setBrandId(null);
            }
            
            String supplierIdParam = request.getParameter("supplierId");
            if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
                product.setSupplierId(Integer.parseInt(supplierIdParam));
            } else {
                product.setSupplierId(null);
            }
            
            // Parse price fields from form
            String costPriceParam = request.getParameter("costPrice");
            if (costPriceParam != null && !costPriceParam.isEmpty()) {
                product.setCostPrice(Double.parseDouble(costPriceParam));
            }
            
            String sellingPriceParam = request.getParameter("sellingPrice");
            if (sellingPriceParam != null && !sellingPriceParam.isEmpty()) {
                product.setSellingPrice(Double.parseDouble(sellingPriceParam));
            }
            
            String compareAtPriceParam = request.getParameter("compareAtPrice");
            if (compareAtPriceParam != null && !compareAtPriceParam.isEmpty()) {
                product.setCompareAtPrice(Double.parseDouble(compareAtPriceParam));
            } else {
                product.setCompareAtPrice(null);
            }
            // product.setStock() - not changed
            // product.setReorderLevel() - not changed
            
            product.setImageURL(imageURL);
            product.setIsActive("on".equals(request.getParameter("isActive")));
            
            boolean success = productDAO.updateProduct(product);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=update_success");
            } else {
                request.setAttribute("error", "Cập nhật sản phẩm thất bại!");
                request.setAttribute("product", product);
                loadFormData(request);
                request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            loadFormData(request);
            request.getRequestDispatcher("/AdminLTE-3.2.0/admin-product-detail.jsp").forward(request, response);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        // Soft delete: Set IsActive = false instead of deleting
        Product product = productDAO.getProductByID(id);
        if (product != null) {
            product.setIsActive(false);
            boolean success = productDAO.updateProduct(product);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=delete_success");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=delete_error");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/products?msg=delete_error");
        }
    }
    
    private void toggleProductStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        
        Product product = productDAO.getProductByID(id);
        if (product != null) {
            // Toggle status
            product.setIsActive(!product.isIsActive());
            boolean success = productDAO.updateProduct(product);
            
            if (success) {
                String msg = product.isIsActive() ? "activate_success" : "deactivate_success";
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=" + msg);
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/products?msg=toggle_error");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/products?msg=toggle_error");
        }
    }
    
    private String handleImageUpload(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("imageFile");
        
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }
        
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        
        // Read file bytes once (so we can save to multiple locations)
        byte[] fileBytes = filePart.getInputStream().readAllBytes();
        
        // 1. Save to deploy directory (for immediate access)
        String deployUploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File deployDir = new File(deployUploadPath);
        if (!deployDir.exists()) {
            deployDir.mkdirs();
        }
        Path deployFilePath = Paths.get(deployUploadPath, uniqueFileName);
        Files.write(deployFilePath, fileBytes);
        
        // 2. Save to project source directory (for persistence across rebuilds)
        String sourceUploadPath = getSourceUploadPath();
        if (sourceUploadPath != null) {
            File sourceDir = new File(sourceUploadPath);
            if (!sourceDir.exists()) {
                sourceDir.mkdirs();
            }
            Path sourceFilePath = Paths.get(sourceUploadPath, uniqueFileName);
            Files.write(sourceFilePath, fileBytes);
        }
        
        // Return relative URL
        return request.getContextPath() + "/" + UPLOAD_DIR + "/" + uniqueFileName;
    }
    
    private void deleteOldImage(String imageURL) {
        if (imageURL != null && !imageURL.isEmpty() && imageURL.contains(UPLOAD_DIR)) {
            try {
                String fileName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
                
                // Delete from deploy directory
                String deployUploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
                Path deployFilePath = Paths.get(deployUploadPath, fileName);
                Files.deleteIfExists(deployFilePath);
                
                // Delete from source directory
                String sourceUploadPath = getSourceUploadPath();
                if (sourceUploadPath != null) {
                    Path sourceFilePath = Paths.get(sourceUploadPath, fileName);
                    Files.deleteIfExists(sourceFilePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Derive the project source upload path from the deploy path.
     * NetBeans deploys to build/web/, so we go up to find the project root
     * and then use web/uploads/products/ as the source directory.
     */
    private String getSourceUploadPath() {
        String deployPath = getServletContext().getRealPath("");
        if (deployPath == null) return null;
        
        String normalized = deployPath.replace("\\", "/");
        // Remove trailing slash
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        
        // NetBeans: deploy path ends with .../build/web
        int buildWebIndex = normalized.lastIndexOf("/build/web");
        if (buildWebIndex >= 0) {
            String projectRoot = normalized.substring(0, buildWebIndex);
            return projectRoot + "/web/" + UPLOAD_DIR;
        }
        
        return null;
    }
    
    private Product createProductFromRequest(HttpServletRequest request, String imageURL) {
        Product product = new Product();
        product.setProductName(request.getParameter("productName"));
        product.setSku(request.getParameter("sku"));
        product.setDescription(request.getParameter("description"));
        product.setSpecifications(request.getParameter("specifications"));
        product.setImageURL(imageURL);
        
        try {
            product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        } catch (Exception e) {}
        
        String brandIdParam = request.getParameter("brandId");
        if (brandIdParam != null && !brandIdParam.isEmpty()) {
            product.setBrandId(Integer.parseInt(brandIdParam));
        }
        
        String supplierIdParam = request.getParameter("supplierId");
        if (supplierIdParam != null && !supplierIdParam.isEmpty()) {
            product.setSupplierId(Integer.parseInt(supplierIdParam));
        }
        
        // Parse price fields from form
        String costPriceParam = request.getParameter("costPrice");
        if (costPriceParam != null && !costPriceParam.isEmpty()) {
            product.setCostPrice(Double.parseDouble(costPriceParam));
        } else {
            product.setCostPrice(0.0);
        }
        
        String sellingPriceParam = request.getParameter("sellingPrice");
        if (sellingPriceParam != null && !sellingPriceParam.isEmpty()) {
            product.setSellingPrice(Double.parseDouble(sellingPriceParam));
        } else {
            product.setSellingPrice(0.0);
        }
        
        String compareAtPriceParam = request.getParameter("compareAtPrice");
        if (compareAtPriceParam != null && !compareAtPriceParam.isEmpty()) {
            product.setCompareAtPrice(Double.parseDouble(compareAtPriceParam));
        } else {
            product.setCompareAtPrice(null);
        }
        
        product.setStock(0);
        product.setReorderLevel(10);
        
        product.setIsActive("on".equals(request.getParameter("isActive")));
        
        return product;
    }
    
    private void loadFormData(HttpServletRequest request) {
        List<Category> categories = categoryDAO.getAllActiveCategories();
        List<Brand> brands = brandDAO.getAllBrands();
        List<Supplier> suppliers = supplierDAO.getAllActiveSuppliers();
        
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);
        request.setAttribute("suppliers", suppliers);
    }
}
