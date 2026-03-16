/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;

/**
 *
 * @author qp
 */
public class StockDisposalDetail {

    private Long id;
    private StockDisposal stockDisposal;
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private String specificReason;
    private String notes;

    private String productName;
    private String productSku;
    private int currentStock;

    public StockDisposalDetail() {
    }

    public StockDisposalDetail(Integer productId, Integer quantity, BigDecimal unitCost) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitCost = unitCost;
        calculateLineTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StockDisposal getStockDisposal() {
        return stockDisposal;
    }

    public void setStockDisposal(StockDisposal stockDisposal) {
        this.stockDisposal = stockDisposal;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getSpecificReason() {
        return specificReason;
    }

    public void setSpecificReason(String specificReason) {
        this.specificReason = specificReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void calculateLineTotal() {
        this.lineTotal = unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return "StockDisposalDetail{"
                + "id=" + id
                + ", productId=" + productId
                + ", quantity=" + quantity
                + ", lineTotal=" + lineTotal
                + '}';
    }
}
