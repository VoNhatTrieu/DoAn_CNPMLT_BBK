package com.example.myapplication;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.SanPham;

import java.util.List;

public class SanPhamAdapter extends RecyclerView.Adapter<SanPhamAdapter.ProductViewHolder> {

    private Context context;
    private List<SanPham> productList;

    public SanPhamAdapter(Context context,List<SanPham> sanPhamList) {
        this.productList = sanPhamList;
        this.context=context;
    }
    public void updateData(List<SanPham> newList) {
        productList = newList;
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
        SanPham sp = productList.get(position);
        holder.ten.setText(sp.getTen());
        holder.gia.setText(sp.getGia() + "đ");
        holder.img.setImageResource(sp.getAnh());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView ten, gia;
        ImageView img;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ten = itemView.findViewById(R.id.tvTen);
            gia = itemView.findViewById(R.id.tvGia);
            img = itemView.findViewById(R.id.ivanh);
        }
    }
}