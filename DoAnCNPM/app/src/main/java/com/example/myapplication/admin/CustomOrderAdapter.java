package com.example.myapplication.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CustomOrderAdapter extends RecyclerView.Adapter<CustomOrderAdapter.CustomOrderViewHolder> {
    private List<OrderModel> orderList;
    private Context context;
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onOrderClick(OrderModel order, int position);
        void onQuoteUpdated();
    }

    public CustomOrderAdapter(List<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_order_admin, parent, false);
        return new CustomOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomOrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        bindOrderData(holder, order);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order, position);
            }
        });

        holder.btnBaoGia.setOnClickListener(v -> showQuoteDialog(order, position));
    }

    private void bindOrderData(CustomOrderViewHolder holder, OrderModel order) {
        holder.tvTenBanh.setText("Tên bánh: " + safe(order.getTenBanh()));
        holder.tvNguoiNhan.setText("Người nhận: " + safe(order.getNguoiNhan()));
        holder.tvSdt.setText("SĐT: " + safe(order.getSdt()));
        holder.tvLoaiBanh.setText("Loại: " + safe(order.getLoaiBanh()));
        holder.tvNgayGiao.setText("Ngày giao: " + safe(order.getNgayGiao()));
        holder.tvMoTa.setText("Mô tả: " + safe(order.getMoTa()));

        String status = order.getStatus() != null ? order.getStatus() : "Chờ xác nhận";
        holder.tvTrangThai.setText("Trạng thái: " + status);
        holder.tvTrangThai.setTextColor(getStatusColor(status));

        if (order.getGiaBaoGia() > 0) {
            holder.layoutBaoGia.setVisibility(View.VISIBLE);
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            holder.tvGiaBaoGia.setText("Giá báo: " + formatter.format(order.getGiaBaoGia()) + " đ");
            holder.tvChiPhiNguyenLieu.setText("Chi phí nguyên liệu: " + formatter.format(order.getChiPhiNguyenLieu()) + " đ");
            holder.tvThongTinBaoGia.setText("Thông tin: " + safe(order.getThongTinBaoGia()));
            holder.btnBaoGia.setText("Cập nhật báo giá");
        } else {
            holder.layoutBaoGia.setVisibility(View.GONE);
            holder.btnBaoGia.setText("Báo giá");
        }

        loadOrderImage(holder, order);
    }

    private String safe(String text) {
        return (text != null && !text.isEmpty()) ? text : "Chưa có";
    }

    private void loadOrderImage(CustomOrderViewHolder holder, OrderModel order) {
        String imageUrl = order.getLinkAnhMau();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Log.d("IMAGE_DEBUG", "Loading image: " + imageUrl);
            holder.imgAnhMau.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.image_placeholder)
                    .into(holder.imgAnhMau);
        } else {
            Log.d("IMAGE_DEBUG", "No image URL for order");
            holder.imgAnhMau.setVisibility(View.GONE);
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Chờ xác nhận":
            case "Chờ báo giá":
                return context.getColor(android.R.color.holo_orange_dark);
            case "Đã báo giá":
                return context.getColor(android.R.color.holo_blue_dark);
            case "Đang làm":
                return context.getColor(android.R.color.holo_green_dark);
            case "Hoàn tất":
                return context.getColor(android.R.color.holo_green_light);
            case "Đã hủy":
                return context.getColor(android.R.color.holo_red_dark);
            default:
                return context.getColor(android.R.color.black);
        }
    }

    private void showQuoteDialog(OrderModel order, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bao_gia, null);

        EditText etChiPhiNguyenLieu = dialogView.findViewById(R.id.et_chi_phi_nguyen_lieu);
        EditText etGiaBaoGia = dialogView.findViewById(R.id.et_gia_bao_gia);
        EditText etThongTinBaoGia = dialogView.findViewById(R.id.et_thong_tin_bao_gia);

        if (order.getChiPhiNguyenLieu() > 0) {
            etChiPhiNguyenLieu.setText(String.valueOf(order.getChiPhiNguyenLieu()));
        }
        if (order.getGiaBaoGia() > 0) {
            etGiaBaoGia.setText(String.valueOf(order.getGiaBaoGia()));
        }
        if (order.getThongTinBaoGia() != null) {
            etThongTinBaoGia.setText(order.getThongTinBaoGia());
        }

        builder.setView(dialogView)
                .setTitle("Báo giá đơn hàng")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String chiPhiStr = etChiPhiNguyenLieu.getText().toString().trim();
                    String giaStr = etGiaBaoGia.getText().toString().trim();
                    String thongTin = etThongTinBaoGia.getText().toString().trim();

                    if (TextUtils.isEmpty(chiPhiStr) || TextUtils.isEmpty(giaStr)) {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double chiPhiNguyenLieu = Double.parseDouble(chiPhiStr);
                        double giaBaoGia = Double.parseDouble(giaStr);
                        updateQuoteInFirestore(order, chiPhiNguyenLieu, giaBaoGia, thongTin, position);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateQuoteInFirestore(OrderModel order, double chiPhiNguyenLieu,
                                        double giaBaoGia, String thongTin, int position) {
        String id = order.getMaDonHang();
        if (id == null || id.isEmpty()) {
            Toast.makeText(context, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("custom_orders").document(id);

        Map<String, Object> updates = new HashMap<>();
        updates.put("chiPhiNguyenLieu", chiPhiNguyenLieu);
        updates.put("giaBaoGia", giaBaoGia);
        updates.put("thongTinBaoGia", thongTin);
        updates.put("status", "Đã báo giá");

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    sendNotificationToCustomer(order, giaBaoGia, thongTin);

                    order.setChiPhiNguyenLieu(chiPhiNguyenLieu);
                    order.setGiaBaoGia(giaBaoGia);
                    order.setThongTinBaoGia(thongTin);
                    order.setStatus("Đã báo giá");

                    notifyItemChanged(position);
                    if (listener != null) {
                        listener.onQuoteUpdated();
                    }
                    Toast.makeText(context, "Cập nhật báo giá thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToCustomer(OrderModel order, double giaBaoGia, String thongTin) {
        String userId = order.getUserId();

        if (userId == null || userId.isEmpty()) {
            Log.e("NOTIFICATION", "❌ Không có userId trong đơn hàng. Không gửi được thông báo.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> notification = new HashMap<>();

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String giaBaoStr = formatter.format(giaBaoGia) + " VNĐ";

        String title = "Báo giá đơn hàng #" + order.getMaDonHang();
        String message = "Giá báo: " + giaBaoStr;
        if (thongTin != null && !thongTin.isEmpty()) {
            message += "\n" + thongTin;
        }

        notification.put("userId", userId);
        notification.put("orderId", order.getMaDonHang());
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", FieldValue.serverTimestamp());
        notification.put("read", false);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(docRef ->
                        Log.d("NOTIFICATION", "✅ Thông báo đã được gửi đến userId: " + userId))
                .addOnFailureListener(e ->
                        Log.e("NOTIFICATION", "❌ Gửi thông báo thất bại: " + e.getMessage()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class CustomOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBanh, tvNguoiNhan, tvSdt, tvLoaiBanh, tvNgayGiao;
        TextView tvMoTa, tvTrangThai, tvGiaBaoGia, tvChiPhiNguyenLieu, tvThongTinBaoGia;
        ImageView imgAnhMau;
        Button btnBaoGia;
        LinearLayout layoutBaoGia;

        public CustomOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBanh = itemView.findViewById(R.id.tv_ten_banh);
            tvNguoiNhan = itemView.findViewById(R.id.tv_nguoi_nhan);
            tvSdt = itemView.findViewById(R.id.tv_sdt);
            tvLoaiBanh = itemView.findViewById(R.id.tv_loai_banh);
            tvNgayGiao = itemView.findViewById(R.id.tv_ngay_giao);
            tvMoTa = itemView.findViewById(R.id.tv_mo_ta);
            tvTrangThai = itemView.findViewById(R.id.tv_trang_thai);
            tvGiaBaoGia = itemView.findViewById(R.id.tv_gia_bao_gia);
            tvChiPhiNguyenLieu = itemView.findViewById(R.id.tv_chi_phi_nguyen_lieu);
            tvThongTinBaoGia = itemView.findViewById(R.id.tv_thong_tin_bao_gia);
            imgAnhMau = itemView.findViewById(R.id.img_anh_mau);
            btnBaoGia = itemView.findViewById(R.id.btn_bao_gia);
            layoutBaoGia = itemView.findViewById(R.id.layout_bao_gia);
        }
    }
}
