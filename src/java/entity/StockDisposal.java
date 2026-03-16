/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qp
 */
public class StockDisposal {
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    
    public static final String REASON_DAMAGED = "DAMAGED";
    public static final String REASON_EXPIRED = "EXPIRED";
    public static final String REASON_DEFECTIVE = "DEFECTIVE";
    public static final String REASON_OTHER = "OTHER";
    
    private Long id;
    private String disposalNumber;
    private LocalDateTime disposalDate;
    private String disposalReason;
    private String status;
    private Integer totalQuantity;
    private BigDecimal totalValue;
    
    private Integer approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    
    private Boolean physicalDisposalConfirmed;
    private Integer disposedBy;
    private LocalDateTime disposedAt;
    
    private String notes;
    
    private Integer createdBy;
    private LocalDateTime createdAt;
    
    private List<StockDisposalDetail> details;
    
    private String createdByName;
    private String approvedByName;
    private String disposedByName;
    
     public StockDisposal() {
        this.disposalDate = LocalDateTime.now();
        this.status = STATUS_PENDING_APPROVAL;
        this.totalQuantity = 0;
        this.totalValue = BigDecimal.ZERO;
        this.physicalDisposalConfirmed = false;
        this.createdAt = LocalDateTime.now();
        this.details = new ArrayList<>();
    }
     
      public StockDisposal(String disposalNumber, String disposalReason, Integer createdBy) {
        this();
        this.disposalNumber = disposalNumber;
        this.disposalReason = disposalReason;
        this.createdBy = createdBy;
    }
      
      public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisposalNumber() {
        return disposalNumber;
    }

    public void setDisposalNumber(String disposalNumber) {
        this.disposalNumber = disposalNumber;
    }

    public LocalDateTime getDisposalDate() {
        return disposalDate;
    }

    public void setDisposalDate(LocalDateTime disposalDate) {
        this.disposalDate = disposalDate;
    }

    public String getDisposalReason() {
        return disposalReason;
    }

    public void setDisposalReason(String disposalReason) {
        this.disposalReason = disposalReason;
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

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
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

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Boolean getPhysicalDisposalConfirmed() {
        return physicalDisposalConfirmed;
    }

    public void setPhysicalDisposalConfirmed(Boolean physicalDisposalConfirmed) {
        this.physicalDisposalConfirmed = physicalDisposalConfirmed;
    }

    public Integer getDisposedBy() {
        return disposedBy;
    }

    public void setDisposedBy(Integer disposedBy) {
        this.disposedBy = disposedBy;
    }

    public LocalDateTime getDisposedAt() {
        return disposedAt;
    }

    public void setDisposedAt(LocalDateTime disposedAt) {
        this.disposedAt = disposedAt;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<StockDisposalDetail> getDetails() {
        return details;
    }

    public void setDetails(List<StockDisposalDetail> details) {
        this.details = details;
    }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public String getDisposedByName() { return disposedByName; }
    public void setDisposedByName(String disposedByName) { this.disposedByName = disposedByName; }

    public void addDetail(StockDisposalDetail detail){
        details.add(detail);
        detail.setStockDisposal(this);
    }
    
    public void removeDetail(StockDisposalDetail detail){
    details.remove(detail);
    detail.setStockDisposal(null);
    }
    
    public void recalculateTotals(){
        this.totalQuantity = 0;
        this.totalValue = BigDecimal.ZERO;
        for (StockDisposalDetail detail : details) {
            this.totalQuantity += detail.getQuantity();
            this.totalValue = this.totalValue.add(detail.getLineTotal());
        }
    }
    
     public void approve(Integer approverId) {
        this.status = STATUS_APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(Integer approverId, String reason) {
        this.status = STATUS_REJECTED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    public void confirmPhysicalDisposal(Integer disposerId){
        if(STATUS_APPROVED.equals(this.status)){
            this.status= STATUS_COMPLETED;
            this.physicalDisposalConfirmed = true;
            this.disposedBy = disposerId;
            this.disposedAt = LocalDateTime.now();
        }
    }
    public boolean canBeApproved() {
        return STATUS_PENDING_APPROVAL.equals(this.status);
    }

    public boolean canBePhysicallyDisposed() {
        return STATUS_APPROVED.equals(this.status) && !physicalDisposalConfirmed;
    }

    @Override
    public String toString() {
        return "StockDisposal{" +
                "id=" + id +
                ", disposalNumber='" + disposalNumber + '\'' +
                ", status='" + status + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", totalValue=" + totalValue +
                '}';
    }
}
