package com.example.myapplication.Profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.DangNhapActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ThongTinNDActivity extends AppCompatActivity {
    private TextView tvUserName;
    private ImageView profileAvatar;
    private CardView cardFavorites, cardOrderHistory, cardDeliveryAddress,
            cardUpdateInfo, cardChangePassword, cardNotifications,
            cardSupport, cardLogout;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ndactivity);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, DangNhapActivity.class));
            finish();
            return;
        }

        initViews();
        setupClickListeners();
        setupUserInfo();
        loadAvatarFromDatabase();
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
        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText(currentUser.getEmail());
        }
    }

    private void setupClickListeners() {
        profileAvatar.setOnClickListener(v -> openImagePicker());

        cardFavorites.setOnClickListener(v -> Toast.makeText(this, "Chức năng Bánh yêu thích đang phát triển", Toast.LENGTH_SHORT).show());
        cardOrderHistory.setOnClickListener(v -> Toast.makeText(this, "Chức năng Lịch sử đơn hàng đang phát triển", Toast.LENGTH_SHORT).show());
        cardDeliveryAddress.setOnClickListener(v -> Toast.makeText(this, "Chức năng Địa chỉ giao hàng đang phát triển", Toast.LENGTH_SHORT).show());
        cardUpdateInfo.setOnClickListener(v -> Toast.makeText(this, "Chức năng Cập nhật thông tin đang phát triển", Toast.LENGTH_SHORT).show());
        cardChangePassword.setOnClickListener(v -> Toast.makeText(this, "Chức năng Đổi mật khẩu đang phát triển", Toast.LENGTH_SHORT).show());
        cardNotifications.setOnClickListener(v -> Toast.makeText(this, "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show());
        cardSupport.setOnClickListener(v -> showSupportDialog());
        cardLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            String base64Image = encodeImageToBase64(imageUri);
            if (base64Image != null) {
                saveBase64ToRealtime(base64Image);
                profileAvatar.setImageURI(imageUri);
            }
        }
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveBase64ToRealtime(String base64) {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase.child("users").child(uid).child("avatarBase64").setValue(base64)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Lưu avatar thành công", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu avatar", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadAvatarFromDatabase() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            mDatabase.child("users").child(uid).child("avatarBase64")
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        String base64 = snapshot.getValue(String.class);
                        if (base64 != null && !base64.isEmpty()) {
                            byte[] imageBytes = Base64.decode(base64, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            profileAvatar.setImageBitmap(bitmap);
                        } else {
                            profileAvatar.setImageResource(R.drawable.default_avatar);
                        }
                    });
        }
    }

    private void showSupportDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Liên hệ hỗ trợ")
                .setMessage("Bạn có thể liên hệ với chúng tôi qua:\n\n" +
                        "📧 Email: support@bakery.com\n" +
                        "📞 Hotline: 1900-xxxx\n" +
                        "🕐 Thời gian: 8:00 - 22:00 hàng ngày")
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Gọi hotline", (dialog, which) ->
                        Toast.makeText(this, "Tính năng gọi điện đang phát triển", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        mAuth.signOut();
        getSharedPreferences("LoginPrefs", MODE_PRIVATE).edit()
                .putBoolean("remember", false)
                .remove("email")
                .apply();
        Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DangNhapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, DangNhapActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
