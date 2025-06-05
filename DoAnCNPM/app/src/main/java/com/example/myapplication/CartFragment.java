package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CartFragment extends Fragment {

    private Toolbar toolbar;
    private Button continueShoppingButton;
    private FloatingActionButton chatButton;

    public CartFragment() {
        // Constructor rỗng bắt buộc
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        continueShoppingButton = view.findViewById(R.id.continue_shopping_button);
        chatButton = view.findViewById(R.id.chat_button);

        // Gán tiêu đề cho Toolbar (nếu cần)
        toolbar.setTitle("Bán bánh kem siu ngon");

        // Nút tiếp tục mua sắm
        continueShoppingButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đi đến trang sản phẩm...", Toast.LENGTH_SHORT).show();
            // TODO: Chuyển về Home hoặc danh sách sản phẩm
        });

        // Nút chat
        chatButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang phát triển tính năng chat!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}