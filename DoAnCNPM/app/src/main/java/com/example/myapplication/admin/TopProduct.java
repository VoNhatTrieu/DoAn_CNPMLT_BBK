package com.example.myapplication.admin;

public class TopProduct {
    private String ten;
    private int soLuong;
    private double doanhThu;
    private String imageUrl;

    public TopProduct() {
        // Constructor mặc định cho Firebase
    }

    public TopProduct(String ten, int soLuong, double doanhThu, String imageUrl) {
        this.ten = ten;
        this.soLuong = soLuong;
        this.doanhThu = doanhThu;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getTen() {
        return ten;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDoanhThu() {
        return doanhThu;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public void setDoanhThu(double doanhThu) {
        this.doanhThu = doanhThu;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
