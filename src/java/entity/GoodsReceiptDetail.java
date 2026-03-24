/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author qp
 */
public class GoodsReceiptDetail {

    private Long id;
    private GoodsReceipt goodsReceipt;
    private Long poLineItemId;
    private Integer productId;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;

    //snapshot
    private Integer oldQty;
    private BigDecimal oldCost;
    private BigDecimal newAvgCost;

    private String notes;

    // display fields 
    private String productName;
    private Integer quantityOrdered;

    public GoodsReceiptDetail() {
        this.oldQty = 0;
        this.oldCost = BigDecimal.ZERO;
    }

    public GoodsReceiptDetail(Long poLineItemId, Integer productId, Integer quantityReceived, BigDecimal unitCost) {
        this();
        this.poLineItemId = poLineItemId;
        this.productId = productId;
        this.quantityReceived = quantityReceived;
        this.unitCost = unitCost;
        calculateLineTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GoodsReceipt getGoodsReceipt() {
        return goodsReceipt;
    }

    public void setGoodsReceipt(GoodsReceipt goodsReceipt) {
        this.goodsReceipt = goodsReceipt;
    }

    public Long getPoLineItemId() {
        return poLineItemId;
    }

    public void setPoLineItemId(Long poLineItemId) {
        this.poLineItemId = poLineItemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(Integer quantityReceived) {
        this.quantityReceived = quantityReceived;
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

    public Integer getOldQty() {
        return oldQty;
    }

    public void setOldQty(Integer oldQty) {
        this.oldQty = oldQty;
    }

    public BigDecimal getOldCost() {
        return oldCost;
    }

    public void setOldCost(BigDecimal oldCost) {
        this.oldCost = oldCost;
    }

    public BigDecimal getNewAvgCost() {
        return newAvgCost;
    }

    public void setNewAvgCost(BigDecimal newAvgCost) {
        this.newAvgCost = newAvgCost;
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

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public void calculateLineTotal() {
        this.lineTotal = unitCost.multiply(BigDecimal.valueOf(quantityReceived));
    }

    //binh quan gia quyen
    //CONG THUC: NewAvgCost =(OldQty * OldCost + NewQty * NewCost) / (OldQty + NewQty)
    public void calculateNewAvgCost() {
        int totalQty = oldQty + quantityReceived;
        if (totalQty == 0) {
            this.newAvgCost = unitCost;
            return;
        }

        BigDecimal oldTotal = oldCost.multiply(BigDecimal.valueOf(oldQty));
        BigDecimal newTotal = unitCost.multiply(BigDecimal.valueOf(quantityReceived));
        this.newAvgCost = oldTotal.add(newTotal).divide(BigDecimal.valueOf(totalQty), 2, RoundingMode.HALF_EVEN);
    }

    //set MAC snapshot from current product state
    public void setMACSnapshot(int currentStock, BigDecimal currentCost) {
        this.oldQty = currentStock;
        this.oldCost = currentCost != null ? currentCost : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "GoodsReceiptDetail{"
                + "id=" + id
                + ", productId=" + productId
                + ", quantityReceived=" + quantityReceived
                + ", unitCost=" + unitCost
                + ", newAvgCost=" + newAvgCost
                + '}';
    }
}
