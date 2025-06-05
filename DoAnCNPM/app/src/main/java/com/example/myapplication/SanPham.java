package com.example.myapplication;

import java.io.Serializable;

public class SanPham implements Serializable {
    private String ten;
    private int gia;
    private int anh;
    private String categori;

    public SanPham(String ten, int gia, int anh, String categori) {
        this.ten = ten;
        this.gia = gia;
        this.anh = anh;
        this.categori = categori;
    }

    public String getTen() { return ten; }
    public int getGia() { return gia; }
    public int getAnh() { return anh; }
    public String getCateri() { return categori; }
}