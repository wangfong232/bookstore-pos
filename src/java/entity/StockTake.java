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

/**
 *
 * @author qp
 */
public class StockTake {

    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_PENDING_APPROVAL = "PENDING_APPROVAL";
    public static final String STATUS_COMPLETED = "COMPLETED";

    public static final String SCOPE_ALL = "ALL";
    public static final String SCOPE_CATEGORY = "CATEGORY";

    private Long id;
    private String stockTakeNumber;
    private String scopeType;
    private String scopeValue;
    private LocalDate stockTakeDate;
    private String status;

    private Integer totalItems;
    private Integer totalVarianceQty;
    private BigDecimal totalVarianceValue;

    private Integer approvedBy;
    private LocalDateTime approvedAt;

    private Integer recountRequestedBy;
    private LocalDateTime recountRequestedAt;
    private String recountReason;

    private String notes;

    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

    private String createdByName;
    private String approvedByName;
    private String recountByName;

    private List<StockTakeDetail> details;

    public StockTake() {
        this.scopeType = SCOPE_ALL;
        this.status = STATUS_IN_PROGRESS;
        this.totalItems = 0;
        this.totalVarianceQty = 0;
        this.totalVarianceValue = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
        this.details = new ArrayList<>();
    }

    public StockTake(String stockTakeNumber, LocalDate stockTakeDate, Integer createdBy) {
        this();
        this.stockTakeNumber = stockTakeNumber;
        this.stockTakeDate = stockTakeDate;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStockTakeNumber() {
        return stockTakeNumber;
    }

    public void setStockTakeNumber(String stockTakeNumber) {
        this.stockTakeNumber = stockTakeNumber;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(String scopeValue) {
        this.scopeValue = scopeValue;
    }

    public LocalDate getStockTakeDate() {
        return stockTakeDate;
    }

    public void setStockTakeDate(LocalDate stockTakeDate) {
        this.stockTakeDate = stockTakeDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalVarianceQty() {
        return totalVarianceQty;
    }

    public void setTotalVarianceQty(Integer totalVarianceQty) {
        this.totalVarianceQty = totalVarianceQty;
    }

    public BigDecimal getTotalVarianceValue() {
        return totalVarianceValue;
    }

    public void setTotalVarianceValue(BigDecimal totalVarianceValue) {
        this.totalVarianceValue = totalVarianceValue;
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

    public Integer getRecountRequestedBy() {
        return recountRequestedBy;
    }

    public void setRecountRequestedBy(Integer recountRequestedBy) {
        this.recountRequestedBy = recountRequestedBy;
    }

    public LocalDateTime getRecountRequestedAt() {
        return recountRequestedAt;
    }

    public void setRecountRequestedAt(LocalDateTime recountRequestedAt) {
        this.recountRequestedAt = recountRequestedAt;
    }

    public String getRecountReason() {
        return recountReason;
    }

    public void setRecountReason(String recountReason) {
        this.recountReason = recountReason;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public List<StockTakeDetail> getDetails() {
        return details;
    }

    public void setDetails(List<StockTakeDetail> details) {
        this.details = details;
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

    public String getRecountByName() {
        return recountByName;
    }

    public void setRecountByName(String recountByName) {
        this.recountByName = recountByName;
    }

    public void addDetail(StockTakeDetail detail) {
        details.add(detail);
        detail.setStockTake(this);
    }

    public void removeDetail(StockTakeDetail detail) {
        details.remove(detail);
        detail.setStockTake(null);
    }

    public void recalculateSummary() {
        this.totalItems = details.size();
        this.totalVarianceQty = 0;
        this.totalVarianceValue = BigDecimal.ZERO;

        for (StockTakeDetail detail : details) {
            this.totalVarianceQty += detail.getVarianceQuantity();
            this.totalVarianceValue = this.totalVarianceValue.add(detail.getVarianceValue());
        }
    }

    public void submitForApproval() {
        if (STATUS_IN_PROGRESS.equals(this.status)) {
            this.status = STATUS_PENDING_APPROVAL;
            this.submittedAt = LocalDateTime.now();
        }
    }

    public void approve(Integer approverId) {
        this.status = STATUS_COMPLETED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
    }

    public void requestRecount(Integer requesterId, String reason) {
        this.status = STATUS_IN_PROGRESS;
        this.recountRequestedBy = requesterId;
        this.recountReason = reason;
        this.recountRequestedAt = LocalDateTime.now();
    }

    public boolean canBeSubmitted() {
        return STATUS_IN_PROGRESS.equals(this.status);
    }

    public boolean hasVariance() {
        return this.totalVarianceQty != 0;
    }

    @Override
    public String toString() {
        return "StockTake{"
                + "id=" + id
                + ", stockTakeNumber='" + stockTakeNumber + '\''
                + ", status='" + status + '\''
                + ", totalVarianceQty=" + totalVarianceQty
                + '}';
    }
}
