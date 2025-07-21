package com.example.myapplication.admin;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Chat.ChatAdapter;
import com.example.myapplication.Chat.Message;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.example.myapplication.R;

public class AdminChatActivity extends AppCompatActivity {
    private static final String TAG = "AdminChatActivity";

    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private Toolbar toolbar;

    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private DatabaseReference chatRef;
    private String adminId = "admin_001";
    private String adminName = "Admin Support";
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);
        // Nhận userId từ Intent
        userId = getIntent().getStringExtra("USER_ID");
        String userName = getIntent().getStringExtra("USER_NAME");

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        initViews();
        initFirebase();
        setupRecyclerView();
        setupClickListeners();
        loadMessages();
        // Set title với tên user
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat với " + (TextUtils.isEmpty(userName) ? userId : userName));
        }
    }
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void initFirebase() {
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(userId);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, adminId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String messageContent = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageContent)) {
            Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = new Message(adminId, adminName, "admin", messageContent);

        String messageId = chatRef.push().getKey();
        message.setMessageId(messageId);

        chatRef.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> {
                    etMessage.setText("");
                    Log.d(TAG, "Tin nhắn admin đã được gửi thành công");

                    // Đánh dấu tất cả tin nhắn của user là đã đọc
                    markUserMessagesAsRead();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi gửi tin nhắn admin: " + e.getMessage());
                    Toast.makeText(AdminChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                });
    }

    private void markUserMessagesAsRead() {
        chatRef.orderByChild("senderType").equalTo("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    messageSnapshot.getRef().child("read").setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi đánh dấu tin nhắn đã đọc: " + error.getMessage());
            }
        });
    }

    private void loadMessages() {
        chatRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();

                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    Message message = messageSnapshot.getValue(Message.class);
                    if (message != null) {
                        messageList.add(message);
                    }
                }

                chatAdapter.notifyDataSetChanged();

                if (messageList.size() > 0) {
                    rvMessages.smoothScrollToPosition(messageList.size() - 1);
                }

                Log.d(TAG, "Admin đã tải " + messageList.size() + " tin nhắn");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Lỗi khi tải tin nhắn: " + error.getMessage());
                Toast.makeText(AdminChatActivity.this, "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}