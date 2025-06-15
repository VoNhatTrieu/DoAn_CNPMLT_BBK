package com.example.myapplication.Yeuthich;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.SanPham;

import java.util.List;
public class dsytAdapter extends RecyclerView.Adapter<dsytAdapter.WishlistViewHolder>{
    private Context context;
    private List<SanPham> wishlistItems;
    private OnWishlistItemClickListener listener;

    public interface OnWishlistItemClickListener {
        void onItemClick(SanPham sanPham);
        void onRemoveClick(SanPham sanPham);
    }

    public dsytAdapter(Context context, List<SanPham> wishlistItems, OnWishlistItemClickListener listener) {
        this.context = context;
        this.wishlistItems = wishlistItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dsyttt, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        SanPham sanPham = wishlistItems.get(position);

        holder.tvTen.setText(sanPham.getTen());
        holder.tvGia.setText(String.format("%,dđ", sanPham.getGia()));
        holder.ivAnh.setImageResource(sanPham.getAnh());

        // Xử lý click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sanPham);
            }
        });

        // Xử lý click vào nút xóa
        holder.ivRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(sanPham);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;
        ImageView ivAnh, ivRemove;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvGia = itemView.findViewById(R.id.tvGia);
            ivAnh = itemView.findViewById(R.id.ivanh);
            ivRemove = itemView.findViewById(R.id.ivRemove);
        }
    }
}