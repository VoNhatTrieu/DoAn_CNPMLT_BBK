package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ghAdapter extends RecyclerView.Adapter<ghAdapter.ViewHolder> {

    private List<SanPham> cartItems;
    private TbGhthaydoi listerner;
    public ghAdapter(List<SanPham> cartItems) {
        this.cartItems = cartItems;
    }
    public interface TbGhthaydoi{
        void updateGH();
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
        holder.ivImage.setImageResource(item.getAnh());
        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getSoLuong() > 1) {
                item.setSoLuong(item.getSoLuong() - 1);
                holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
                holder.tvProductPrice.setText(String.format("%,dđ", item.getGia() * item.getSoLuong()));
                ghmanager.getInstance().updateCartItem(item); // Cập nhật giỏ hàng
                if(listerner!=null){
                    listerner.updateGH();
                }
            }
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            item.setSoLuong(item.getSoLuong() + 1);
            holder.tvQuantity.setText(String.valueOf(item.getSoLuong()));
            holder.tvProductPrice.setText(String.format("%,dđ", item.getGia() * item.getSoLuong()));
            ghmanager.getInstance().updateCartItem(item); // Cập nhật giỏ hàng
            if(listerner!=null){
                listerner.updateGH();
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName, tvProductPrice, tvQuantity;
        public Button btnDecrease, btnIncrease;
        public ImageView ivImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage=itemView.findViewById(R.id.item_image1);
            tvProductName = itemView.findViewById(R.id.item_name1);
            tvProductPrice = itemView.findViewById(R.id.item_price1);
            tvQuantity = itemView.findViewById(R.id.item_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_Tru);
            btnIncrease = itemView.findViewById(R.id.btn_Tang);
        }
    }
}