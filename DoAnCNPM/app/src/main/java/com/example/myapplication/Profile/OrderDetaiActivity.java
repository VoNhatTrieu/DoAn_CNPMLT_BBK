package com.example.myapplication.Profile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.admin.OrderModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.util.Locale;

public class OrderDetaiActivity extends AppCompatActivity {
    private static final String TAG = "OrderDetaiActivity";

    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private LinearLayout layoutContent;
    private TextView tvEmptyState;

    // Order info views
    private TextView tvOrderId, tvCustomerName, tvPhone, tvCakeType, tvCakeName;
    private TextView tvDeliveryDate, tvDescription, tvStatus, tvQuotePrice;
    private TextView tvMaterialCost, tvQuoteInfo;
    private ImageView ivCakeImage;
    private CardView cardQuoteInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detai);

        initViews();
        initFirebase();

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadOrderDetails(orderId);
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết đơn hàng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());


        // Order info views
        tvOrderId = findViewById(R.id.tv_order_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvPhone = findViewById(R.id.tv_phone);
        tvCakeType = findViewById(R.id.tv_cake_type);
        tvCakeName = findViewById(R.id.tv_cake_name);
        tvDeliveryDate = findViewById(R.id.tv_delivery_date);
        tvDescription = findViewById(R.id.tv_description);
        tvStatus = findViewById(R.id.tv_status);
        tvQuotePrice = findViewById(R.id.tv_quote_price);
        tvMaterialCost = findViewById(R.id.tv_material_cost);
        tvQuoteInfo = findViewById(R.id.tv_quote_info);
        ivCakeImage = findViewById(R.id.iv_cake_image);
        cardQuoteInfo = findViewById(R.id.card_quote_info);
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadOrderDetails(String orderId) {
        showLoading(true);

        db.collection("custom_orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(this::handleOrderSuccess)
                .addOnFailureListener(this::handleOrderFailure);
    }

    private void handleOrderSuccess(DocumentSnapshot snapshot) {
        showLoading(false);

        if (!snapshot.exists()) {
            showEmptyState("Đơn hàng không tồn tại");
            return;
        }

        try {
            OrderModel order = snapshot.toObject(OrderModel.class);
            if (order != null) {
                displayOrderDetails(order);
                showContent(true);
            } else {
                showEmptyState("Lỗi khi đọc dữ liệu đơn hàng");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing order data: ", e);
            showEmptyState("Lỗi khi xử lý dữ liệu đơn hàng");
        }
    }

    private void handleOrderFailure(Exception e) {
        showLoading(false);
        Log.e(TAG, "Error loading order: ", e);
        showEmptyState("Lỗi khi tải đơn hàng: " + e.getMessage());
    }

    private void displayOrderDetails(OrderModel order) {
        // Basic order info
        tvOrderId.setText(safe(order.getMaDonHang()));
        tvCustomerName.setText(safe(order.getNguoiNhan()));
        tvPhone.setText(safe(order.getSdt()));
        tvCakeType.setText(safe(order.getLoaiBanh()));
        tvCakeName.setText(safe(order.getTenBanh()));
        tvDeliveryDate.setText(safe(order.getNgayGiao()));
        tvDescription.setText(safe(order.getMoTa()));

        // Status with color
        String status = order.getStatus() != null ? order.getStatus() : "Chờ xác nhận";
        tvStatus.setText(status);
        tvStatus.setTextColor(getStatusColor(status));

        // Quote information
        if (order.getGiaBaoGia() > 0) {
            cardQuoteInfo.setVisibility(View.VISIBLE);
            NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
            tvQuotePrice.setText(formatter.format(order.getGiaBaoGia()) + " đ");
            tvMaterialCost.setText(formatter.format(order.getChiPhiNguyenLieu()) + " đ");
            tvQuoteInfo.setText(safe(order.getThongTinBaoGia()));
        } else {
            cardQuoteInfo.setVisibility(View.GONE);
        }

        // Load image
        loadCakeImage(order.getLinkAnhMau());
    }

    private void loadCakeImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ivCakeImage.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(ivCakeImage);
        } else {
            ivCakeImage.setVisibility(View.GONE);
        }
    }

    private String safe(String text) {
        return (text != null && !text.isEmpty()) ? text : "Chưa có thông tin";
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Chờ xác nhận":
            case "Chờ báo giá":
                return getColor(R.color.status_pending);
            case "Đã báo giá":
                return getColor(R.color.status_quoted);
            case "Đang làm":
                return getColor(R.color.status_processing);
            case "Hoàn tất":
                return getColor(R.color.status_completed);
            case "Đã hủy":
                return getColor(R.color.status_cancelled);
            default:
                return getColor(android.R.color.black);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showContent(boolean show) {
        layoutContent.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState(String message) {
        tvEmptyState.setText(message);
        tvEmptyState.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.GONE);
    }
}