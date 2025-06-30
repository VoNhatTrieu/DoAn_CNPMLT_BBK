package com.example.myapplication;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String ten;
    private int gia;
    private int anh;
    private String cateri;
    private String imageUrl;
    private String mota; // ✅ Thêm trường mô tả
    private int soLuong; // ✅ Thêm trường số lượng
    private long timestamp;

    // Constructor mặc định (cần cho Firestore)
    public SanPham() {}

    // Constructor cơ bản
    public SanPham(String ten, int gia, int anh, String cateri) {
        this.ten = ten;
        this.gia = gia;
        this.anh = anh;
        this.cateri = cateri;
        this.timestamp = System.currentTimeMillis();
    }

    // Constructor đầy đủ
    public SanPham(String ten, int gia, int anh, String cateri, String imageUrl, String mota, int soLuong) {
        this.ten = ten;
        this.gia = gia;
        this.anh = anh;
        this.cateri = cateri;
        this.imageUrl = imageUrl;
        this.mota = mota;
        this.soLuong = soLuong;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters và Setters
    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getGia() {
        return gia;
    }

    public void setGia(int gia) {
        this.gia = gia;
    }

    public int getAnh() {
        return anh;
    }

    public void setAnh(int anh) {
        this.anh = anh;
    }

    public String getCateri() {
        return cateri;
    }

    public void setCateri(String cateri) {
        this.cateri = cateri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}