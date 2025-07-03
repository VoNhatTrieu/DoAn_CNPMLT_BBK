package com.example.myapplication.admin;

public class OrderModel {
    private String maDonHang;
    private String tenBanh;
    private String userId;
    private String nguoiNhan;
    private String sdt;
    private String loaiBanh;
    private String ngayGiao;
    private String moTa;
    private String status;
    private double giaBaoGia;
    private double chiPhiNguyenLieu;
    private String thongTinBaoGia;
    private String linkAnhMau;

    public OrderModel() {
        // Required for Firebase
    }

    public OrderModel(String maDonHang, String tenBanh, String nguoiNhan, String sdt,
                      String loaiBanh, String ngayGiao, String moTa, String status,
                      double giaBaoGia, double chiPhiNguyenLieu,
                      String thongTinBaoGia, String linkAnhMau) {
        this.maDonHang = maDonHang;
        this.tenBanh = tenBanh;
        this.nguoiNhan = nguoiNhan;
        this.sdt = sdt;
        this.loaiBanh = loaiBanh;
        this.ngayGiao = ngayGiao;
        this.moTa = moTa;
        this.status = status;
        this.giaBaoGia = giaBaoGia;
        this.chiPhiNguyenLieu = chiPhiNguyenLieu;
        this.thongTinBaoGia = thongTinBaoGia;
        this.linkAnhMau = linkAnhMau;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public String getTenBanh() {
        return tenBanh;
    }

    public void setTenBanh(String tenBanh) {
        this.tenBanh = tenBanh;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(String nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getLoaiBanh() {
        return loaiBanh;
    }

    public void setLoaiBanh(String loaiBanh) {
        this.loaiBanh = loaiBanh;
    }

    public String getNgayGiao() {
        return ngayGiao;
    }

    public void setNgayGiao(String ngayGiao) {
        this.ngayGiao = ngayGiao;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getGiaBaoGia() {
        return giaBaoGia;
    }

    public void setGiaBaoGia(double giaBaoGia) {
        this.giaBaoGia = giaBaoGia;
    }

    public double getChiPhiNguyenLieu() {
        return chiPhiNguyenLieu;
    }

    public void setChiPhiNguyenLieu(double chiPhiNguyenLieu) {
        this.chiPhiNguyenLieu = chiPhiNguyenLieu;
    }

    public String getThongTinBaoGia() {
        return thongTinBaoGia;
    }

    public void setThongTinBaoGia(String thongTinBaoGia) {
        this.thongTinBaoGia = thongTinBaoGia;
    }

    public String getLinkAnhMau() {
        return linkAnhMau;
    }

    public void setLinkAnhMau(String linkAnhMau) {
        this.linkAnhMau = linkAnhMau;
    }
}
