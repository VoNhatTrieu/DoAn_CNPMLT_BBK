package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Yeuthich.QLYT;

import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.ProductViewHolder> {
    private Context context;
    private List<SanPham> sanPhamList;
    private OnItemClickListener listener;

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
        holder.ivAnh.setImageResource(sp.getAnh());

        // Cập nhật trạng thái icon yêu thích
        updateWishlistIcon(holder.ivWishlist, sp);

        // Xử lý click vào sản phẩm
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(sp);
            }
        });

        // Xử lý click vào nút yêu thích
        holder.ivWishlist.setOnClickListener(v -> {
            QLYT tim = QLYT.getInstance();

            if (tim.isInWishlist(sp)) {
                // Nếu đã có trong wishlist, xóa khỏi wishlist
                tim.removeFromWishlist(sp);
                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            } else {
                // Nếu chưa có trong wishlist, thêm vào wishlist
                tim.addToWishlist(sp);
                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }

            // Cập nhật icon
            updateWishlistIcon(holder.ivWishlist, sp);
        });
    }

    private void updateWishlistIcon(ImageView ivWishlist, SanPham sanPham) {
        if (QLYT.getInstance().isInWishlist(sanPham)) {
            ivWishlist.setImageResource(R.drawable.baseline_favorite_border_24); // Tim đỏ
        } else {
            ivWishlist.setImageResource(R.drawable.baseline_favorite_border_24); // Tim rỗng
        }
    }

    @Override
    public int getItemCount() {
        return sanPhamList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTen, tvGia;
        ImageView ivAnh, ivWishlist;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvGia = itemView.findViewById(R.id.tvGia);
            ivAnh = itemView.findViewById(R.id.ivanh);
            ivWishlist = itemView.findViewById(R.id.ivWishlist);
        }
    }
}