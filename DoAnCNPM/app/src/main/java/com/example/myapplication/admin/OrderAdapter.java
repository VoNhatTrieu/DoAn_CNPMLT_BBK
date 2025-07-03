package com.example.myapplication.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<DonHang> orderList;
    private final List<DonHang> orderListFull;
    private OnOrderClickListener clickListener;

    public OrderAdapter(List<DonHang> orderList) {
        this.orderList = orderList;
        this.orderListFull = new ArrayList<>(orderList);
    }

    public interface OnOrderClickListener {
        void onOrderClick(DonHang order, int position);
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_admin, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        DonHang order = orderList.get(position);
        bindOrderData(holder, order);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onOrderClick(order, position);
            }
        });
    }

    private void bindOrderData(OrderViewHolder holder, DonHang order) {
        holder.tvMaDonHang.setText("Mã: " + (order.getMaDonHang() != null ? order.getMaDonHang() : "N/A"));
        holder.tvTenKhachHang.setText("KH: " + (order.getTenKhachHang() != null ? order.getTenKhachHang() : "Chưa có tên"));
        holder.tvSoDienThoai.setText("SĐT: " + (order.getSoDienThoai() != null ? order.getSoDienThoai() : "Chưa có SĐT"));
        holder.tvDiaChi.setText("Địa chỉ: " + (order.getDiaChi() != null ? order.getDiaChi() : "Chưa có địa chỉ"));
        holder.tvTongTien.setText(String.format(Locale.getDefault(), "Tổng: %,.0f đ", order.getTongTien()));
        holder.tvNgayTao.setText("Ngày đặt: " + formatTimestamp(order.getNgayTao()));
        holder.tvTrangThai.setText(order.getTrangThaiText());
        holder.tvTrangThai.setTextColor(order.getTrangThaiColor());
        holder.tvLoaiBanh.setText(order.isLaBanhTheoYeuCau() ? "Bánh theo yêu cầu" : "Bánh thường");
        holder.tvSoLuongSanPham.setText("Sản phẩm: " + (order.getDanhSachSanPham() != null ? order.getDanhSachSanPham().size() : 0) + " loại");

        loadOrderImage(holder, order);

        // Ẩn/hiện nhóm layout báo giá theo loại bánh
        if (order.isLaBanhTheoYeuCau()) {
            holder.layoutBaoGia.setVisibility(View.VISIBLE);
            String baoGiaText = order.isDaBaoGia() ? "Đã báo giá" : "Chưa báo giá";
            holder.tvBaoGia.setText(baoGiaText);
            holder.tvBaoGia.setTextColor(order.isDaBaoGia() ?
                    holder.itemView.getContext().getColor(android.R.color.holo_green_dark) :
                    holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));

            if (order.getGhiChu() != null && !order.getGhiChu().trim().isEmpty()) {
                holder.tvGhiChu.setText("Ghi chú: " + order.getGhiChu());
            } else {
                holder.tvGhiChu.setText("Ghi chú: Không có");
            }
        } else {
            holder.layoutBaoGia.setVisibility(View.GONE);
        }
    }

    private void loadOrderImage(OrderViewHolder holder, DonHang order) {
        if (order.isLaBanhTheoYeuCau() && order.getLinkAnhMau() != null && !order.getLinkAnhMau().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(order.getLinkAnhMau())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(holder.imgOrder);
            holder.imgOrder.setVisibility(View.VISIBLE);
        } else {
            // Ẩn ImageView nếu không phải bánh theo yêu cầu hoặc không có ảnh mẫu
            holder.imgOrder.setVisibility(View.GONE);
        }
    }

    private String formatTimestamp(com.google.firebase.Timestamp timestamp) {
        if (timestamp == null) return "Chưa xác định";
        try {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            return "Lỗi định dạng";
        }
    }

    public void filterByStatus(String status) {
        orderList.clear();
        if (status == null || status.equals("Tất cả")) {
            orderList.addAll(orderListFull);
        } else {
            for (DonHang order : orderListFull) {
                if (order.getTrangThaiText().equals(status)) {
                    orderList.add(order);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<DonHang> newOrderList) {
        orderList.clear();
        orderListFull.clear();
        if (newOrderList != null) {
            orderList.addAll(newOrderList);
            orderListFull.addAll(newOrderList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaDonHang, tvTenKhachHang, tvSoDienThoai, tvDiaChi, tvTongTien;
        TextView tvNgayTao, tvTrangThai, tvLoaiBanh, tvSoLuongSanPham;
        TextView tvGhiChu, tvBaoGia;
        ImageView imgOrder;
        LinearLayout layoutBaoGia;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOrder = itemView.findViewById(R.id.img_order);
            tvMaDonHang = itemView.findViewById(R.id.tv_ma_don_hang);
            tvTenKhachHang = itemView.findViewById(R.id.tv_ten_khach_hang);
            tvSoDienThoai = itemView.findViewById(R.id.tv_so_dien_thoai);
            tvDiaChi = itemView.findViewById(R.id.tv_dia_chi);
            tvTongTien = itemView.findViewById(R.id.tv_tong_tien);
            tvNgayTao = itemView.findViewById(R.id.tv_ngay_tao);
            tvTrangThai = itemView.findViewById(R.id.tv_trang_thai);
            tvLoaiBanh = itemView.findViewById(R.id.tv_loai_banh);
            tvSoLuongSanPham = itemView.findViewById(R.id.tv_so_luong_san_pham);
            tvGhiChu = itemView.findViewById(R.id.tv_ghi_chu);
            tvBaoGia = itemView.findViewById(R.id.tv_bao_gia);
            layoutBaoGia = itemView.findViewById(R.id.layout_bao_gia);
        }
    }
}