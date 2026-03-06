/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import util.DateUtil;

/**
 *
 * @author qp
 */
public class PurchaseOrder {

    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_PARTIAL_RECEIVED = "PARTIAL_RECEIVED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    private Long id;
    private String poNumber;
    private Integer supplierId;
    private LocalDate orderDate;
    private LocalDate expectedDate;
    private String status;

    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalAmount;

    private Integer approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    private Integer cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    private String notes;

    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for display
    private String supplierName;
    private String createdByName;
    private String approvedByName;
    private String cancelledByName;

    private List<PurchaseOrderItem> items;
    private List<GoodsReceipt> goodsReceipts;

    public PurchaseOrder() {
        this.status = STATUS_PENDING_APPROVAL;
        this.subtotal = BigDecimal.ZERO;
        this.totalDiscount = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.goodsReceipts = new ArrayList<>();
    }

    public PurchaseOrder(String poNumber, Integer supplierId, LocalDate orderDate, Integer createdBy) {
        this();
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getOrderDateFormatted() {
        return DateUtil.format(orderDate);
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }

    public String getExpectedDateFormatted() {
        return DateUtil.format(expectedDate);
    }

    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public String getApprovedAtFormatted() {
        return DateUtil.format(approvedAt);
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Integer cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancelledAtFormatted() {
        return DateUtil.format(cancelledAt);
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedAtFormatted() {
        return DateUtil.format(createdAt);
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedAtFormatted() {
        return DateUtil.format(updatedAt);
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }

    public String getCancelledByName() {
        return cancelledByName;
    }

    public void setCancelledByName(String cancelledByName) {
        this.cancelledByName = cancelledByName;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }

    public List<GoodsReceipt> getGoodsReceipts() {
        return goodsReceipts;
    }

    public void setGoodsReceipts(List<GoodsReceipt> goodsReceipts) {
        this.goodsReceipts = goodsReceipts;
    }

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    public void removeItem(PurchaseOrderItem item) {
        items.remove(item);
        item.setPurchaseOrder(null);
    }

    public void recalculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        this.totalDiscount = BigDecimal.ZERO;
        
        for (PurchaseOrderItem item : items) {
            // Calculate subtotal (before discount)
            BigDecimal lineSubtotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantityOrdered()));
            this.subtotal = this.subtotal.add(lineSubtotal);
            
            // Calculate line discount
            BigDecimal lineDiscount = BigDecimal.ZERO;
            if (PurchaseOrderItem.DISCOUNT_PERCENT.equals(item.getDiscountType())) {
                lineDiscount = lineSubtotal.multiply(item.getDiscountValue())
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_EVEN);
            } else {
                lineDiscount = item.getDiscountValue() != null ? item.getDiscountValue() : BigDecimal.ZERO;
            }
            this.totalDiscount = this.totalDiscount.add(lineDiscount);
        }
        
        this.totalAmount = this.subtotal.subtract(this.totalDiscount);
    }

    public boolean canBeApproved() {
        return STATUS_PENDING_APPROVAL.equals(this.status);
    }

    public boolean canBeCancelled() {
        return STATUS_APPROVED.equals(this.status);
    }

    public boolean canReceiveGoods() {
        return STATUS_APPROVED.equals(this.status) || STATUS_PARTIAL_RECEIVED.equals(this.status);
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", poNumber='" + poNumber + '\'' +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
