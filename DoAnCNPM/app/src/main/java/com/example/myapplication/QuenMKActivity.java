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

public class QuenMKActivity extends AppCompatActivity {
    private TextInputEditText etEmail;
    private MaterialButton btnSendReset;
    private TextView tvBackToLogin;
    private ImageView btnBack;

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
        btnSendReset.setOnClickListener(v -> {

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
    private  void ResetEmail(){

        String email=etEmail.getText().toString().trim();
        if(!checkemail(email)){
            return;
        };
        Toast.makeText(this,"Mật khẩu đẵ được gữi đến email",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(QuenMKActivity.this,TaiKhoanFragment.class);
        startActivity(intent);
        finish();
    }
}