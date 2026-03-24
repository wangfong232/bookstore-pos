/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComboProduct {

    private int comboID;
    private int productID;
    private int comboQuantity;
    private boolean isActive;
    private Date createdDate;
    private Date updatedDate;

    // Display fields (loaded from joined queries)
    private String productName;
    private String productSku;
    private double sellingPrice;
    private String imageURL;

    private List<ComboProductItem> comboItems;

    public ComboProduct() {
        this.comboQuantity = 0;
        this.isActive = true;
        this.comboItems = new ArrayList<>();
    }

    public int getComboID() {
        return comboID;
    }

    public void setComboID(int comboID) {
        this.comboID = comboID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getComboQuantity() {
        return comboQuantity;
    }

    public void setComboQuantity(int comboQuantity) {
        this.comboQuantity = comboQuantity;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<ComboProductItem> getComboItems() {
        return comboItems;
    }

    public void setComboItems(List<ComboProductItem> comboItems) {
        this.comboItems = comboItems;
    }

    public void addItem(ComboProductItem item) {
        this.comboItems.add(item);
    }

    @Override
    public String toString() {
        return "ComboProduct{" +
                "comboID=" + comboID +
                ", productID=" + productID +
                ", productName='" + productName + '\'' +
                ", comboQuantity=" + comboQuantity +
                ", isActive=" + isActive +
                '}';
    }
}

