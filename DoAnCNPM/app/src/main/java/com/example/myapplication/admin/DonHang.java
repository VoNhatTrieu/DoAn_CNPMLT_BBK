package com.example.myapplication.admin;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonHang implements Parcelable {
    private String maDonHang;
    private String userId;
    private String tenKhachHang;
    private String soDienThoai;
    private String diaChi;
    private List<DHitem> danhSachSanPham;
    private double tongTien;
    private String trangThai;
    private Timestamp ngayTao;
    private Timestamp ngayCapNhat;
    private String ghiChu;
    private boolean laBanhTheoYeuCau;
    private String linkAnhMau;
    private double chiPhiNguyenLieu;
    private String thongTinBaoGia;
    private boolean daBaoGia;

    public DonHang() {}

    public DonHang(String maDonHang, String userId, String tenKhachHang, String soDienThoai,
                   String diaChi, List<DHitem> danhSachSanPham, double tongTien,
                   String trangThai, Timestamp ngayTao) {
        this.maDonHang = maDonHang;
        this.userId = userId;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.danhSachSanPham = danhSachSanPham;
        this.tongTien = tongTien;
        this.trangThai = trangThai != null ? trangThai : "cho_xu_ly";
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayTao;
        this.laBanhTheoYeuCau = false;
        this.daBaoGia = false;
    }

    // Constructor from OrderModel
    public DonHang(OrderModel model) {
        this.tenKhachHang = model.getNguoiNhan();
        this.soDienThoai = model.getSdt();
        this.trangThai = model.getStatus() != null ? model.getStatus() : "cho_xu_ly";
        this.linkAnhMau = model.getLinkAnhMau();
        this.laBanhTheoYeuCau = "Bánh theo yêu cầu".equalsIgnoreCase(model.getLoaiBanh());
        this.ghiChu = model.getMoTa();
        this.danhSachSanPham = new ArrayList<>();
        this.ngayTao = null;
        this.ngayCapNhat = null;
    }

    protected DonHang(Parcel in) {
        maDonHang = in.readString();
        userId = in.readString();
        tenKhachHang = in.readString();
        soDienThoai = in.readString();
        diaChi = in.readString();
        danhSachSanPham = new ArrayList<>();
        in.readList(danhSachSanPham, DHitem.class.getClassLoader());
        tongTien = in.readDouble();
        trangThai = in.readString();
        long ngayTaoTime = in.readLong();
        ngayTao = ngayTaoTime != -1 ? new Timestamp(new Date(ngayTaoTime)) : null;
        long ngayCapNhatTime = in.readLong();
        ngayCapNhat = ngayCapNhatTime != -1 ? new Timestamp(new Date(ngayCapNhatTime)) : null;
        ghiChu = in.readString();
        laBanhTheoYeuCau = in.readByte() != 0;
        linkAnhMau = in.readString();
        chiPhiNguyenLieu = in.readDouble();
        thongTinBaoGia = in.readString();
        daBaoGia = in.readByte() != 0;
    }

    public static final Creator<DonHang> CREATOR = new Creator<DonHang>() {
        @Override
        public DonHang createFromParcel(Parcel in) {
            return new DonHang(in);
        }

        @Override
        public DonHang[] newArray(int size) {
            return new DonHang[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maDonHang);
        dest.writeString(userId);
        dest.writeString(tenKhachHang);
        dest.writeString(soDienThoai);
        dest.writeString(diaChi);
        dest.writeList(danhSachSanPham);
        dest.writeDouble(tongTien);
        dest.writeString(trangThai);
        dest.writeLong(ngayTao != null ? ngayTao.toDate().getTime() : -1);
        dest.writeLong(ngayCapNhat != null ? ngayCapNhat.toDate().getTime() : -1);
        dest.writeString(ghiChu);
        dest.writeByte((byte) (laBanhTheoYeuCau ? 1 : 0));
        dest.writeString(linkAnhMau);
        dest.writeDouble(chiPhiNguyenLieu);
        dest.writeString(thongTinBaoGia);
        dest.writeByte((byte) (daBaoGia ? 1 : 0));
    }

    // ✅ Đã thêm kiểm tra null cho trangThai
    public String getTrangThaiText() {
        if (trangThai == null) return "Không rõ";
        switch (trangThai) {
            case "cho_xu_ly": return "Chờ xử lý";
            case "dang_lam": return "Đang làm";
            case "hoan_tat": return "Hoàn tất";
            case "huy": return "Đã hủy";
            case "theo_yeu_cau": return "Theo yêu cầu";
            default: return "Không rõ";
        }
    }

    public int getTrangThaiColor() {
        if (trangThai == null) return android.graphics.Color.BLACK;
        switch (trangThai) {
            case "cho_xu_ly": return android.graphics.Color.GRAY;
            case "dang_lam": return android.graphics.Color.BLUE;
            case "hoan_tat": return android.graphics.Color.GREEN;
            case "huy": return android.graphics.Color.RED;
            case "theo_yeu_cau": return android.graphics.Color.MAGENTA;
            default: return android.graphics.Color.BLACK;
        }
    }

    // Getter & Setter
    public String getMaDonHang() { return maDonHang; }
    public void setMaDonHang(String maDonHang) { this.maDonHang = maDonHang; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
    public List<DHitem> getDanhSachSanPham() { return danhSachSanPham; }
    public void setDanhSachSanPham(List<DHitem> danhSachSanPham) { this.danhSachSanPham = danhSachSanPham; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public Timestamp getNgayTao() { return ngayTao; }
    public void setNgayTao(Timestamp ngayTao) { this.ngayTao = ngayTao; }
    public Timestamp getNgayCapNhat() { return ngayCapNhat; }
    public void setNgayCapNhat(Timestamp ngayCapNhat) { this.ngayCapNhat = ngayCapNhat; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
    public boolean isLaBanhTheoYeuCau() { return laBanhTheoYeuCau; }
    public void setLaBanhTheoYeuCau(boolean laBanhTheoYeuCau) { this.laBanhTheoYeuCau = laBanhTheoYeuCau; }
    public String getLinkAnhMau() { return linkAnhMau; }
    public void setLinkAnhMau(String linkAnhMau) { this.linkAnhMau = linkAnhMau; }
    public double getChiPhiNguyenLieu() { return chiPhiNguyenLieu; }
    public void setChiPhiNguyenLieu(double chiPhiNguyenLieu) { this.chiPhiNguyenLieu = chiPhiNguyenLieu; }
    public String getThongTinBaoGia() { return thongTinBaoGia; }
    public void setThongTinBaoGia(String thongTinBaoGia) { this.thongTinBaoGia = thongTinBaoGia; }
    public boolean isDaBaoGia() { return daBaoGia; }
    public void setDaBaoGia(boolean daBaoGia) { this.daBaoGia = daBaoGia; }
}
