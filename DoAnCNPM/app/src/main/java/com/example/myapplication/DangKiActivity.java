package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DangKiActivity extends AppCompatActivity {
    private TextInputEditText etTen, etEmail, etSDT, etMK, etChapNhanMk;
    private CheckBox cbCheck;
    private MaterialButton btnDKI;
    private TextView tvDN, tvTem;
    private ImageView btback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        etTen = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etSDT = findViewById(R.id.etPhone);
        etMK = findViewById(R.id.etPassword);
        etChapNhanMk = findViewById(R.id.etConfirmPassword);
        cbCheck = findViewById(R.id.cbTerms);
        btnDKI = findViewById(R.id.btnRegister);
        tvDN = findViewById(R.id.tvLoginNow);
        tvTem = findViewById(R.id.tvTerms);
        btback = findViewById(R.id.btn_back);

        btback.setOnClickListener(v -> finish());
        tvTem.setOnClickListener(v -> {
            Toast.makeText(DangKiActivity.this, "Đang mở điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
        });

        // SỬA LỖI: Chuyển đến MainActivity với thông tin để hiển thị TaiKhoanFragment
        tvDN.setOnClickListener(v -> {
            Intent intent = new Intent(DangKiActivity.this, MainActivity.class);
            intent.putExtra("SHOW_TAIKHOAN_FRAGMENT", true); // Truyền thông tin để hiển thị TaiKhoanFragment
            startActivity(intent);
            finish();
        });

        btnDKI.setOnClickListener(v -> DangKi());
    }

    private boolean CheckDK(String fullName, String email, String phone, String password, String confirmPassword) {
        if (TextUtils.isEmpty(fullName)) {
            etTen.setError("Vui lòng nhập đầy đủ họ và tên");
            etTen.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập địa chỉ email");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            etSDT.setError("Vui lòng nhập số điện thoại");
            etSDT.requestFocus();
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            etSDT.setError("Vui lòng nhập số điện thoại hợp lệ (ít nhất 10 số)");
            etSDT.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etMK.setError("Vui lòng nhập mật khẩu");
            etMK.requestFocus();
            return false;
        }
        if (password.length() < 5) {
            etMK.setError("Mật khẩu phải có ít nhất 5 ký tự");
            etMK.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etChapNhanMk.setError("Vui lòng xác nhận mật khẩu");
            etChapNhanMk.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etChapNhanMk.setError("Mật khẩu không khớp");
            etChapNhanMk.requestFocus();
            return false;
        }

        return true;
    }

    private void DangKi() {
        String fullname = etTen.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String SDT = etSDT.getText().toString().trim();
        String passw = etMK.getText().toString().trim();
        String chekmk = etChapNhanMk.getText().toString().trim();

        if (!CheckDK(fullname, email, SDT, passw, chekmk)) {
            return;
        }
        if (!cbCheck.isChecked()) {
            Toast.makeText(this, "Vui lòng chấp nhận điều khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

        // SỬA LỖI: Sau khi đăng ký thành công, chuyển đến MainActivity với thông tin để hiển thị TaiKhoanFragment
        Intent intent = new Intent(DangKiActivity.this, MainActivity.class);
        intent.putExtra("SHOW_TAIKHOAN_FRAGMENT", true); // Truyền thông tin để hiển thị TaiKhoanFragment
        startActivity(intent);
        finish();
    }
}