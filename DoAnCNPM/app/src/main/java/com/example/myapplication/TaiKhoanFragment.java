package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Profile.DoiMatKhauActivity;
import com.example.myapplication.Profile.LichSuDonHangActivity;
import com.example.myapplication.Profile.NotificationsActivity;
import com.example.myapplication.Profile.ThongTinNDActivity;
import com.example.myapplication.Yeuthich.DSYTActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaiKhoanFragment extends Fragment {

    private ImageView ivUserAvatar, ivNotificationDot;
    private TextView tvWelcomeTitle, tvWelcomeSubtitle;
    private Button btnLogin, btnRegister;
    private LinearLayout llSupport, llAbout;

    // Profile menu items (when logged in)
    private LinearLayout llProfileMenu;
    private LinearLayout llFavorites, llOrderHistory, llDeliveryAddress
            , llChangePassword, llNotifications,
            llContactSupport, llLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    public TaiKhoanFragment() {
        // Constructor mặc định
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ánh xạ view
        initViews(view);

        // Kiểm tra trạng thái đăng nhập
        checkLoginStatus();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        ivNotificationDot = view.findViewById(R.id.iv_notification_dot);
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = view.findViewById(R.id.tv_welcome_subtitle);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        llSupport = view.findViewById(R.id.ll_support);
        llAbout = view.findViewById(R.id.ll_about);

        // Profile menu items
        llProfileMenu = view.findViewById(R.id.ll_profile_menu);
        llFavorites = view.findViewById(R.id.ll_favorites);
        llOrderHistory = view.findViewById(R.id.ll_order_history);
        llDeliveryAddress = view.findViewById(R.id.ll_delivery_address);
        llChangePassword = view.findViewById(R.id.ll_change_password);
        llNotifications = view.findViewById(R.id.ll_notifications);
        llContactSupport = view.findViewById(R.id.ll_contact_support);
        llLogout = view.findViewById(R.id.ll_logout);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), DangNhapActivity.class)));
        btnRegister.setOnClickListener(v -> startActivity(new Intent(getActivity(), DangKiActivity.class)));
        llSupport.setOnClickListener(v -> showSupportDialog());
        llAbout.setOnClickListener(v -> Toast.makeText(getContext(), "Ứng dụng bán bánh kem", Toast.LENGTH_SHORT).show());

        if (llFavorites != null) {
            llFavorites.setOnClickListener(v -> startActivity(new Intent(getActivity(), DSYTActivity.class)));
        }
        if (llOrderHistory != null) {
            llOrderHistory.setOnClickListener(v -> startActivity(new Intent(getActivity(), LichSuDonHangActivity.class)));
        }
        if (llDeliveryAddress != null) {
            llDeliveryAddress.setOnClickListener(v -> Toast.makeText(getContext(), "Chức năng Địa chỉ giao hàng đang phát triển", Toast.LENGTH_SHORT).show());
        }
        if (llChangePassword != null) {
            llChangePassword.setOnClickListener(v -> startActivity(new Intent(getActivity(), DoiMatKhauActivity.class)));
        }
        if (llNotifications != null) {
            llNotifications.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationsActivity.class)));
        }
        if (llContactSupport != null) {
            llContactSupport.setOnClickListener(v -> showSupportDialog());
        }
        if (llLogout != null) {
            llLogout.setOnClickListener(v -> showLogoutDialog());
        }
        ivUserAvatar.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(getActivity(), ThongTinNDActivity.class));
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để chỉnh sửa avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            showProfileInterface();
        } else {
            showLoginInterface();
        }
    }

    private void showProfileInterface() {
        String userId = currentUser.getUid();
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String role = dataSnapshot.child("role").getValue(String.class);

                if ("admin".equals(role)) {
                    startActivity(new Intent(getActivity(), com.example.myapplication.admin.AdminActivity.class));
                    return;
                }

                String userName = dataSnapshot.child("name").getValue(String.class);
                String userEmail = currentUser.getEmail();

                tvWelcomeTitle.setText(userName != null ? userName : "Xin chào!");
                tvWelcomeSubtitle.setText(userEmail);

                btnLogin.setVisibility(View.GONE);
                btnRegister.setVisibility(View.GONE);
                if (llProfileMenu != null) {
                    llProfileMenu.setVisibility(View.VISIBLE);
                }

                checkUnreadNotifications();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tvWelcomeTitle.setText("Xin chào!");
                tvWelcomeSubtitle.setText(currentUser.getEmail());
                btnLogin.setVisibility(View.GONE);
                btnRegister.setVisibility(View.GONE);
                if (llProfileMenu != null) {
                    llProfileMenu.setVisibility(View.VISIBLE);
                }

                checkUnreadNotifications();
            }
        });
    }

    private void checkUnreadNotifications() {
        if (ivNotificationDot == null || currentUser == null) return;

        FirebaseFirestore.getInstance()
                .collection("notifications")
                .whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("read", false)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        ivNotificationDot.setVisibility(View.VISIBLE);
                    } else {
                        ivNotificationDot.setVisibility(View.GONE);
                    }
                });
    }

    private void showLoginInterface() {
        tvWelcomeTitle.setText("Chào mừng đến với ứng dụng");
        tvWelcomeSubtitle.setText("Đăng nhập để trải nghiệm đầy đủ tính năng");
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);

        if (llProfileMenu != null) {
            llProfileMenu.setVisibility(View.GONE);
        }
        if (ivNotificationDot != null) {
            ivNotificationDot.setVisibility(View.GONE);
        }
    }

    private void showSupportDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Liên hệ hỗ trợ")
                .setMessage("Bạn có thể liên hệ với chúng tôi qua:\n\n" +
                        "📧 Email: support@bakery.com\n" +
                        "📞 Hotline: 1900-xxxx\n" +
                        "🕐 Thời gian: 8:00 - 22:00 hàng ngày")
                .setPositiveButton("Đóng", null)
                .setNeutralButton("Gọi hotline", (dialog, which) -> {
                    Toast.makeText(getContext(), "Tính năng gọi điện đang phát triển", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performLogout() {
        mAuth.signOut();

        if (getActivity() != null) {
            getActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE)
                    .edit()
                    .putBoolean("remember", false)
                    .remove("email")
                    .apply();
        }

        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        checkLoginStatus();
    }
}
