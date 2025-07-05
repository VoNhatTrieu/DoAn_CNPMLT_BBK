package com.example.myapplication.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvAddress, tvRole, tvStatus, tvCreatedAt, tvLastLogin;
    private ImageView ivAvatar ,btnBack;
    private Button btnEdit;
    private ProgressBar progressBar;

    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        userId = getIntent().getStringExtra("uid");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Ánh xạ view đúng với layout bạn gửi
        ivAvatar = findViewById(R.id.ivAvatar);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvRole = findViewById(R.id.tvRole);
        tvStatus = findViewById(R.id.tvStatus);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        btnEdit = findViewById(R.id.btnEdit);
        progressBar = findViewById(R.id.progressBar);
        btnBack=findViewById(R.id.btnBack);

        loadUserInfo();

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditUserActivity.class);
            intent.putExtra("uid", userId);
            startActivity(intent);
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        userRef.get().addOnSuccessListener(snapshot -> {
            progressBar.setVisibility(View.GONE);

            if (snapshot.exists()) {
                tvName.setText("Tên: " + getStringValue(snapshot, "name"));
                tvEmail.setText("Email: " + getStringValue(snapshot, "email"));
                tvPhone.setText("Số điện thoại: " + getStringValue(snapshot, "phoneNumber"));
                tvAddress.setText("Địa chỉ: " + getStringValue(snapshot, "address")); // Có thể không tồn tại
                tvRole.setText("Vai trò: " + getStringValue(snapshot, "role"));

                boolean isVerified = Boolean.TRUE.equals(snapshot.child("isEmailVerified").getValue(Boolean.class));
                tvStatus.setText("Trạng thái: " + (isVerified ? "Hoạt động" : "Bị khóa"));
                tvStatus.setTextColor(getResources().getColor(isVerified ? R.color.gradient_start : R.color.gray));

                Long createdAt = snapshot.child("createdAt").getValue(Long.class);
                if (createdAt != null) {
                    String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            .format(new Date(createdAt));
                    tvCreatedAt.setText("Ngày tạo: " + dateStr);
                } else {
                    tvCreatedAt.setText("Ngày tạo: Chưa rõ");
                }

                // Tùy bạn lưu lastLogin như thế nào, mặc định bỏ qua nếu không có
                Long lastLogin = snapshot.child("lastLogin").getValue(Long.class);
                if (lastLogin != null) {
                    String loginStr = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            .format(new Date(lastLogin));
                    tvLastLogin.setText("Đăng nhập lần cuối: " + loginStr);
                } else {
                    tvLastLogin.setText("Đăng nhập lần cuối: Chưa rõ");
                }
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
        });
    }

    private String getStringValue(DataSnapshot snapshot, String key) {
        String val = snapshot.child(key).getValue(String.class);
        return val != null ? val : "Chưa cập nhật";
    }
}
