package com.example.myapplication;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class Banner extends RecyclerView.Adapter<Banner.BannerViewHolder> {

    private List<Integer> listbanner;

    // Constructor
    public Banner(List<Integer> a) {
        this.listbanner = a != null ? a : new ArrayList<>(); // Khởi tạo danh sách rỗng nếu null
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_banner, parent, false); // Sử dụng item_banner
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        if (listbanner != null && !listbanner.isEmpty()) {
            int imageResId = listbanner.get(position);
            holder.anhbanner.setImageResource(imageResId);
        } else {
            holder.anhbanner.setImageResource(R.drawable.ban1); // Hình ảnh dự phòng
        }
    }

    @Override
    public int getItemCount() {
        return listbanner.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView anhbanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            anhbanner = itemView.findViewById(R.id.anhbanner); // Sử dụng ID anhbanner
            if (anhbanner == null) {
                Log.e("Banner", "Không tìm thấy ImageView với ID anhbanner trong item_banner.xml");
            }
        }
    }
}