package com.example.myapplication.Profile;

import com.example.myapplication.SanPham;
import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
public class lsdh_order implements Serializable{
    private String orderId;
    private String userId;
    private String receiverName;
    private String address;
    private String phoneNumber;
    private String paymentMethod;
    private int totalAmount;
    private int depositAmount;
    private int remainingAmount;
    private List<SanPham> products;
    private String status; // Ví dụ: "Pending", "Processing", "Shipped", "Delivered", "Cancelled"
    @ServerTimestamp
    private Date createdAt;
    public lsdh_order(){
    }
    public lsdh_order(String orderId, String userId, String receiverName, String address, String phoneNumber,
                      String paymentMethod, int totalAmount, int depositAmount, int remainingAmount,
                      List<SanPham> products, String status){
        this.orderId = orderId;
        this.userId = userId;
        this.receiverName = receiverName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;
        this.remainingAmount = remainingAmount;
        this.products = products;
        this.status = status;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }
    public int getDepositAmount() { return depositAmount; }
    public void setDepositAmount(int depositAmount) { this.depositAmount = depositAmount; }
    public int getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(int remainingAmount) { this.remainingAmount = remainingAmount; }
    public List<SanPham> getProducts() { return products; }
    public void setProducts(List<SanPham> products) { this.products = products; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

}
