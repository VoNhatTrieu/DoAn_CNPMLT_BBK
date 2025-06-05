package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

class LoginFragment extends Fragment {

    private ImageView btnBack;
    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private Button btnLogin;
    private TextView tvForgotPassword, tvCreateAccount;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Gắn layout cho fragment
        View view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);

        // Ánh xạ các view từ layout
        btnBack = view.findViewById(R.id.btn_back);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        cbRemember = view.findViewById(R.id.cbRemember);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        tvCreateAccount = view.findViewById(R.id.tvCreateAccount);

        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                boolean isRemember = cbRemember.isChecked();
                Toast.makeText(getContext(), "Đăng nhập thành công (giả lập)\nGhi nhớ: " + isRemember, Toast.LENGTH_SHORT).show();
                // TODO: Xử lý logic đăng nhập thật ở đây
            }
        });

        // Mở màn hình Quên mật khẩu
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Mở màn hình Tạo tài khoản
        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegisterActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
