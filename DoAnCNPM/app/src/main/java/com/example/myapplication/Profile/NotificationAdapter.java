package com.example.myapplication.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private Context context;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
        void onViewOrderClick(String orderId);
    }

    public NotificationAdapter(List<Notification> notifications, Context context, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.context = context;
        this.listener = listener;
    }

    public void updateNotifications(List<Notification> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);

        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(formatTimestamp(notification.getTimestamp()));

        // Set icon based on notification type
        setNotificationIcon(holder.ivIcon, notification.getType());

        // Background color for unread notifications
        if (!notification.isRead()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.unread_notification_bg));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
        }

        // Item click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });

        // View Order button
        if (notification.getOrderId() != null && !notification.getOrderId().isEmpty()) {
            holder.btnViewOrder.setVisibility(View.VISIBLE);
            holder.btnViewOrder.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewOrderClick(notification.getOrderId());
                }
            });
        } else {
            holder.btnViewOrder.setVisibility(View.GONE);
        }
    }

    private void setNotificationIcon(ImageView imageView, String type) {
        int iconRes;
        switch (type != null ? type : "general") {
            case "quote":
                iconRes = R.drawable.ic_price_tag;
                break;
            case "order_update":
                iconRes = R.drawable.ic_order_update;
                break;
            default:
                iconRes = R.drawable.ic_notification;
                break;
        }
        imageView.setImageResource(iconRes);
    }

    private String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";

        Date date = timestamp.toDate();
        long now = System.currentTimeMillis();
        long time = date.getTime();
        long diff = now - time;

        if (diff < 60000) { // Less than 1 minute
            return "Vừa xong";
        } else if (diff < 3600000) { // Less than 1 hour
            return (diff / 60000) + " phút trước";
        } else if (diff < 86400000) { // Less than 24 hours
            return (diff / 3600000) + " giờ trước";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvMessage, tvTime;
        MaterialButton btnViewOrder;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvMessage = itemView.findViewById(R.id.tv_notification_message);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            btnViewOrder = itemView.findViewById(R.id.btn_view_order);
        }
    }
}