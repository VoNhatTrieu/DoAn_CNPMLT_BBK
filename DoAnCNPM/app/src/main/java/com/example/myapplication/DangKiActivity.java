package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DangKiActivity extends AppCompatActivity {
    private static final String TAG = "DangKiActivity";

    private TextInputEditText etTen, etEmail, etSDT, etMK, etChapNhanMk;
    private CheckBox cbCheck;
    private MaterialButton btnDKI;
    private TextView tvDN, tvTem;
    private ImageView btback;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        // Ánh xạ view
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
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btback.setOnClickListener(v -> finish());
        tvTem.setOnClickListener(v ->
                Toast.makeText(DangKiActivity.this, "Đang mở điều khoản dịch vụ", Toast.LENGTH_SHORT).show()
        );
        tvDN.setOnClickListener(v -> {
            Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
            startActivity(intent);
            finish();
        });
        btnDKI.setOnClickListener(v -> DangKi());
    }
    private void DangKi() {
        String fullname = etTen.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String SDT = etSDT.getText().toString().trim();
        String passw = etMK.getText().toString().trim();
        String chekmk = etChapNhanMk.getText().toString().trim();
        if (!CheckDK(fullname, email, SDT, passw, chekmk)) return;
        if (!cbCheck.isChecked()) {
            Toast.makeText(this, "Vui lòng chấp nhận điều khoản", Toast.LENGTH_SHORT).show();
            return;
        }

        btnDKI.setEnabled(false);
        btnDKI.setText("Đang đăng kí...");

        auth.createUserWithEmailAndPassword(email, passw)
                .addOnCompleteListener(this, task -> {
                    btnDKI.setEnabled(true);
                    btnDKI.setText("Đăng ký");

                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmailAndPassword:success");
                        FirebaseUser user = auth.getCurrentUser();
                        LuuDatabase(user, fullname, SDT);
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        // Thêm log chi tiết lỗi
                        if (task.getException() != null) {
                            Log.e(TAG, "Error details: " + task.getException().getMessage());
                        }
                        String errorTB = getErrorMessage(task.getException());
                        Toast.makeText(DangKiActivity.this, errorTB, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void LuuDatabase(FirebaseUser user, String fullName, String phoneNumber) {
        if (user != null) {
            String userId = user.getUid();
            String userEmail = user.getEmail();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("uid", userId);
            userMap.put("name", fullName);
            userMap.put("email", userEmail);
            userMap.put("phoneNumber", phoneNumber);
            userMap.put("signInMethod", "email");
            userMap.put("createdAt", System.currentTimeMillis());
            userMap.put("isEmailVerified", user.isEmailVerified());

            mDatabase.child("users").child(userId).setValue(userMap)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User data saved successfully");
                        Toast.makeText(DangKiActivity.this, "🎉 Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(() -> {
                            Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
                            startActivity(intent);
                            finish();
                        }, 2000);
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error saving user data", e);
                        Toast.makeText(DangKiActivity.this, "Lỗi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        }
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
    private String getErrorMessage(Exception exception) {
        if (exception == null) return "Đăng ký thất bại";

        String errorCode = exception.getMessage();
        if (errorCode != null) {
            if (errorCode.contains("WEAK_PASSWORD")) {
                return "Mật khẩu quá yếu. Vui lòng chọn mật khẩu mạnh hơn";
            } else if (errorCode.contains("EMAIL_ALREADY_IN_USE")) {
                return "Email này đã được sử dụng";
            } else if (errorCode.contains("INVALID_EMAIL")) {
                return "Email không hợp lệ";
            } else if (errorCode.contains("TOO_MANY_REQUESTS")) {
                return "Quá nhiều yêu cầu. Vui lòng thử lại sau";
            }
        }

        return "Đăng ký thất bại. Vui lòng thử lại";
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
