package com.example.myapplication.admin;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.admin.TopProduct;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.TopProductViewHolder> {

    private List<TopProduct> topProducts;

    public TopProductAdapter(List<TopProduct> topProducts) {
        this.topProducts = topProducts;

    }
    @NonNull
    @Override
    public TopProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_product, parent, false);
        return new TopProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull TopProductViewHolder holder, int position) {
        TopProduct topProduct = topProducts.get(position);
        holder.tvProductName.setText(topProduct.getTen());
        holder.tvQuantitySold.setText("Đã bán: " + topProduct.getSoLuong());
        holder.tvTotalRevenue.setText("Doanh thu: " + NumberFormat.getInstance(Locale.getDefault()).format(topProduct.getDoanhThu()) + " VNĐ");
        if(topProduct.getImageUrl()!=null && !topProduct.getImageUrl().isEmpty()){
            Glide.with(holder.itemView.getContext())
                    .load(topProduct.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .into(holder.ivProductImage);

        }else{
            holder.ivProductImage.setImageResource(R.drawable.default_avatar);

        }
    }
    @Override
    public int getItemCount() {
        return topProducts.size();
    }
    static class TopProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvQuantitySold, tvTotalRevenue;

        public TopProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantitySold = itemView.findViewById(R.id.tvQuantitySold);
            tvTotalRevenue = itemView.findViewById(R.id.tvTotalRevenue);
        }
    }
}
