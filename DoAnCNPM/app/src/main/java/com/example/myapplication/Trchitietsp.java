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

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trchitietsp);

        // Ánh xạ View
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
        add = findViewById(R.id.themvohang);

        bl.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();

        // Lấy thông tin từ Intent
        String productId = getIntent().getStringExtra("productId");
        SanPham sanPhamFromIntent = (SanPham) getIntent().getSerializableExtra("sanPham");

        // Ưu tiên productId từ Firestore
        if (productId != null && !productId.isEmpty()) {
            loadProductFromFirestore(productId);
        } else if (sanPhamFromIntent != null) {
            // Fallback: sử dụng object truyền trực tiếp
            sanPham = sanPhamFromIntent;
            setupUI(sanPham);
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupCommonUI();
    }

    private void loadProductFromFirestore(String productId) {
        db.collection("sanpham").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        sanPham = documentSnapshot.toObject(SanPham.class);
                        if (sanPham != null) {
                            setupUI(sanPham);
                        } else {
                            Toast.makeText(this, "Lỗi đọc dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tải sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Trchitietsp", "Firestore error: " + e.getMessage());
                    finish();
                });
    }

    private void setupCommonUI() {
        // Quay lại
        backIcon.setOnClickListener(v -> finish());

        // Danh sách đánh giá mẫu
        xct = new ArrayList<>();
        xct.add(new xemchitiet(5.0f, "Ngon, không quá ngọt!", "Nguyễn An"));
        xct.add(new xemchitiet(4.5f, "Bánh rất thơm và đẹp mắt", "Trần Minh"));
        xct.add(new xemchitiet(4.8f, "Chất lượng tuyệt vời!", "Lê Hoa"));

        x = new xctAdapter(xct);
        bl.setAdapter(x);
        danhgiasao();

        // Gửi đánh giá
        btnGuiDanhGia.setOnClickListener(v -> {
            String comment = binhluan.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đánh giá...", Toast.LENGTH_SHORT).show();
                return;
            }

            float rating = ratingBar.getRating();
            if (rating == 0) {
                rating = 4.5f; // Giá trị mặc định
            }

            xct.add(new xemchitiet(rating, comment, "Người dùng"));
            x.notifyDataSetChanged();
            danhgiasao();
            binhluan.setText("");
            ratingBar.setRating(0);
            Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();
        });

        binhluan.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String comment = binhluan.getText().toString().trim();
                if (!comment.isEmpty()) {
                    float rating = ratingBar.getRating();
                    if (rating == 0) {
                        rating = 4.5f;
                    }
                    xct.add(new xemchitiet(rating, comment, "Người dùng"));
                    x.notifyDataSetChanged();
                    danhgiasao();
                    binhluan.setText("");
                    ratingBar.setRating(0);
                    Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupUI(SanPham sp) {
        if (sp == null) return;

        // Hiển thị thông tin cơ bản
        tvTitle.setText(sp.getTen() != null ? sp.getTen() : "Tên sản phẩm");

        // Hiển thị mô tả nếu có, ngược lại hiển thị thông tin mặc định
        String moTa = sp.getMota();
        if (moTa != null && !moTa.trim().isEmpty()) {
            chitit.setText(moTa);
        } else {
            chitit.setText("Bánh ngon được làm từ những nguyên liệu tươi ngon, chất lượng cao. Hương vị thơm ngon, hấp dẫn.");
        }

        // Tính giá theo size dựa vào giá gốc từ Firestore
        nho = sp.getGia();
        trung = (int) (nho * 1.25);
        lon = (int) (nho * 1.5);
        gia.setText(String.format("%,dđ", nho)); // Mặc định là size nhỏ

        // Load ảnh: Ưu tiên URL từ Firestore, fallback về resource
        if (sp.getImageUrl() != null && !sp.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(sp.getImageUrl())
                    .placeholder(R.drawable.ban1) // Placeholder khi đang load
                    .error(R.drawable.ban1) // Ảnh lỗi
                    .into(imageViewsp);
        } else if (sp.getAnh() != 0) {
            imageViewsp.setImageResource(sp.getAnh());
        } else {
            imageViewsp.setImageResource(R.drawable.ban1); // Ảnh mặc định
        }

        // Bắt sự kiện size sau khi đã biết giá
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
            // Tạo bản sao với giá đã được điều chỉnh theo size
            SanPham productToAdd = new SanPham(sp.getTen(), getCurrentPrice(), sp.getAnh(), sp.getCateri());
            if (sp.getImageUrl() != null) {
                productToAdd.setImageUrl(sp.getImageUrl());
            }
            if (sp.getMota() != null) {
                productToAdd.setMota(sp.getMota());
            }

            ghmanager.getInstance().addToCart(productToAdd);
            Toast.makeText(this, "Đã thêm " + sp.getTen() + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private int getCurrentPrice() {
        int checkedId = rgSize.getCheckedRadioButtonId();
        if (checkedId == R.id.trung) {
            return trung;
        } else if (checkedId == R.id.lon) {
            return lon;
        } else {
            return nho; // Mặc định là size nhỏ
        }
    }

    private void danhgiasao() {
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

        // Cập nhật rating bar hiển thị (không cho phép user thay đổi)
        ratingBar.setRating(avg);
        ratingBar.setIsIndicator(false); // Cho phép user đánh giá

        saodg.setText(String.format("%.1f/5 (%d đánh giá)", avg, xct.size()));
    }
}