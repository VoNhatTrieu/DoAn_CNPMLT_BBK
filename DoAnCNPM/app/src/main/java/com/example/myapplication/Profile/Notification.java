package com.example.myapplication.Profile;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Notification {
    private String id;
    private String userId;
    private String orderId;
    private String title;
    private String message;
    private boolean read;
    private Timestamp timestamp;
    private String type; // "quote", "order_update", "general"

    public Notification() {
        // Required empty constructor for Firestore
    }

    public Notification(String userId, String orderId, String title, String message, String type) {
        this.userId = userId;
        this.orderId = orderId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}