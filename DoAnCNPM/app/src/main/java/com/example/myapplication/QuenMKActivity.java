package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class QuenMKActivity extends AppCompatActivity {
    private TextInputEditText etEmail;
    private MaterialButton btnSendReset;
    private TextView tvBackToLogin;
    private ImageView btnBack;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quen_mkactivity);
        etEmail=findViewById(R.id.etEmail);
        btnSendReset=findViewById(R.id.btnSendReset);
        tvBackToLogin=findViewById(R.id.tvBackToLogin);
        btnBack=findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        tvBackToLogin.setOnClickListener(v -> {
            Intent intent=new Intent(QuenMKActivity.this,DangKiActivity.class);
            startActivity(intent);
            finish();
        });
        auth=FirebaseAuth.getInstance();
        btnSendReset.setOnClickListener(v -> {
            String Email=etEmail.getText().toString().trim();
            if(!checkemail(Email)) return;
            auth.sendPasswordResetEmail(Email).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(QuenMKActivity.this,"Mật khẩu đẵ được gữi đến email",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(QuenMKActivity.this,DangNhapActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(QuenMKActivity.this,"Lỗi",Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
    private boolean checkemail(String email){
        if(TextUtils.isEmpty(email)){
            etEmail.setError("Vui lòng nhập đúng email");
            etEmail.requestFocus();
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError("Vui lòng nhập đúng email");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

}