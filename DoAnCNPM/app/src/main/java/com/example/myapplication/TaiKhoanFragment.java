package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class TaiKhoanFragment extends Fragment {
    private ImageView btback;
    private TextInputEditText email, password;
    private CheckBox nho;
    private MaterialButton login, gg, fb;
    private TextView qmk, taotk;

    // SharedPreferences keys
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER = "remember";

    public TaiKhoanFragment() {
        // Required empty public constructor
    }

    public static TaiKhoanFragment newInstance(String param1, String param2) {
        return new TaiKhoanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tai_khoan, container, false);

        // Khởi tạo view
        btback = view.findViewById(R.id.btn_back);
        email = view.findViewById(R.id.etEmail);
        password = view.findViewById(R.id.etPassword);
        nho = view.findViewById(R.id.cbRemember);
        login = view.findViewById(R.id.btnLogin);
        gg = view.findViewById(R.id.btnGoogleLogin);
        fb = view.findViewById(R.id.btnFacebookLogin);
        qmk = view.findViewById(R.id.tvForgotPassword);
        taotk = view.findViewById(R.id.tvCreateAccount);

        // Nút quay lại
        btback.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // Nút đăng nhập
        login.setOnClickListener(v -> Login());

        // Chuyển sang màn hình Quên mật khẩu
        qmk.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), QuenMKActivity.class);
                startActivity(intent);
            }
        });

        // Chuyển sang màn hình Tạo tài khoản
        taotk.setOnClickListener(v -> {
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), DangKiActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    // Xử lý đăng nhập
    private void Login() {
        String Email = email.getText() != null ? email.getText().toString().trim() : "";
        String Pw = password.getText() != null ? password.getText().toString().trim() : "";

        if (!kiemTraEmailPassword(Email, Pw)) {
            return;
        }

        // Giả lập đăng nhập thành công
        Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

        // Lưu thông tin nếu chọn "Nhớ"
        if (nho.isChecked()) {
            saveLoginInfo(Email);
        } else {
            clearSavedLoginInfo();
        }

        // TODO: Thêm xử lý chuyển màn hình chính sau khi đăng nhập thành công
    }

    // Kiểm tra định dạng email + mật khẩu không rỗng
    private boolean kiemTraEmailPassword(String Email, String Password) {
        if (Email.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(getContext(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }

        if (Password.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }

        return true;
    }

    // Lưu thông tin đăng nhập nếu "nhớ"
    private void saveLoginInfo(String emailText) {
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_EMAIL, emailText);
            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }
    }

    // Xóa thông tin đã lưu nếu không nhớ
    private void clearSavedLoginInfo() {
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(KEY_EMAIL);
            editor.putBoolean(KEY_REMEMBER, false);
            editor.apply();
        }
    }

    // Tải email đã lưu nếu có
    private void loadSavedLoginInfo() {
        if (getContext() != null) {
            SharedPreferences prefs = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            boolean rememberMe = prefs.getBoolean(KEY_REMEMBER, false);

            if (rememberMe) {
                String savedEmail = prefs.getString(KEY_EMAIL, "");
                if (!savedEmail.isEmpty()) {
                    email.setText(savedEmail);
                    nho.setChecked(true);
                }
            }
        }
    }

    // Khi fragment mở lại thì tự động nạp dữ liệu đã lưu
    @Override
    public void onResume() {
        super.onResume();
        loadSavedLoginInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
