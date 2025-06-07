package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";
    private Toolbar toolbar;
    private FloatingActionButton chatButton;
    private RecyclerView rvCartItems;
    private LinearLayout emptyCartLayout;
    private ghAdapter ghAdapter;
    private  Button thanhtoan;

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
        chatButton = view.findViewById(R.id.chat_button);
        rvCartItems = view.findViewById(R.id.rvCartItems);
        emptyCartLayout = view.findViewById(R.id.empty_cart_layout);
        thanhtoan=view.findViewById(R.id.btnthanhtoan);
        // Gán tiêu đề cho Toolbar
        toolbar.setTitle("Giỏ hàng");

        // Lấy danh sách sản phẩm từ giỏ hàng
        List<SanPham> cartItems = ghmanager.getInstance().getCartItems();
        Log.d(TAG, "Số lượng sản phẩm trong giỏ hàng: " + cartItems.size());

        // Kiểm tra giỏ hàng trống hay không
        if (cartItems.isEmpty()) {
            Log.d(TAG, "Giỏ hàng trống, hiển thị empty_cart_layout");
            emptyCartLayout.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Giỏ hàng có sản phẩm, hiển thị RecyclerView");
            emptyCartLayout.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);

            // Thiết lập RecyclerView
            ghAdapter = new ghAdapter(cartItems);
            rvCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
            rvCartItems.setAdapter(ghAdapter);
        }
        thanhtoan.setOnClickListener(v -> {
            if(!cartItems.isEmpty()){
                Intent intent=new Intent(getActivity(),ThanhToanActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getContext(),"Giỏ hàng trống",Toast.LENGTH_SHORT).show();
            }
        });
        // Nút chat
        chatButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Đang phát triển tính năng chat!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}