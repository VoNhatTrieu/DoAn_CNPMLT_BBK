package com.example.myapplication.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.SanPham;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {
    private Context context;
    private List<SanPham> productList;
    private OnProductActionListener listener;

    public interface OnProductActionListener {
        void onEditProduct(SanPham product);
        void onDeleteProduct(SanPham product);
    }

    public AdminProductAdapter(Context context, List<SanPham> productList, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public void updateData(List<SanPham> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < productList.size()) {
            productList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, productList.size());
        }
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        SanPham product = productList.get(position);

        // Set basic info
        holder.tvProductName.setText(product.getTen() != null ? product.getTen() : "Tên sản phẩm");
        holder.tvProductPrice.setText(String.format("%,dđ", product.getGia()));
        holder.tvProductCategory.setText(String.format("Danh mục: %s",
                product.getCateri() != null ? product.getCateri() : "Chưa phân loại"));
        holder.tvProductQuantity.setText(String.format("Số lượng: %d", product.getSoLuong()));

        // Load image with priority: URL -> Resource -> Default
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ban1)
                    .error(R.drawable.ban1)
                    .into(holder.ivProductImage);
        } else if (product.getAnh() != 0) {
            holder.ivProductImage.setImageResource(product.getAnh());
        } else {
            holder.ivProductImage.setImageResource(R.drawable.ban1);
        }

        // Set click listeners
        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteProduct(product);
            }
        });

        // Optional: Add item click listener for viewing details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditProduct(product); // Or create a separate method for viewing
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class AdminProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice, tvProductCategory, tvProductQuantity;
        ImageView ivProductImage, ivEdit, ivDelete;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}