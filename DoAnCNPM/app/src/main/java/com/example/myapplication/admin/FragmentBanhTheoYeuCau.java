package com.example.myapplication.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FragmentBanhTheoYeuCau extends Fragment {
    private RecyclerView recyclerView;
    private CustomOrderAdapter adapter; // Thay đổi từ OrderAdapter sang CustomOrderAdapter
    private List<OrderModel> banhYCList; // Thay đổi từ DonHang sang OrderModel
    private CollectionReference customOrdersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_banh_theo_yeu_cau, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_custom);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        banhYCList = new ArrayList<>();
        adapter = new CustomOrderAdapter(banhYCList, getContext()); // Khởi tạo CustomOrderAdapter
        recyclerView.setAdapter(adapter);

        customOrdersRef = FirebaseFirestore.getInstance().collection("custom_orders");

        taiDanhSach();

        return view;
    }

    private void taiDanhSach() {
        customOrdersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                banhYCList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    OrderModel order = doc.toObject(OrderModel.class);
                    order.setMaDonHang(doc.getId());

                    // Thêm debug log để kiểm tra dữ liệu ảnh
                    if (doc.get("linkAnhMau") != null) {
                        Log.d("IMAGE_DEBUG", "Image URL for order " + doc.getId() + ": " + doc.getString("linkAnhMau"));
                        order.setLinkAnhMau(doc.getString("linkAnhMau"));
                    } else {
                        Log.d("IMAGE_DEBUG", "No image for order " + doc.getId());
                    }

                    banhYCList.add(order);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    public static FragmentBanhTheoYeuCau newInstance(boolean isCustom) {
        FragmentBanhTheoYeuCau fragment = new FragmentBanhTheoYeuCau();
        Bundle args = new Bundle();
        args.putBoolean("isCustom", isCustom);
        fragment.setArguments(args);
        return fragment;
    }
    // Cập nhật phương thức filterByStatus để làm việc với OrderModel
    public void filterByStatus(String status) {
        if (status.equals("Tất cả")) {
            taiDanhSach();
            return;
        }

        String firestoreStatus = convertToFirestoreStatus(status);
        customOrdersRef
                .whereEqualTo("status", firestoreStatus)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        banhYCList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            order.setMaDonHang(doc.getId());
                            banhYCList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi lọc theo trạng thái", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertToFirestoreStatus(String displayStatus) {
        switch (displayStatus) {
            case "Chờ xử lý": return "cho_xu_ly";
            case "Đang làm": return "dang_lam";
            case "Hoàn tất": return "hoan_tat";
            case "Đã hủy": return "huy";
            case "Chờ báo giá": return "Chờ báo giá";
            case "Đã báo giá": return "Đã báo giá";
            default: return "";
        }
    }

    public CustomOrderAdapter getAdapter() { // Thay đổi kiểu trả về
        return adapter;
    }

    public void refreshData() {
        taiDanhSach();
    }
}