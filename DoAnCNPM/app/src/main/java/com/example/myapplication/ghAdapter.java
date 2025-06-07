package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ghAdapter extends RecyclerView.Adapter<ghAdapter.ViewHolder> {

    private List<SanPham> cartItems;

    public ghAdapter(List<SanPham> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SanPham item = cartItems.get(position);
        holder.tvProductName.setText(item.getTen());
        holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
        holder.tvProductPrice.setText(String.format("%,dđ", item.getGia() * item.getSoLuong()));

        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
                holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
                holder.tvProductPrice.setText(String.format("%,dđ", item.getGia() * item.getSoLuong()));
                ghmanager.getInstance().updateCartItem(item); // Cập nhật giỏ hàng
            }
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            item.setSoLuong(item.getSoLuong() + 1);
            holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
            holder.tvProductPrice.setText(String.format("%,dđ", item.getGia() * item.getSoLuong()));
            ghmanager.getInstance().updateCartItem(item); // Cập nhật giỏ hàng
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName, tvProductPrice, tvQuantity;
        public Button btnDecrease, btnIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.item_name1);
            tvProductPrice = itemView.findViewById(R.id.item_price1);
            tvQuantity = itemView.findViewById(R.id.item_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_Tru);
            btnIncrease = itemView.findViewById(R.id.btn_Tang);
        }
    }
}