package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Trchitietsp extends AppCompatActivity {
    private ImageView imageViewsp, backIcon;
    private TextView chitit, gia, saodg, tvTitle;
    private SanPham sanPham;
    private int nho = 100000;
    private int trung = 150000;
    private int lon = 235000;
    private RatingBar ratingBar;
    private RadioGroup rgSize;
    private EditText binhluan;
    private Button btnGuiDanhGia;
    private RecyclerView bl;
    private List<xemchitiet> xct;
    private xctAdapter x;
    private Button add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trchitietsp);

        // Khởi tạo các view
        backIcon = findViewById(R.id.back_icon);
        tvTitle = findViewById(R.id.tv_title);
        imageViewsp = findViewById(R.id.ivSP);
        chitit = findViewById(R.id.tvChitietTP);
        gia = findViewById(R.id.tvPrice);
        saodg = findViewById(R.id.saodanhgia);
        ratingBar = findViewById(R.id.rating);
        binhluan = findViewById(R.id.NhapDG);
        btnGuiDanhGia = findViewById(R.id.btnGuiDanhGia);
        bl = findViewById(R.id.rvbl);
        rgSize = findViewById(R.id.rgSize);
        add=findViewById(R.id.themvohang);

        // Thiết lập RecyclerView
        bl.setLayoutManager(new LinearLayoutManager(this));

        // Lấy dữ liệu SanPham từ Intent
        sanPham = (SanPham) getIntent().getSerializableExtra("sanPham");
        if (sanPham == null) {
            Log.e("Trchitietsp", "SanPham is null. Check Intent key or data passing.");
            Toast.makeText(this, "Không thể hiển thị chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Log.d("Trchitietsp", "SanPham received: " + sanPham.getTen());

        // Thiết lập tiêu đề và hình ảnh
        tvTitle.setText(sanPham.getTen());
        imageViewsp.setImageResource(sanPham.getAnh());
        chitit.setText("Kem sữa tươi, dâu tây, đường, bột mì, trứng, vani.");
        gia.setText(String.format("%,dđ", nho));


        // Xử lý sự kiện nhấn nút back
        backIcon.setOnClickListener(v -> {
            Log.d("Trchitietsp", "Back button clicked, returning to previous Activity");
            finish();
        });

        // Chọn size
        rgSize.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.nho) {
                gia.setText(String.format("%,dđ", nho));
            } else if (checkedId == R.id.trung) {
                gia.setText(String.format("%,dđ", trung));
            } else if (checkedId == R.id.lon) {
                gia.setText(String.format("%,dđ", lon));
            }
        });

        // Thêm vào giỏ hàng
        add.setOnClickListener(v -> {
            if(sanPham!=null){
                ghmanager.getInstance().addToCart(sanPham);
                Toast.makeText(this, "Đã thêm " + sanPham.getTen() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this,"Không thể thêm vào giỏ hàng",Toast.LENGTH_SHORT).show();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Danh sách bình luận
        xct = new ArrayList<>();
        xct.add(new xemchitiet(5.0f, "Ngon, không quá ngọt!", "Nguyễn An"));
        x = new xctAdapter(xct);
        bl.setAdapter(x);
        danhgiasao();

        // Xử lý nhấn nút gửi đánh giá
        btnGuiDanhGia.setOnClickListener(v -> {
            String comment = binhluan.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đánh giá...", Toast.LENGTH_SHORT).show();
                return;
            }
            xct.add(new xemchitiet(4.5f, comment, "Người dùng"));
            x.notifyDataSetChanged();
            danhgiasao();
            binhluan.setText("");
            Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();
        });

        // Giữ sự kiện bàn phím (tùy chọn)
        binhluan.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String comment = binhluan.getText().toString().trim();
                if (comment.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đánh giá...", Toast.LENGTH_SHORT).show();
                    return true;
                }
                xct.add(new xemchitiet(4.5f, comment, "Người dùng"));
                x.notifyDataSetChanged();
                danhgiasao();
                binhluan.setText("");
                Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    void danhgiasao() {
        if (xct.isEmpty()) {
            ratingBar.setRating(0);
            saodg.setText("0/5");
            return;
        }
        float avg = 0;
        for (xemchitiet re : xct) {
            avg += re.getRating();
        }
        avg /= xct.size();
        ratingBar.setRating(avg);
        saodg.setText(String.format("%.1f/5", avg));
    }
}