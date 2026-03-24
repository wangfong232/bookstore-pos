package entity;

import java.lang.*;
import java.util.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
*
*
*/
public class Customer {

    private String customerID;
    private String fullName;
    private String email;
    private LocalDate birthday;
    private LocalDateTime registerDate;
    private String status;
    private String note;
    private int points;
    private String tierName;
    private String phoneNumber;

    public Customer() {
    }

    public Customer(String customerID, String fullName, String email, LocalDate birthday,
            LocalDateTime registerDate, String status, String note) {
        this.customerID = customerID;
        this.fullName = fullName;
        this.email = email;
        this.birthday = birthday;
        this.registerDate = registerDate;
        this.status = status;
        this.note = note;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return fullName;
    }

    public void setCustomerName(String customerName) {
        this.fullName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
