package com.example.myapplication.Profile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class Order_lsdhAdapter extends RecyclerView.Adapter<Order_lsdhAdapter.OrderViewHolder>{
    private Context context;
    private List<lsdh_order> orderList;
    private OnTrackClickListener onTrackClickListener;
    private OnCancelClickListener cancelListener;
    public interface OnCancelClickListener {
        void onCancelClick(lsdh_order order);
    }
    public interface OnTrackClickListener {
        void onTrackClick(lsdh_order order);
    }
    public Order_lsdhAdapter(Context context, List<lsdh_order> orderList, OnCancelClickListener cancelListener, OnTrackClickListener onTrackClickListener) {
        this.context = context;
        this.orderList = orderList;
        this.cancelListener = cancelListener;
        this.onTrackClickListener = onTrackClickListener;
    }
    public Order_lsdhAdapter(Context context, List<lsdh_order> orderList, OnCancelClickListener cancelListener) {
        this.context = context;
        this.orderList = orderList;
        this.cancelListener = cancelListener;
        this.onTrackClickListener = null;
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
        //nut theo dõi đơn hàng
        holder.btnTrackOrder.setOnClickListener(v -> {
            if(onTrackClickListener != null){
                onTrackClickListener.onTrackClick(order);
            }
            else {
                Intent intent=new Intent(context, CTDonDHActivity.class);
                intent.putExtra("orderId",order.getOrderId());
                context.startActivity(intent);
            }

        });

//        holder.tvoderdetail.setOnClickListener(v -> {
//            Intent intent=new Intent(context, CTDonDHActivity.class);
//            intent.putExtra("order",order);
//            context.startActivity(intent);
//        });
        if (order.getStatus().equals("Cancelled")) {
            holder.btnReOrder.setVisibility(View.VISIBLE);
            holder.btnReOrder.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("orders").document(order.getOrderId())
                        .update("status", "Processing")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Đã đặt lại đơn hàng!", Toast.LENGTH_SHORT).show();
                            order.setStatus("Processing"); // cập nhật local
                            notifyItemChanged(position); // làm mới item trong RecyclerView
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Lỗi khi đặt lại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.btnReOrder.setVisibility(View.GONE);
        }

        progressStepper(holder, order.getStatus());
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
    private  void progressStepper(OrderViewHolder holder, String status) {
        resetProges(holder);
        switch (status) {
            case "Pending":
            holder.step1Circle.setBackgroundResource(R.drawable.step_completed_circle);
            break;
            case "Processing":
                holder.step1Circle.setBackgroundResource(R.drawable.step_completed_circle);
                holder.step2Circle.setBackgroundResource(R.drawable.step_current_circle);
                holder.progressLine1.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
                case "Shipped":
                    holder.step1Circle.setBackgroundResource(R.drawable.step_completed_circle);
                    holder.step2Circle.setBackgroundResource(R.drawable.step_completed_circle);
                    holder.step3Circle.setBackgroundResource(R.drawable.step_current_circle);
                    holder.progressLine1.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                    holder.progressLine2.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                    break;
            case "Delivered":
                holder.step1Circle.setBackgroundResource(R.drawable.step_completed_circle);
                holder.step2Circle.setBackgroundResource(R.drawable.step_completed_circle);
                holder.step3Circle.setBackgroundResource(R.drawable.step_completed_circle);
                holder.step4Circle.setBackgroundResource(R.drawable.step_completed_circle);
                holder.progressLine1.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                holder.progressLine2.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                holder.progressLine3.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "Cancelled":
                holder.step1Circle.setBackgroundResource(R.drawable.step_completed_circle);
                break;

        }
    }
    private void resetProges(OrderViewHolder holder){
        holder.step1Circle.setBackgroundResource(R.drawable.step_pending_circle);
        holder.step2Circle.setBackgroundResource(R.drawable.step_pending_circle);
        holder.step3Circle.setBackgroundResource(R.drawable.step_pending_circle);
        holder.step4Circle.setBackgroundResource(R.drawable.step_pending_circle);

        int color = context.getResources().getColor(android.R.color.darker_gray);
        holder.progressLine1.setBackgroundColor(color);
        holder.progressLine2.setBackgroundColor(color);
        holder.progressLine3.setBackgroundColor(color);
    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderTotal, tvOrderStatus,tvoderdetail;
        Button btnCancelOrder, btnTrackOrder;
        View step1Circle, step2Circle, step3Circle, step4Circle;
        View progressLine1, progressLine2, progressLine3;
        Button btnReOrder;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
            btnTrackOrder = itemView.findViewById(R.id.btn_track_order);
            tvoderdetail=itemView.findViewById(R.id.tv_order_details);
            step1Circle = itemView.findViewById(R.id.step_1_circle);
            step2Circle = itemView.findViewById(R.id.step_2_circle);
            step3Circle = itemView.findViewById(R.id.step_3_circle);
            step4Circle = itemView.findViewById(R.id.step_4_circle);
            btnReOrder = itemView.findViewById(R.id.btn_reorder);


            progressLine1 = itemView.findViewById(R.id.progress_line_1);
            progressLine2 = itemView.findViewById(R.id.progress_line_2);
            progressLine3 = itemView.findViewById(R.id.progress_line_3);
        }
    }

}
