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
import com.example.myapplication.Profile.ThongTinNDActivity;
import com.example.myapplication.Yeuthich.DSYTActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaiKhoanFragment extends Fragment {

    private ImageView ivUserAvatar;
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
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = view.findViewById(R.id.tv_welcome_subtitle);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        llSupport = view.findViewById(R.id.ll_support);
        llAbout = view.findViewById(R.id.ll_about);

        // Profile menu items (you'll need to add these to your XML)
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
        // Nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DangNhapActivity.class));
        });

        // Nút đăng ký
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DangKiActivity.class));
        });

        // Hỗ trợ khách hàng
        llSupport.setOnClickListener(v -> {
            showSupportDialog();
        });

        // Về chúng tôi
        llAbout.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ứng dụng bán bánh kem", Toast.LENGTH_SHORT).show();
        });

        // Profile menu click listeners
        if (llFavorites != null) {
            llFavorites.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DSYTActivity.class);
                startActivity(intent);
            });
        }

        if (llOrderHistory != null) {
            llOrderHistory.setOnClickListener(v -> {
               Intent intent=new Intent(getActivity(), LichSuDonHangActivity.class);
               startActivity(intent);
            });
        }

        if (llDeliveryAddress != null) {
            llDeliveryAddress.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Chức năng Địa chỉ giao hàng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }



        if (llChangePassword != null) {
            llChangePassword.setOnClickListener(v -> {
              Intent intent=new Intent(getActivity(), DoiMatKhauActivity.class);
              startActivity(intent);
            });
        }

        if (llNotifications != null) {
            llNotifications.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Chức năng Thông báo đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        if (llContactSupport != null) {
            llContactSupport.setOnClickListener(v -> {
                showSupportDialog();
            });
        }

        if (llLogout != null) {
            llLogout.setOnClickListener(v -> {
                showLogoutDialog();
            });
        }
        ivUserAvatar.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(getActivity(), ThongTinNDActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Vui lòng đăng nhập để chỉnh sửa avatar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Kiểm tra lại trạng thái đăng nhập khi fragment được hiển thị lại
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Người dùng đã đăng nhập - hiển thị profile interface
            showProfileInterface();
        } else {
            // Người dùng chưa đăng nhập - hiển thị giao diện đăng nhập
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
                    // 👉 Nếu là admin, chuyển luôn sang AdminActivity
                    Intent intent = new Intent(getActivity(), com.example.myapplication.admin.AdminActivity.class);
                    startActivity(intent);
                    return; // Không hiển thị giao diện người dùng nữa
                }

                // Nếu không phải admin, hiển thị như bình thường
                String userName = dataSnapshot.child("name").getValue(String.class);
                String userEmail = currentUser.getEmail();

                tvWelcomeTitle.setText(userName != null ? userName : "Xin chào!");
                tvWelcomeSubtitle.setText(userEmail);

                btnLogin.setVisibility(View.GONE);
                btnRegister.setVisibility(View.GONE);
                if (llProfileMenu != null) {
                    llProfileMenu.setVisibility(View.VISIBLE);
                }
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
            }
        });
    }


    private void showLoginInterface() {
        // Hiển thị giao diện đăng nhập
        tvWelcomeTitle.setText("Chào mừng đến với ứng dụng");
        tvWelcomeSubtitle.setText("Đăng nhập để trải nghiệm đầy đủ tính năng");
        btnLogin.setVisibility(View.VISIBLE);
        btnRegister.setVisibility(View.VISIBLE);

        // Ẩn menu profile
        if (llProfileMenu != null) {
            llProfileMenu.setVisibility(View.GONE);
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
        // Đăng xuất Firebase
        mAuth.signOut();

        // Xóa thông tin đăng nhập đã lưu
        if (getActivity() != null) {
            getActivity().getSharedPreferences("LoginPrefs", getActivity().MODE_PRIVATE)
                    .edit()
                    .putBoolean("remember", false)
                    .remove("email")
                    .apply();
        }

        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Cập nhật giao diện
        checkLoginStatus();
    }
}