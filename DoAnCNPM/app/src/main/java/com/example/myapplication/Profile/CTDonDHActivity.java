package com.example.myapplication.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class CTDonDHActivity extends AppCompatActivity {
    private ImageView icBack;
    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvReceiverName, tvAddress, tvPhone;
    private TextView tvPaymentMethod, tvTotalAmount, tvDepositAmount, tvRemainingAmount;
    private RecyclerView rvProducts;
    private Button btnCancelOrder, btnContactSupport;

    private lsdh_order order;
    private OrderProductAdapter productAdapter;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctdon_dhactivity);
        db=FirebaseFirestore.getInstance();

        icBack = findViewById(R.id.ic_back);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvReceiverName = findViewById(R.id.tv_receiver_name);
        tvAddress = findViewById(R.id.tv_address);
        tvPhone = findViewById(R.id.tv_phone);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvDepositAmount = findViewById(R.id.tv_deposit_amount);
        tvRemainingAmount = findViewById(R.id.tv_remaining_amount);
        rvProducts = findViewById(R.id.rv_products);
        btnCancelOrder = findViewById(R.id.btn_cancel_order);
        btnContactSupport = findViewById(R.id.btn_contact_support);

        getOderitem();
    setupClickListeners();
    setupRecyclerView();

    }
    private void getOderitem() {
        String orderId = getIntent().getStringExtra("orderId"); // gửi dưới dạng String
        if (orderId == null) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        order = documentSnapshot.toObject(lsdh_order.class);
                        displayOrderDetails(); // sau khi lấy thành công thì hiển thị
                        setupRecyclerView();
                        setupClickListeners();
                    } else {
                        Toast.makeText(this, "Đơn hàng không tồn tại", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void setupRecyclerView() {
        if (order != null && order.getProducts() != null) {
            productAdapter = new OrderProductAdapter(this, order.getProducts());
            rvProducts.setLayoutManager(new LinearLayoutManager(this));
            rvProducts.setAdapter(productAdapter);
        }
    }

    private void displayOrderDetails() {
        if (order == null) return;

        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        // Thông tin cơ bản
        tvOrderId.setText("#" + order.getOrderId());

        if (order.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            tvOrderDate.setText(sdf.format(order.getCreatedAt()));
        } else {
            tvOrderDate.setText("N/A");
        }

        tvOrderStatus.setText(getStatusText(order.getStatus()));
        updateStatusStyle(tvOrderStatus, order.getStatus());

        // Thông tin người nhận
        tvReceiverName.setText(order.getReceiverName() != null ? order.getReceiverName() : "N/A");
        tvAddress.setText(order.getAddress() != null ? order.getAddress() : "N/A");
        tvPhone.setText(order.getPhoneNumber() != null ? order.getPhoneNumber() : "N/A");

        // Thông tin thanh toán
        tvPaymentMethod.setText(order.getPaymentMethod() != null ? order.getPaymentMethod() : "N/A");
        tvTotalAmount.setText(formatter.format(order.getTotalAmount()) + "đ");
        tvDepositAmount.setText(formatter.format(order.getDepositAmount()) + "đ");
        tvRemainingAmount.setText(formatter.format(order.getRemainingAmount()) + "đ");

        // Hiển thị nút hủy chỉ cho đơn hàng Pending
        if ("Pending".equals(order.getStatus())) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        } else {
            btnCancelOrder.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        icBack.setOnClickListener(v -> finish());

        btnCancelOrder.setOnClickListener(v -> showCancelOrderDialog());

        btnContactSupport.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng liên hệ hỗ trợ đang được phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void showCancelOrderDialog() {
        if (order == null) return;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?\n\nNếu hủy bạn sẽ mất 50% số tiền đặt cọc.")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelOrder())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelOrder() {
        if (order == null) return;

        db.collection("orders").document(order.getOrderId())
                .update("status", "Cancelled")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đơn hàng đã được hủy", Toast.LENGTH_SHORT).show();
                    order.setStatus("Cancelled");
                    displayOrderDetails(); // Cập nhật lại UI
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi hủy đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getStatusText(String status) {
        switch (status) {
            case "Pending": return "Đang chờ xử lý";
            case "Processing": return "Đang xử lý";
            case "Shipped": return "Đang giao";
            case "Delivered": return "Đã giao";
            case "Cancelled": return "Đã hủy";
            default: return status;
        }
    }

    private void updateStatusStyle(TextView statusView, String status) {
        int backgroundRes;

        switch (status) {
            case "Pending":
                backgroundRes = R.drawable.status_background_pending;
                break;
            case "Processing":
                backgroundRes = R.drawable.status_background_processing;
                break;
            case "Shipped":
                backgroundRes = R.drawable.status_background_shipped;
                break;
            case "Delivered":
                backgroundRes = R.drawable.status_background_delivered;
                break;
            case "Cancelled":
                backgroundRes = R.drawable.status_background_cancelled;
                break;
            default:
                backgroundRes = R.drawable.status_background_pending;
        }

        statusView.setBackgroundResource(backgroundRes);
    }
}