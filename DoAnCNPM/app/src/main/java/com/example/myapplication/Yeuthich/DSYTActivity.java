package com.example.myapplication.Yeuthich;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.SanPham;
import com.example.myapplication.Trchitietsp;

import java.util.ArrayList;
import java.util.List;

public class DSYTActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<SanPham> dsitem;
    private dsytAdapter dsyt;
    private TextView tvempty;
    private ImageView btback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dsytactivity);
        recyclerView = findViewById(R.id.rvWishlist);
        tvempty = findViewById(R.id.tvEmpty);
        btback = findViewById(R.id.back_icon);

        btback.setOnClickListener(v -> finish());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        dsitem = QLYT.getInstance().getWishlistItems();
        if (dsitem == null) {
            dsitem = new ArrayList<>();
        }
        dsyt = new dsytAdapter(this, dsitem, new dsytAdapter.OnWishlistItemClickListener() {
            @Override
            public void onItemClick(SanPham sanPham) {
                Intent intent = new Intent(DSYTActivity.this, Trchitietsp.class);
                intent.putExtra("sanPham", sanPham);
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(SanPham sanPham) {
                QLYT.getInstance().removeFromWishlist(sanPham);
                dsitem.remove(sanPham);
                dsyt.notifyDataSetChanged();
                updateEmptyState();
                Toast.makeText(DSYTActivity.this, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(dsyt);
        updateEmptyState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dsitem.clear();
        List<SanPham> updatedItems = QLYT.getInstance().getWishlistItems();
        if (updatedItems != null) {
            dsitem.addAll(updatedItems);
        }
        dsyt.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (dsitem.isEmpty()) {
            tvempty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvempty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}