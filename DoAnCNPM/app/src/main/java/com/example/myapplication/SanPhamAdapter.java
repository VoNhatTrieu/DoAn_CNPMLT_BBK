package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.ProductViewHolder> {
    private Context context;
    private List<SanPham> sanPhamList;
    private OnItemClickListener listener; // Callback interface

    // Interface để xử lý sự kiện nhấn
    public interface OnItemClickListener {
        void onItemClick(SanPham sanPham);
    }

    public SanPhamAdapter(Context context, List<SanPham> sanPhamList, OnItemClickListener listener) {
        this.context = context;
        this.sanPhamList = sanPhamList;
        this.listener = listener;
    }

    public void updateData(List<SanPham> newList) {
        this.sanPhamList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_san_pham_adapter, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SanPham sp = sanPhamList.get(position);
        holder.tvTen.setText(sp.getTen());
        holder.tvGia.setText(String.format("%,dđ", sp.getGia()));
        holder.ivAnh.setImageResource(sp.getAnh()); // Sửa lỗi sp.get()

        // Xử lý sự kiện nhấn qua callback
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sp);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;
        ImageView ivAnh;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvGia = itemView.findViewById(R.id.tvGia);
            ivAnh = itemView.findViewById(R.id.ivanh);
        }
    }
}