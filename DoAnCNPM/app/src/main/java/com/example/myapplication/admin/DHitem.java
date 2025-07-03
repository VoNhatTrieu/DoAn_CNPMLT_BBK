package com.example.myapplication.admin;


import android.os.Parcel;
import android.os.Parcelable;

public class DHitem implements Parcelable {
    private String tenSanPham;
    private int soLuong;
    private double donGia;
    private String hinhAnh;

    public DHitem() {}

    public DHitem(String tenSanPham, int soLuong, double donGia, String hinhAnh) {
        this.tenSanPham = tenSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.hinhAnh = hinhAnh;
    }

    // Constructor từ Parcel
    protected DHitem(Parcel in) {
        tenSanPham = in.readString();
        soLuong = in.readInt();
        donGia = in.readDouble();
        hinhAnh = in.readString();
    }

    public static final Creator<DHitem> CREATOR = new Creator<DHitem>() {
        @Override
        public DHitem createFromParcel(Parcel in) {
            return new DHitem(in);
        }

        @Override
        public DHitem[] newArray(int size) {
            return new DHitem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tenSanPham);
        dest.writeInt(soLuong);
        dest.writeDouble(donGia);
        dest.writeString(hinhAnh);
    }

    // Getters và Setters (giữ nguyên)
    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}