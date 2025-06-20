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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TaiKhoanFragment extends Fragment {

    private ImageView ivUserAvatar;
    private TextView tvWelcomeTitle, tvWelcomeSubtitle;
    private Button btnLogin, btnRegister;
    private LinearLayout llSupport, llAbout;

    // Giả lập trạng thái đăng nhập (false = chưa đăng nhập)
    private boolean isLoggedIn = false;

    public TaiKhoanFragment() {
        // Constructor mặc định
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);

        // Ánh xạ view
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        tvWelcomeTitle = view.findViewById(R.id.tv_welcome_title);
        tvWelcomeSubtitle = view.findViewById(R.id.tv_welcome_subtitle);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);
        llSupport = view.findViewById(R.id.ll_support);
        llAbout = view.findViewById(R.id.ll_about);

        // Kiểm tra trạng thái đăng nhập giả lập
        if (isLoggedIn) {
            // Nếu đã đăng nhập
            tvWelcomeTitle.setText("Xin chào, Người dùng!");
            tvWelcomeSubtitle.setText("Bạn đã đăng nhập thành công.");
            btnLogin.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
        } else {
            // Nếu chưa đăng nhập
            tvWelcomeTitle.setText("Chào mừng đến với ứng dụng");
            tvWelcomeSubtitle.setText("Đăng nhập để trải nghiệm đầy đủ tính năng");
            btnLogin.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.VISIBLE);
        }

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
            // showToast("Đang phát triển chức năng hỗ trợ");
        });

        // Về chúng tôi
        llAbout.setOnClickListener(v -> {
            // showToast("Ứng dụng bán bánh kem - Phiên bản demo");
        });

        return view;
    }
}
