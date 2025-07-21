package com.example.myapplication.admin;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.admin.ChatRoom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<ChatRoom> chatRoomList;
    private OnChatClickListener listener;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public interface OnChatClickListener {
        void onChatClick(ChatRoom chatRoom);
    }

    public ChatListAdapter(List<ChatRoom> chatRoomList, OnChatClickListener listener) {
        this.chatRoomList = chatRoomList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_room, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomList.get(position);

        // Hiển thị tên user
        holder.tvUserName.setText(chatRoom.getUserName());

        // Hiển thị tin nhắn cuối
        if (!TextUtils.isEmpty(chatRoom.getLastMessage())) {
            holder.tvLastMessage.setText(chatRoom.getLastMessage());
        } else {
            holder.tvLastMessage.setText("Không có tin nhắn");
        }

        // Hiển thị thời gian
        if (chatRoom.getLastMessageTime() > 0) {
            Date messageDate = new Date(chatRoom.getLastMessageTime());
            Date today = new Date();

            // Nếu tin nhắn trong ngày hôm nay, hiển thị giờ
            // Nếu không, hiển thị ngày
            if (dateFormat.format(messageDate).equals(dateFormat.format(today))) {
                holder.tvTime.setText(timeFormat.format(messageDate));
            } else {
                holder.tvTime.setText(dateFormat.format(messageDate));
            }
        }

        // Hiển thị số tin nhắn chưa đọc
        if (chatRoom.getUnreadCount() > 0) {
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.tvUnreadCount.setText(String.valueOf(chatRoom.getUnreadCount()));
        } else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }

        // Xử lý click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(chatRoom);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatRoomList.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvLastMessage;
        TextView tvTime;
        TextView tvUnreadCount;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }
    }
}