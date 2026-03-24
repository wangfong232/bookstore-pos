/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author qp
 */
public class InventoryTransaction {

    public static final String TYPE_IN = "IN";
    public static final String TYPE_OUT = "OUT";
    public static final String TYPE_ADJUSTMENT = "ADJUSTMENT";

    public static final String REF_GOODS_RECEIPT = "GOODS_RECEIPT";
    public static final String REF_SALE = "SALE";
    public static final String REF_DISPOSAL = "DISPOSAL";
    public static final String REF_STOCK_TAKE = "STOCK_TAKE";
    public static final String REF_MANUAL = "MANUAL";

    private Long transactionId;
    private Integer productId;
    private String transactionType;
    private String referenceType;
    private Long referenceId;
    private String referenceCode;
    private Integer quantityChange;
    private Integer stockBefore;
    private Integer stockAfter;
    private BigDecimal unitCost;
    private String notes;
    private Integer createdBy;
    private LocalDateTime transactionDate;

    private String productName;
    private String productSku;
    private String createdByName;
    private String referenceUrl;

    public InventoryTransaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public InventoryTransaction(Integer productId, String transactionType, String referenceType,
            Long referenceId, String referenceCode, Integer quantityChange,
            Integer stockBefore, Integer createdBy) {
        this();
        this.productId = productId;
        this.transactionType = transactionType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.referenceCode = referenceCode;
        this.quantityChange = quantityChange;
        this.stockBefore = stockBefore;
        this.stockAfter = stockBefore + quantityChange;
        this.createdBy = createdBy;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public Integer getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(Integer quantityChange) {
        this.quantityChange = quantityChange;
    }

    public Integer getStockBefore() {
        return stockBefore;
    }

    public void setStockBefore(Integer stockBefore) {
        this.stockBefore = stockBefore;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public void setStockAfter(Integer stockAfter) {
        this.stockAfter = stockAfter;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getReferenceUrl() {
        return referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
    }

    public boolean isInBound() {
        return TYPE_IN.equals(transactionType);
    }

    public boolean isOutBound() {
        return TYPE_OUT.equals(transactionType);
    }

    public boolean isAdjustment() {
        return TYPE_ADJUSTMENT.equals(transactionType);
    }

    public BigDecimal getTransactionValue() {
        if (unitCost == null) {
            return BigDecimal.ZERO;
        }
        return unitCost.multiply(BigDecimal.valueOf(Math.abs(quantityChange)));
    }

    public static InventoryTransaction createGoodsReceiptTransaction(Integer productId,
            Integer quantity, Integer stockBefore, BigDecimal unitCost, Long receiptId,
            String receiptCode, Integer createdBy) {
        InventoryTransaction tx = new InventoryTransaction(productId, TYPE_IN, REF_GOODS_RECEIPT, receiptId,
                receiptCode, quantity, stockBefore, createdBy);
        tx.setUnitCost(unitCost);
        return tx;
    }

    public static InventoryTransaction createSaleTransaction(
            Integer productId, Integer quantity, Integer stockBefore,
            BigDecimal unitCost, Long invoiceId, String invoiceCode, Integer createdBy) {

        InventoryTransaction tx = new InventoryTransaction(
                productId, TYPE_OUT, REF_SALE,
                invoiceId, invoiceCode, -quantity, stockBefore, createdBy);
        tx.setUnitCost(unitCost);
        return tx;
    }

    public static InventoryTransaction createDisposalTransaction(
            Integer productId, Integer quantity, Integer stockBefore,
            BigDecimal unitCost, Long disposalId, String disposalCode, Integer createdBy) {

        InventoryTransaction tx = new InventoryTransaction(
                productId, TYPE_OUT, REF_DISPOSAL,
                disposalId, disposalCode, -quantity, stockBefore, createdBy);
        tx.setUnitCost(unitCost);
        return tx;
    }

    public static InventoryTransaction createStockTakeAdjustment(
            Integer productId, Integer varianceQty, Integer stockBefore,
            BigDecimal unitCost, Long stockTakeId, String stockTakeCode,
            Integer createdBy, String notes) {

        InventoryTransaction tx = new InventoryTransaction(
                productId, TYPE_ADJUSTMENT, REF_STOCK_TAKE,
                stockTakeId, stockTakeCode, varianceQty, stockBefore, createdBy);
        tx.setUnitCost(unitCost);
        tx.setNotes(notes);
        return tx;
    }

    @Override
    public String toString() {
        return "InventoryTransaction{"
                + "transactionId=" + transactionId
                + ", productId=" + productId
                + ", transactionType='" + transactionType + '\''
                + ", quantityChange=" + quantityChange
                + ", stockBefore=" + stockBefore
                + ", stockAfter=" + stockAfter
                + '}';
    }
}
