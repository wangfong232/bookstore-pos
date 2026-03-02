/*
 * Product entity for POS module
 */
package entity;

import java.util.Date;

public class Product {

    private int id;
    private String productName;
    private int categoryId;
    private Integer brandId;
    private Integer supplierId;
    private String sku;

    private String description;
    private String specifications;
    private String imageURL;

    private Double costPrice;
    private double sellingPrice;
    private Double compareAtPrice;

    private int stock;
    private int reservedStock;
    private int reorderLevel;
    private Date lastLowStockAlertAt;

    private boolean isActive;
    private Date createdDate;
    private Date updatedDate;

    public Product() {
        this.stock = 0;
        this.reservedStock = 0;
        this.reorderLevel = 0;
        this.isActive = true;
    }

    public Product(int id, String productName, double sellingPrice) {
        this();
        this.id = id;
        this.productName = productName;
        this.sellingPrice = sellingPrice;
    }

    public Product(int id, String productName, int categoryId, String sku,
                   double sellingPrice, int stock) {
        this();
        this.id = id;
        this.productName = productName;
        this.categoryId = categoryId;
        this.sku = sku;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductID() {
        return id;
    }

    public void setProductID(int productID) {
        this.id = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    // Alias methods for POS module (ID-style names)
    public int getCategoryID() {
        return categoryId;
    }

    public void setCategoryID(int categoryID) {
        this.categoryId = categoryID;
    }

    public int getBrandID() {
        return brandId != null ? brandId : 0;
    }

    public void setBrandID(int brandID) {
        this.brandId = brandID;
    }

    public int getSupplierID() {
        return supplierId != null ? supplierId : 0;
    }

    public void setSupplierID(int supplierID) {
        this.supplierId = supplierID;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getCompareAtPrice() {
        return compareAtPrice;
    }

    public void setCompareAtPrice(Double compareAtPrice) {
        this.compareAtPrice = compareAtPrice;
    }
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getReservedStock() {
        return reservedStock;
    }

    public void setReservedStock(int reservedStock) {
        this.reservedStock = reservedStock;
    }
    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Date getLastLowStockAlertAt() {
        return lastLowStockAlertAt;
    }

    public void setLastLowStockAlertAt(Date lastLowStockAlertAt) {
        this.lastLowStockAlertAt = lastLowStockAlertAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

  
    public int getAvailableStock() {
        return stock - reservedStock;
    }

  
    public boolean isLowStock() {
        return getAvailableStock() <= reorderLevel;
    }

   
    public boolean isOutOfStock() {
        return getAvailableStock() <= 0;
    }

    public double getProfit() {
        if (costPrice == null) return 0;
        return sellingPrice - costPrice;
    }

   
    public double getProfitMargin() {
        if (costPrice == null || sellingPrice == 0) return 0;
        return ((sellingPrice - costPrice) / sellingPrice) * 100;
    }

  
    public boolean isOnSale() {
        return compareAtPrice != null && compareAtPrice > sellingPrice;
    }

 
    public double getDiscountPercentage() {
        if (!isOnSale()) return 0;
        return ((compareAtPrice - sellingPrice) / compareAtPrice) * 100;
    }

   
    public boolean reserveStock(int quantity) {
        if (getAvailableStock() >= quantity) {
            this.reservedStock += quantity;
            return true;
        }
        return false;
    }

    
    public void releaseStock(int quantity) {
        this.reservedStock = Math.max(0, this.reservedStock - quantity);
    }

    
    public boolean deductStock(int quantity) {
        if (this.stock >= quantity) {
            this.stock -= quantity;
            this.reservedStock = Math.max(0, this.reservedStock - quantity);
            return true;
        }
        return false;
    }


    public void addStock(int quantity) {
        this.stock += quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", sku='" + sku + '\'' +
                ", sellingPrice=" + sellingPrice +
                ", stock=" + stock +
                ", availableStock=" + getAvailableStock() +
                ", isActive=" + isActive +
                '}';
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
