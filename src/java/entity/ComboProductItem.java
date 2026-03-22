/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

public class ComboProductItem {

    private int comboItemID;
    private int comboID;
    private int childProductID;
    private int quantity;

    // Display fields (loaded from joined queries)
    private String childProductName;
    private String childProductSku;
    private int childProductStock;

    public ComboProductItem() {
        this.quantity = 1;
    }

    public ComboProductItem(int childProductID, int quantity) {
        this.childProductID = childProductID;
        this.quantity = quantity;
    }

    public int getComboItemID() {
        return comboItemID;
    }

    public void setComboItemID(int comboItemID) {
        this.comboItemID = comboItemID;
    }

    public int getComboID() {
        return comboID;
    }

    public void setComboID(int comboID) {
        this.comboID = comboID;
    }

    public int getChildProductID() {
        return childProductID;
    }

    public void setChildProductID(int childProductID) {
        this.childProductID = childProductID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getChildProductName() {
        return childProductName;
    }

    public void setChildProductName(String childProductName) {
        this.childProductName = childProductName;
    }

    public String getChildProductSku() {
        return childProductSku;
    }

    public void setChildProductSku(String childProductSku) {
        this.childProductSku = childProductSku;
    }

    public int getChildProductStock() {
        return childProductStock;
    }

    public void setChildProductStock(int childProductStock) {
        this.childProductStock = childProductStock;
    }

    @Override
    public String toString() {
        return "ComboProductItem{" +
                "comboItemID=" + comboItemID +
                ", childProductID=" + childProductID +
                ", childProductName='" + childProductName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
