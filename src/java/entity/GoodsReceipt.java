/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.DateUtil;

/**
 *
 * @author qp
 */
public class GoodsReceipt {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_COMPLETED = "COMPLETED";

    private Long id;
    private String receiptNumber;
    private PurchaseOrder purchaseOrder;
    private Long poId;
    private LocalDateTime receiptDate;
    private String status;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private String notes;
    private Integer receivedBy;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    //display fields 
    private String poNumber;
    private String supplierName;
    private String receivedByName;

    private List<GoodsReceiptDetail> details;

    public GoodsReceipt() {
        this.receiptDate = LocalDateTime.now();
        this.status = STATUS_PENDING;
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    public GoodsReceipt(String receiptNumber, Long poId, Integer receivedBy) {
        this();
        this.receiptNumber = receiptNumber;
        this.poId = poId;
        this.receivedBy = receivedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public PurchaseOrder getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
        if (purchaseOrder != null) {
            this.poId = purchaseOrder.getId();
        }
    }

    public Long getPoId() {
        return poId;
    }

    public void setPoId(Long poId) {
        this.poId = poId;
    }

    public LocalDateTime getReceiptDate() {
        return receiptDate;
    }

    public String getReceiptDateFormatted() {
        return DateUtil.format(receiptDate);
    }

    public void setReceiptDate(LocalDateTime receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(Integer receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getCompletedAtFormatted() {
        return DateUtil.format(completedAt);
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getReceivedByName() {
        return receivedByName;
    }

    public void setReceivedByName(String receivedByName) {
        this.receivedByName = receivedByName;
    }

    public List<GoodsReceiptDetail> getDetails() {
        return details;
    }

    public void setDetails(List<GoodsReceiptDetail> details) {
        this.details = details;
    }

    // Helper methods
    public void addDetail(GoodsReceiptDetail detail) {
        details.add(detail);
        detail.setGoodsReceipt(this);
    }

    public void removeDetail(GoodsReceiptDetail detail) {
        details.remove(detail);
        detail.setGoodsReceipt(null);
    }

    public void recalculateTotals() {
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
        for (GoodsReceiptDetail detail : details) {
            this.totalQuantity += detail.getQuantityReceived();
            this.totalAmount = this.totalAmount.add(detail.getLineTotal());
        }
    }

    public void complete() {
        this.status = STATUS_COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean canBeCompleted() {
        return STATUS_PENDING.equals(this.status) && !details.isEmpty();
    }

    @Override
    public String toString() {
        return "GoodsReceipt{"
                + "id=" + id
                + ", receiptNumber='" + receiptNumber + '\''
                + ", status='" + status + '\''
                + ", totalQuantity=" + totalQuantity
                + ", totalAmount=" + totalAmount
                + '}';
    }
}
