package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private MaterialCardView QlSanPham, QLDonHang, QlNguoiDung, QLThongKe;
    private ImageView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mAuth = FirebaseAuth.getInstance();
        QlSanPham = findViewById(R.id.cardManageProducts);
        QLDonHang = findViewById(R.id.cardManageOrders);
        QlNguoiDung = findViewById(R.id.cardUserManagement);
        QLThongKe = findViewById(R.id.cardRevenueReport);
        btnProfile = findViewById(R.id.btnProfile);

        View rootView = findViewById(R.id.main);
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets st = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(st.left, st.top, st.right, st.bottom);
                return insets;
            });
        }

        setupClickListeners();
        animation();
    }
    private void animaScale(View v,long delay){
        v.setAlpha(0f);
        v.setScaleX(0.9f);
        v.setScaleY(0.9f);
        v.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(delay)
                .start();
    }
    private void animation(){
        animaScale(QlSanPham,0);
        animaScale(QLDonHang,100);
        animaScale(QlNguoiDung,200);
        animaScale(QLThongKe,300);
    }
    private void setupClickListeners() {
        QlSanPham.setOnClickListener(this);
        QLDonHang.setOnClickListener(this);
        QlNguoiDung.setOnClickListener(this);
        QLThongKe.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.cardManageProducts){
          Intent intent = new Intent(AdminActivity.this, QLSanPhamActivity.class);
          startActivity(intent);
          finish();
        }else if(id == R.id.cardManageOrders){
           Intent intent=new Intent(AdminActivity.this, AdminQLDHActivity.class);
           startActivity(intent);
           finish();
        }else if(id == R.id.cardUserManagement){
            showToast("Quản lý người dùng");
            }else if(id == R.id.cardRevenueReport){
            showToast("Báo cáo doanh thu");
        }else if(id == R.id.btnProfile){
           mAuth.signOut();
           Intent intent = new Intent(AdminActivity.this, MainActivity.class);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
           startActivity(intent);
           finish();
        }
    }
}
