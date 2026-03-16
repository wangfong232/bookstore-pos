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
public class StockTakeDetail {

    public static final String REASON_LOSS = "LOSS";
    public static final String REASON_DAMAGE = "DAMAGE";
    public static final String REASON_THEFT = "THEFT";
    public static final String REASON_ERROR = "ERROR";
    public static final String REASON_OTHER = "OTHER";

    private Long id;
    private StockTake stockTake;
    private Integer productId;

    private Integer systemQuantity;
    private Integer actualQuantity;
    private BigDecimal unitCost;
    private String varianceReason;
    private String notes;

    private String productName;
    private String productSku;

    public StockTakeDetail() {
        this.actualQuantity = 0;
    }

    public StockTakeDetail(Integer productId, Integer systemQuantity, BigDecimal unitCost) {
        this();
        this.productId = productId;
        this.systemQuantity = systemQuantity;
        this.unitCost = unitCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StockTake getStockTake() {
        return stockTake;
    }

    public void setStockTake(StockTake stockTake) {
        this.stockTake = stockTake;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(Integer systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    public Integer getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(Integer actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getVarianceReason() {
        return varianceReason;
    }

    public void setVarianceReason(String varianceReason) {
        this.varianceReason = varianceReason;
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

    public Integer getVarianceQuantity() {
        return actualQuantity - systemQuantity;
    }

    public BigDecimal getVarianceValue() {
        return unitCost.multiply(BigDecimal.valueOf(actualQuantity - systemQuantity));
    }

    public boolean hasVariance() {
        return !actualQuantity.equals(systemQuantity);
    }

    public boolean isShortage() {
        return actualQuantity < systemQuantity;
    }

    public boolean isSurplus() {
        return actualQuantity > systemQuantity;
    }

    public void updateCount(Integer newActualQuantity, String reason, String note) {
        this.actualQuantity = newActualQuantity;
        if (hasVariance()) {
            this.varianceReason = reason;
            this.notes = note;
        } else {
            this.varianceReason = null;
            this.notes = null;
        }
    }

    @Override
    public String toString() {
        return "StockTakeDetail{"
                + "id=" + id
                + ", productId=" + productId
                + ", systemQuantity=" + systemQuantity
                + ", actualQuantity=" + actualQuantity
                + ", variance=" + getVarianceQuantity()
                + '}';
    }
}
