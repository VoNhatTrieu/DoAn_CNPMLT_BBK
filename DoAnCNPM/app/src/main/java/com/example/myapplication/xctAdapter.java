package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class xctAdapter extends RecyclerView.Adapter<xctAdapter.ViewHolder> {
    private List<xemchitiet> xem;

    public xctAdapter(List<xemchitiet> xem) {
        this.xem = xem != null ? xem : new ArrayList<>(); // Kiểm tra null
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_xemchitiet, parent, false); // Sửa layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        xemchitiet xemchitiet = xem.get(position); // Sửa lại từ xemchitiet.get thành xem.get
        holder.tvComment.setText(xemchitiet.getComment() + " - " + xemchitiet.getUserName() + " (" + xemchitiet.getRating() + " sao)");
    }

    @Override
    public int getItemCount() {
        return xem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}