package com.example.myapplication.Profile;

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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ProductViewHolder> {
    private Context context;
    private List<SanPham> productList;

    public OrderProductAdapter(Context context, List<SanPham> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itme_orderproducadapter, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SanPham product = productList.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvProductName.setText(product.getTen());

        // Format giá tiền
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(product.getGia()) + "đ");

        // Hiển thị số lượng (nếu có thuộc tính quantity trong SanPham)
         holder.tvQuantity.setText("x" + product.getSoLuong());

         //hiwen thị hình ảnh
        holder.ivProductImage.setImageResource(product.getAnh());
    }
    public  void updetProduc(List<SanPham> productList){
     this.productList=productList;
     notifyDataSetChanged();
    }
    public void  Remove(int pos){
        if(productList!=null&&pos>=0&&pos<productList.size()){
            productList.remove(pos);
            notifyItemRemoved(pos);
        }
    }
    public void  Add(SanPham product){
        if(productList!=null){
            productList.add(product);
            notifyItemInserted(productList.size()-1);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice,  tvQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}