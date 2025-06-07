package com.example.myapplication;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String ten;
    private int gia;
    private int soLuong;
    private int anh; // Resource ID cho hình ảnh
    private String cateri; // Thêm trường danh mục

    public SanPham(String ten, int gia, int anh, String cateri) { // Cập nhật constructor
        this.ten = ten;
        this.gia = gia;
        this.soLuong = 1; // Mặc định số lượng là 1
        this.anh = anh;
        this.cateri = cateri;
    }

    public String getTen() {
        return ten;
    }

    public int getGia() {
        return gia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getAnh() {
        return anh;
    }

    public String getCateri() { // Thêm getter cho cateri
        return cateri;
    }

    public void setCateri(String cateri) {
        this.cateri = cateri;
    }
}