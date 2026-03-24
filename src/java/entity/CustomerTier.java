package entity;

import java.lang.*;
import java.util.*;
import java.io.*;

/*
*
*
 */
public class CustomerTier {

    private int tierID;
    private String tierName;
    private double minPoint;
    private double discountRate;

    public CustomerTier() {
    }

    public CustomerTier(int tierID, String tierName, double minPoint, double discountRate) {
        this.tierID = tierID;
        this.tierName = tierName;
        this.minPoint = minPoint;
        this.discountRate = discountRate;
    }

    public int getTierID() {
        return tierID;
    }

    public void setTierID(int tierID) {
        this.tierID = tierID;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public double getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(double minPoint) {
        this.minPoint = minPoint;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

}
