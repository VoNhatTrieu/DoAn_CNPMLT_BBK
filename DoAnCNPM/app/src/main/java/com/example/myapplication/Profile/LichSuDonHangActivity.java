package com.example.myapplication.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LichSuDonHangActivity extends AppCompatActivity {
    private RecyclerView rvOrders;
    private ImageView icback;
    private Order_lsdhAdapter orderAdapter;
    private List<lsdh_order> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration orderListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_don_hang);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        icback=findViewById(R.id.icback);
        rvOrders = findViewById(R.id.rv_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderList=new ArrayList<>();
        orderAdapter=new Order_lsdhAdapter(this,orderList,this::huyDonHang);
        rvOrders.setAdapter(orderAdapter);

        icback.setOnClickListener(v -> {finish();});
        if(mAuth.getCurrentUser()!=null){
            TaiDonHang();
        }
        else{
            Toast.makeText(this,"Vui lòng đăng nhập",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private void TaiDonHang(){
        String userId = mAuth.getCurrentUser().getUid();
        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show(); // kiểm tra

        orderListener = db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải đơn hàng: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderList.clear();
                    if (snapshot != null) {
                        Toast.makeText(this, "Tìm thấy " + snapshot.size() + " đơn", Toast.LENGTH_SHORT).show(); // kiểm tra
                        for (lsdh_order order : snapshot.toObjects(lsdh_order.class)) {
                            orderList.add(order);
                        }
                    } else {
                        Toast.makeText(this, "Snapshot null", Toast.LENGTH_SHORT).show();
                    }
                    orderAdapter.notifyDataSetChanged();
                });
    }
    private void huyDonHang(lsdh_order order) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn hàng")
                .setMessage(String.format("Nếu hủy bạn sẽ mất 50%% số tiền đặt cọc đơn hàng này. Xác nhận hủy?"))
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    db.collection("orders").document(order.getOrderId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đơn hàng đã được hủy và xóa khỏi lịch sử", Toast.LENGTH_SHORT).show();
                                orderList.remove(order); // Cập nhật danh sách
                                orderAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Lỗi khi xóa đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Không", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (orderListener != null) {
            orderListener.remove();
        }
    }
}