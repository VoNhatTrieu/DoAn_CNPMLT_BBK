package com.example.myapplication.Profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.myapplication.R;

public class DoiMatKhauActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mat_khau);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập trước khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivBack = findViewById(R.id.iv_back);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnChangePassword = findViewById(R.id.btn_change_password);

        ivBack.setOnClickListener(v -> finish());
        btnChangePassword.setOnClickListener(v -> {
            if (validateInputs()) {
                changePassword();
            }
        });

        setupProgressDialog();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đổi mật khẩu...");
        progressDialog.setCancelable(false);
    }

    private void changePassword() {
        String currentPw = etCurrentPassword.getText().toString().trim();
        String newPw = etNewPassword.getText().toString().trim();
        String userEmail = currentUser.getEmail();

        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập trước khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPw);
        currentUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updatePassword(newPw);
            } else {
                progressDialog.dismiss();
                etCurrentPassword.setError("Mật khẩu hiện tại không đúng");
                etCurrentPassword.requestFocus();
                Toast.makeText(DoiMatKhauActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String newPw) {
        currentUser.updatePassword(newPw).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(DoiMatKhauActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                clearFields();
                finish();
            } else {
                String error = "Đổi mật khẩu thất bại";
                if (task.getException() != null) {
                    error = task.getException().getMessage();
                }
                Toast.makeText(DoiMatKhauActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            etCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            etCurrentPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword)) {
            etNewPassword.setError("Vui lòng nhập mật khẩu mới");
            etNewPassword.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Mật khẩu mới phải có ít nhất 6 ký tự");
            etNewPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (newPassword.equals(currentPassword)) {
            etNewPassword.setError("Mật khẩu mới phải khác mật khẩu hiện tại");
            etNewPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        etCurrentPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
