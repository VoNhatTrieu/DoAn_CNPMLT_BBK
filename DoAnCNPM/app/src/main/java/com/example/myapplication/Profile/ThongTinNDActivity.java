package com.example.myapplication.Profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.myapplication.DangNhapActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class ThongTinNDActivity extends AppCompatActivity {
    private TextView tvUserName;
    private ImageView profileAvatar;
    // CardViews cho các menu
    private CardView cardFavorites, cardOrderHistory, cardDeliveryAddress,
            cardUpdateInfo, cardChangePassword, cardNotifications,
            cardSupport, cardLogout;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ndactivity);
        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();
        // Kiểm tra đăng nhập
        if (currentUser == null) {
            // Nếu chưa đăng nhập, chuyển về màn hình đăng nhập
            Intent intent = new Intent(this, DangNhapActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        // Khởi tạo views
        initViews();
        // Thiết lập thông tin người dùng
        setupUserInfo();
        // Thiết lập click listeners
        setupClickListeners();
    }
    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        profileAvatar = findViewById(R.id.profile_avatar);
        cardFavorites = findViewById(R.id.card_favorites);
        cardOrderHistory = findViewById(R.id.card_order_history);
        cardDeliveryAddress = findViewById(R.id.card_delivery_address);
        cardUpdateInfo = findViewById(R.id.card_update_info);
        cardChangePassword = findViewById(R.id.card_change_password);
        cardNotifications = findViewById(R.id.card_notifications);
        cardSupport = findViewById(R.id.card_support);
        cardLogout = findViewById(R.id.card_logout);
    }
    private void setupUserInfo() {
        // Lấy thông tin từ Intent hoặc Firebase
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String userEmail = intent.getStringExtra("USER_EMAIL");
        String userUid = intent.getStringExtra("USER_UID");
        // Nếu có tên từ Intent, hiển thị luôn
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            // Nếu không có, lấy từ Firebase Database
            loadUserInfoFromDatabase();
        }
        // Có thể thêm logic load avatar từ Firebase Storage ở đây
    }
    private void loadUserInfoFromDatabase() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        if (name != null && !name.isEmpty()) {
                            tvUserName.setText(name);
                        } else {
                            // Fallback sử dụng email
                            tvUserName.setText(currentUser.getEmail());
                        }
                    } else {
                        // Nếu không có data trong database, sử dụng email
                        tvUserName.setText(currentUser.getEmail());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi
                    tvUserName.setText(currentUser.getEmail());
                }
            });
        }
    }
    private void setupClickListeners() {
        // Bánh đã yêu thích
        cardFavorites.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Bánh yêu thích đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement favorites activity
            // Intent intent = new Intent(this, FavoritesActivity.class);
            // startActivity(intent);
        });
        // Lịch sử đơn hàng
        cardOrderHistory.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Lịch sử đơn hàng đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement order history activity
            // Intent intent = new Intent(this, OrderHistoryActivity.class);
            // startActivity(intent);
        });
        // Địa chỉ giao hàng
        cardDeliveryAddress.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Địa chỉ giao hàng đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement delivery address activity
            // Intent intent = new Intent(this, DeliveryAddressActivity.class);
            // startActivity(intent);
        });
        // Cập nhật thông tin
        cardUpdateInfo.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Cập nhật thông tin đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement update info activity
            // Intent intent = new Intent(this, UpdateInfoActivity.class);
            // startActivity(intent);
        });
        // Đổi mật khẩu
        cardChangePassword.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement change password activity
            // Intent intent = new Intent(this, ChangePasswordActivity.class);
            // startActivity(intent);
        });
        // Thông báo
        cardNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement notifications activity
            // Intent intent = new Intent(this, NotificationsActivity.class);
            // startActivity(intent);
        });
        // Liên hệ hỗ trợ
        cardSupport.setOnClickListener(v -> {
            showSupportDialog();
        });
        // Đăng xuất
        cardLogout.setOnClickListener(v -> {
            showLogoutDialog();
        });
    }
    private void showSupportDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Liên hệ hỗ trợ")
                .setMessage("Bạn có thể liên hệ với chúng tôi qua:\n\n" +
                        "📧 Email: support@bakery.com\n" +
                        "📞 Hotline: 1900-xxxx\n" +
                        "🕐 Thời gian: 8:00 - 22:00 hàng ngày")
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Gọi hotline", (dialog, which) -> {
                    Toast.makeText(this, "Tính năng gọi điện đang phát triển", Toast.LENGTH_SHORT).show();
                    // TODO: Implement phone call
                })
                .show();
    }
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void performLogout() {
        // Đăng xuất Firebase
        mAuth.signOut();
        getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                .edit()
                .putBoolean("remember", false)
                .remove("email")
                .apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(this, DangNhapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra lại trạng thái đăng nhập mỗi khi activity resume
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, DangNhapActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}