package com.example.myapplication.Chat;

public class Message {
    private String messageId;
    private String senderId;
    private String senderName;
    private String senderType; // "user" hoặc "admin"
    private String content;
    private long timestamp;
    private boolean isRead;
    public Message() {
        // Constructor rỗng cho Firebase
    }

    public Message(String senderId, String senderName, String senderType, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderType = senderType;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters và Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}