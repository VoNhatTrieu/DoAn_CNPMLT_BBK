package com.example.myapplication;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String ten;
    private int gia;
    private int soLuong;
    private int anh; // Resource ID cho hình ảnh
    private String cateri;

    // ✅ Constructor rỗng - BẮT BUỘC CHO FIRESTORE
    public SanPham() {
    }

    // Constructor có tham số
    public SanPham(String ten, int gia, int anh, String cateri) {
        this.ten = ten;
        this.gia = gia;
        this.soLuong = 1;
        this.anh = anh;
        this.cateri = cateri;
    }

    // Getter và Setter
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public int getGia() { return gia; }
    public void setGia(int gia) { this.gia = gia; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public int getAnh() { return anh; }
    public void setAnh(int anh) { this.anh = anh; }

    public String getCateri() { return cateri; }
    public void setCateri(String cateri) { this.cateri = cateri; }
}
