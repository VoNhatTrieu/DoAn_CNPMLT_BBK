package com.example.myapplication.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import com.example.myapplication.R;
public class Order_lsdhAdapter extends RecyclerView.Adapter<Order_lsdhAdapter.OrderViewHolder>{
    private Context context;
    private List<lsdh_order> orderList;
    private OnCancelClickListener cancelListener;
    public interface OnCancelClickListener {
        void onCancelClick(lsdh_order order);
    }
    public Order_lsdhAdapter(Context context, List<lsdh_order> orderList, OnCancelClickListener cancelListener) {
        this.context = context;
        this.orderList = orderList;
        this.cancelListener = cancelListener;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lsdh, parent, false);
        return new OrderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
       lsdh_order order = orderList.get(position);

        holder.tvOrderId.setText("Mã đơn hàng: #" + order.getOrderId());
        holder.tvOrderTotal.setText(String.format("Tổng tiền: %,dđ", order.getTotalAmount()));
        holder.tvOrderStatus.setText("Trạng thái: " + layTextTrangThai(order.getStatus()));

        if (order.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.tvOrderDate.setText("Ngày đặt: " + sdf.format(order.getCreatedAt()));
        } else {
            holder.tvOrderDate.setText("Ngày đặt: N/A");
        }

        // Hiển thị nút hủy chỉ cho đơn hàng với
        if (order.getStatus().equals("Pending")) {
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnCancelOrder.setOnClickListener(v -> cancelListener.onCancelClick(order));
        } else {
            holder.btnCancelOrder.setVisibility(View.GONE);
        }
    }
    private String layTextTrangThai(String status) {
        switch (status) {
            case "Pending": return "Đang chờ xử lý";
            case "Processing": return "Đang xử lý";
            case "Shipped": return "Đang giao";
            case "Delivered": return "Đã giao";
            case "Cancelled": return "Đã hủy";
            default: return status;
        }
    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus;
        Button btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
        }
    }

}
