package com.example.myapplication.Chat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.Chat.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_ADMIN = 2;

    private List<Message> messageList;
    private String currentUserId;
    private SimpleDateFormat dateFormat;

    public ChatAdapter(List<Message> messageList, String currentUserId) {
        this.messageList = messageList;
        this.currentUserId = currentUserId;
        this.dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderType().equals("admin")) {
            return VIEW_TYPE_ADMIN;
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_user, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_admin, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.tvMessage.setText(message.getContent());
        holder.tvSenderName.setText(message.getSenderName());

        // Format thời gian
        String timeString = dateFormat.format(new Date(message.getTimestamp()));
        holder.tvTime.setText(timeString);

        // Hiển thị trạng thái đã đọc (chỉ cho tin nhắn của user)
        if (message.getSenderType().equals("user")) {
            if (holder.tvReadStatus != null) {
                holder.tvReadStatus.setText(message.isRead() ? "Đã đọc" : "Đã gửi");
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvSenderName;
        TextView tvTime;
        TextView tvReadStatus;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvReadStatus = itemView.findViewById(R.id.tvReadStatus);
        }
    }
}