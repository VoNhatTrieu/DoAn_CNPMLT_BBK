package com.example.myapplication.admin;

import android.os.Bundle;
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

public class Fragment_DonHangThuong extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private List<DonHang> donHangList;
    private CollectionReference ordersRef;

    public static Fragment_DonHangThuong newInstance(boolean isCustom) {
        return new Fragment_DonHangThuong();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__don_hang_thuong, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_donhang);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        donHangList = new ArrayList<>();
        adapter = new OrderAdapter(donHangList);
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseFirestore.getInstance().collection("don_hang");

        taiDanhSachDonHang();

        return view;
    }

    private void taiDanhSachDonHang() {
        ordersRef.whereEqualTo("laBanhTheoYeuCau", false).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                donHangList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    DonHang order = doc.toObject(DonHang.class);
                    donHangList.add(order);
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Lỗi tải danh sách đơn hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void filterByStatus(String status) {
        if (status.equals("Tất cả")) {
            taiDanhSachDonHang();
            return;
        }

        String firestoreStatus = convertToFirestoreStatus(status);
        ordersRef.whereEqualTo("laBanhTheoYeuCau", false)
                .whereEqualTo("trangThai", firestoreStatus)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        donHangList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            DonHang order = doc.toObject(DonHang.class);
                            donHangList.add(order);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Lỗi lọc trạng thái đơn", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String convertToFirestoreStatus(String displayStatus) {
        switch (displayStatus) {
            case "Chờ xử lý": return "cho_xu_ly";
            case "Đang làm": return "dang_lam";
            case "Hoàn tất": return "hoan_tat";
            case "Đã hủy": return "huy";
            case "Theo yêu cầu": return "theo_yeu_cau";
            default: return "";
        }
    }

    public OrderAdapter getAdapter() {
        return adapter;
    }

    public void refreshData() {
        taiDanhSachDonHang();
    }
}