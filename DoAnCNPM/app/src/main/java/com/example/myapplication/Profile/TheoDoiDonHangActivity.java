package com.example.myapplication.Profile;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.example.myapplication.R;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TheoDoiDonHangActivity extends AppCompatActivity {

    private ImageView icBack;
    private TextView tvOrderId, tvOrderStatus, tvCurrentStep;


    private View step1Circle, step2Circle, step3Circle, step4Circle;
    private TextView step1Text, step2Text, step3Text, step4Text;
    private TextView step1Date, step2Date, step3Date, step4Date;
    private View progressLine1, progressLine2, progressLine3;


    private TextView tvEstimatedDelivery, tvShippingAddress, tvContactInfo;

    private lsdh_order order;
    private FirebaseFirestore db;
    private ListenerRegistration orderListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theo_doi_don_hang);

        icBack = findViewById(R.id.ic_back);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvCurrentStep = findViewById(R.id.tv_current_step);
        step1Circle = findViewById(R.id.step_1_circle);
        step2Circle = findViewById(R.id.step_2_circle);
        step3Circle = findViewById(R.id.step_3_circle);
        step4Circle = findViewById(R.id.step_4_circle);
        step1Text = findViewById(R.id.step_1_text);
        step2Text = findViewById(R.id.step_2_text);
        step3Text = findViewById(R.id.step_3_text);
        step4Text = findViewById(R.id.step_4_text);
        step1Date = findViewById(R.id.step_1_date);
        step2Date = findViewById(R.id.step_2_date);
        step3Date = findViewById(R.id.step_3_date);
        step4Date = findViewById(R.id.step_4_date);
        progressLine1 = findViewById(R.id.progress_line_1);
        progressLine2 = findViewById(R.id.progress_line_2);
        progressLine3 = findViewById(R.id.progress_line_3);

        tvEstimatedDelivery = findViewById(R.id.tv_estimated_delivery);
        tvShippingAddress = findViewById(R.id.tv_shipping_address);
        tvContactInfo = findViewById(R.id.tv_contact_info);

        icBack.setOnClickListener(v -> finish());
        db = FirebaseFirestore.getInstance();
        getOrderIntent();
    }
    private void getOrderIntent() {
        order = (lsdh_order) getIntent().getSerializableExtra("order");
        if (order == null) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void setup(){
        if(order!=null)return;
        DocumentReference orderRef = db.collection("orders").document(order.getOrderId());
            orderListener = orderRef.addSnapshotListener((snapshot, error) -> {
                if (error != null) {
                    Toast.makeText(this, "Lỗi theo dõi đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                   lsdh_order updatedOrder = snapshot.toObject(lsdh_order.class);
                    if (updatedOrder != null) {
                        order = updatedOrder;
                        updatetracking();
                    }
                }
            });
    }

    private void displayOrderInfo() {
        if (order == null) return;

        tvOrderId.setText("#" + order.getOrderId());
        tvOrderStatus.setText(getStatusText(order.getStatus()));

        // Địa chỉ giao hàng
        String address = order.getReceiverName() + "\n" +
                order.getAddress() + "\n" +
                "SĐT: " + order.getPhoneNumber();
        tvShippingAddress.setText(address);


        tvContactInfo.setText("Hotline: 1900-1234\nEmail: support@example.com");


        tvEstimatedDelivery.setText("3-5 ngày làm việc");

        updatetracking();
    }
    private void updatetracking() {
        if (order == null) return;

        String status = order.getStatus();
        tvCurrentStep.setText("Trạng thái hiện tại: " + getStatusText(status));

        // Reset tất cả về trạng thái mặc định
        resetAllSteps();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());
        String currentDate = order.getCreatedAt() != null ? sdf.format(order.getCreatedAt()) : "";

        switch (status) {
            case "Pending":
                updateStep(1, true, "#4CAF50", "Đã đặt hàng", currentDate);
                tvCurrentStep.setText("Đơn hàng đang chờ xác nhận");
                break;

            case "Processing":
                updateStep(1, true, "#4CAF50", "Đã xác nhận", currentDate);
                updateStep(2, true, "#FF9800", "Đang chuẩn bị", "");
                updateProgressLine(1, true);
                tvCurrentStep.setText("Đang chuẩn bị hàng hóa");
                break;

            case "Shipped":
                updateStep(1, true, "#4CAF50", "Đã xác nhận", currentDate);
                updateStep(2, true, "#4CAF50", "Đã chuẩn bị", "");
                updateStep(3, true, "#2196F3", "Đang giao hàng", "");
                updateProgressLine(1, true);
                updateProgressLine(2, true);
                tvCurrentStep.setText("Đơn hàng đang được giao đến bạn");
                break;

            case "Delivered":
                updateStep(1, true, "#4CAF50", "Đã xác nhận", currentDate);
                updateStep(2, true, "#4CAF50", "Đã chuẩn bị", "");
                updateStep(3, true, "#4CAF50", "Đã giao hàng", "");
                updateStep(4, true, "#4CAF50", "Đã hoàn thành", "");
                updateProgressLine(1, true);
                updateProgressLine(2, true);
                updateProgressLine(3, true);
                tvCurrentStep.setText("Đơn hàng đã được giao thành công");
                break;

            case "Cancelled":
                updateStep(1, true, "#F44336", "Đã hủy", currentDate);
                tvCurrentStep.setText("Đơn hàng đã được hủy");
                break;
        }
    }
    private void resetAllSteps() {
        updateStep(1, false, "#E0E0E0", "Đặt hàng", "");
        updateStep(2, false, "#E0E0E0", "Chuẩn bị", "");
        updateStep(3, false, "#E0E0E0", "Giao hàng", "");
        updateStep(4, false, "#E0E0E0", "Hoàn thành", "");

        updateProgressLine(1, false);
        updateProgressLine(2, false);
        updateProgressLine(3, false);
    }
    private void updateStep(int stepNumber, boolean completed, String color, String text, String date) {
        View circle;
        TextView stepText, stepDate;

        switch (stepNumber) {
            case 1:
                circle = step1Circle;
                stepText = step1Text;
                stepDate = step1Date;
                break;
            case 2:
                circle = step2Circle;
                stepText = step2Text;
                stepDate = step2Date;
                break;
            case 3:
                circle = step3Circle;
                stepText = step3Text;
                stepDate = step3Date;
                break;
            case 4:
                circle = step4Circle;
                stepText = step4Text;
                stepDate = step4Date;
                break;
            default:
                return;
        }

        // Cập nhật circle
        circle.setBackground(ContextCompat.getDrawable(this,
                completed ? R.drawable.step_completed_circle : R.drawable.step_pending_circle));

        //
        stepText.setText(text);
        stepText.setTextColor(android.graphics.Color.parseColor(color));
        stepText.setTypeface(null, completed ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);

       //ngày
        stepDate.setText(date);
        stepDate.setVisibility(date.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void updateProgressLine(int lineNumber, boolean completed) {
        View line;
        switch (lineNumber) {
            case 1: line = progressLine1; break;
            case 2: line = progressLine2; break;
            case 3: line = progressLine3; break;
            default: return;
        }

        line.setBackgroundColor(android.graphics.Color.parseColor(
                completed ? "#4CAF50" : "#E0E0E0"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}