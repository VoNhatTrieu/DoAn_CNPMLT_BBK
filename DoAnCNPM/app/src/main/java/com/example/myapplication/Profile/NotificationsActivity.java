package com.example.myapplication.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {
    private static final String TAG = "NotificationsActivity";

    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private RecyclerView recyclerView;
    private List<Notification> notificationsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        initFirebase();
        setupRecyclerView();
        loadNotifications();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thông báo");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recycler_notifications);

    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void setupRecyclerView() {
        notificationsList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationsList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadNotifications() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);
        String userId = currentUser.getUid();

        db.collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    showLoading(false);

                    if (error != null) {
                        Log.e(TAG, "Error loading notifications: ", error);
                        Toast.makeText(this, "Lỗi tải thông báo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) {
                        showEmptyState(true);
                        return;
                    }

                    notificationsList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        try {
                            Notification notification = doc.toObject(Notification.class);
                            notification.setId(doc.getId());
                            notificationsList.add(notification);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing notification: ", e);
                        }
                    }

                    adapter.updateNotifications(notificationsList);
                    showEmptyState(notificationsList.isEmpty());
                });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyState(boolean show) {
        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void markAllNotificationsAsRead() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        db.collection("notifications")
                .whereEqualTo("userId", userId)
                .whereEqualTo("read", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Không có thông báo chưa đọc", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.update(doc.getReference(), "read", true);
                    }

                    batch.commit().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi cập nhật thông báo", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error marking notifications as read: ", e);
                    Toast.makeText(this, "Lỗi cập nhật thông báo", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onNotificationClick(Notification notification) {
        if (!notification.isRead()) {
            markNotificationAsRead(notification);
        }
        showNotificationDetail(notification);
    }

    private void markNotificationAsRead(Notification notification) {
        db.collection("notifications")
                .document(notification.getId())
                .update("read", true)
                .addOnFailureListener(e -> Log.e(TAG, "Error marking notification as read: ", e));
    }

    @Override
    public void onViewOrderClick(String orderId) {
        Intent intent = new Intent(this, OrderDetaiActivity.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
    }

    private void showNotificationDetail(Notification notification) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(notification.getTitle())
                .setMessage(notification.getMessage())
                .setPositiveButton("Đóng", null);

        if (notification.getOrderId() != null && !notification.getOrderId().isEmpty()) {
            builder.setNeutralButton("Xem đơn hàng", (dialog, which) ->
                    onViewOrderClick(notification.getOrderId()));
        }

        builder.show();
    }
}
