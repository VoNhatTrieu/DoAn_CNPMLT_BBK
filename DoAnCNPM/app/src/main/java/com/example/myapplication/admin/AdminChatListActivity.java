package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class AdminChatListActivity extends AppCompatActivity implements ChatListAdapter.OnChatClickListener {
    private static final String TAG = "AdminChatListActivity";

    private RecyclerView rvChatList;
    private View emptyView;
    private Toolbar toolbar;

    private ChatListAdapter chatListAdapter;
    private List<ChatRoom> chatRoomList;
    private DatabaseReference chatsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat_list);
        initViews();
        initFirebase();
        setupRecyclerView();
        loadChatRooms();
    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvChatList = findViewById(R.id.rvChatList);
        emptyView = findViewById(R.id.emptyView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quản lý Chat");

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initFirebase() {
        chatsRef = FirebaseDatabase.getInstance().getReference("chats");
    }

    private void setupRecyclerView() {
        chatRoomList = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(chatRoomList, this);

        rvChatList.setLayoutManager(new LinearLayoutManager(this));
        rvChatList.setAdapter(chatListAdapter);
    }

    private void loadChatRooms() {
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatRoomList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();

                    // Tạo ChatRoom object
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setUserId(userId);

                    // Đếm tin nhắn chưa đọc và lấy tin nhắn mới nhất
                    int unreadCount = 0;
                    long lastMessageTime = 0;
                    String lastMessage = "";
                    String userName = "Khách hàng";

                    for (DataSnapshot messageSnapshot : userSnapshot.getChildren()) {
                        DataSnapshot senderTypeSnapshot = messageSnapshot.child("senderType");
                        DataSnapshot readSnapshot = messageSnapshot.child("read");
                        DataSnapshot timestampSnapshot = messageSnapshot.child("timestamp");
                        DataSnapshot contentSnapshot = messageSnapshot.child("content");
                        DataSnapshot senderNameSnapshot = messageSnapshot.child("senderName");

                        // Đếm tin nhắn chưa đọc của user
                        if ("user".equals(senderTypeSnapshot.getValue(String.class)) &&
                                !Boolean.TRUE.equals(readSnapshot.getValue(Boolean.class))) {
                            unreadCount++;
                        }

                        // Lấy tin nhắn mới nhất
                        if (timestampSnapshot.exists()) {
                            long timestamp = timestampSnapshot.getValue(Long.class);
                            if (timestamp > lastMessageTime) {
                                lastMessageTime = timestamp;
                                lastMessage = contentSnapshot.getValue(String.class);

                                // Lấy tên user từ tin nhắn đầu tiên
                                if (senderNameSnapshot.exists()) {
                                    userName = senderNameSnapshot.getValue(String.class);
                                }
                            }
                        }
                    }

                    chatRoom.setUserName(userName);
                    chatRoom.setLastMessage(lastMessage);
                    chatRoom.setLastMessageTime(lastMessageTime);
                    chatRoom.setUnreadCount(unreadCount);

                    // Chỉ thêm vào danh sách nếu có tin nhắn
                    if (lastMessageTime > 0) {
                        chatRoomList.add(chatRoom);
                    }
                }

                // Sắp xếp theo thời gian tin nhắn mới nhất
                chatRoomList.sort((room1, room2) ->
                        Long.compare(room2.getLastMessageTime(), room1.getLastMessageTime()));

                chatListAdapter.notifyDataSetChanged();

                // Hiển thị/ẩn empty view
                if (chatRoomList.isEmpty()) {
                    rvChatList.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    rvChatList.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                }

                Log.d(TAG, "Đã tải " + chatRoomList.size() + " cuộc chat");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải danh sách chat: " + error.getMessage());
                Toast.makeText(AdminChatListActivity.this, "Lỗi khi tải danh sách chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onChatClick(ChatRoom chatRoom) {
        Intent intent = new Intent(this, AdminChatActivity.class);
        intent.putExtra("USER_ID", chatRoom.getUserId());
        intent.putExtra("USER_NAME", chatRoom.getUserName());
        startActivity(intent);
    }
}
