package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class HomeFragment extends Fragment {
    private  ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private EditText timkiem;
    private ImageView boloc;
    private  RecyclerView recyclerView;
    private List<SanPham> tatca;
    private  List<SanPham>locsp;
    private Runnable truotbanner;
    private  SanPhamAdapter sanPhamAdapter;

    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private  int vitri=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager2=view.findViewById(R.id.Banner);
        tabLayout=view.findViewById(R.id.chamtron);
        recyclerView=view.findViewById(R.id.dsSP);
        timkiem=view.findViewById(R.id.edTimKiem);
        boloc=view.findViewById(R.id.igBoLoc);
        setupBanner();
        setupBoLoc();
        setupTimKiem();
        setupProductList();
        return view;
    }
    void setupBanner(){
        List<Integer>qc= Arrays.asList(R.drawable.ban1,R.drawable.ban2,R.drawable.ban3,R.drawable.ban4);
        Banner banner=new Banner(qc);
        viewPager2.setAdapter(banner);
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) ->{}).attach();
        truotbanner = () -> {
            if (banner.getItemCount() == 0) return;

            int nextIndex = viewPager2.getCurrentItem() + 1;
            if (nextIndex >= banner.getItemCount()) {
                nextIndex = 0;
            }

            viewPager2.setCurrentItem(nextIndex, true);
            bannerHandler.postDelayed(truotbanner, 3000);
        };
        bannerHandler.postDelayed(truotbanner, 3000);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bannerHandler.removeCallbacks(truotbanner);
                bannerHandler.postDelayed(truotbanner, 4000);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bannerHandler.removeCallbacks(truotbanner);
    }
    private void setupTimKiem() {
        timkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override

            public void afterTextChanged(Editable s) {
               String sanpham=s.toString().trim();
               if(sanpham.isEmpty()){
                   locsp.clear();
                   sanPhamAdapter.notifyDataSetChanged();
               }
                else {
                    Timkiemsp(sanpham);
               }
            }
        });
    }
    private void Timkiemsp(String query) {
        query = Normalizer.normalize(query, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
        locsp.clear();
        for (SanPham sp : tatca) {
            String tenSanPham = Normalizer.normalize(sp.getTen() != null ? sp.getTen() : "", Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase();
            if (tenSanPham.contains(query)) {
                locsp.add(sp);
            }
        }
        sanPhamAdapter.notifyDataSetChanged();
    }


    private void setupBoLoc() {
        boloc.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String[] loai = {"Tất cả", "Kem", "Chocolate", "Trà xanh"};
            builder.setTitle("Chọn loại bánh")
                    .setItems(loai, (dialog, which) -> {
                        String selected = loai[which];
                        filterByCategory(selected);
                    });
            builder.create().show();
        });
    }
    private void setupProductList() {
        tatca = Arrays.asList(
                new SanPham("Bánh kem dâu", 120000, R.drawable.anh1,  "Kem"),
                new SanPham("Bánh chocolate", 150000, R.drawable.ban1,  "Chocolate"),
                new SanPham("Bánh tiramisu", 140000, R.drawable.ban2,  "Kem"),
                new SanPham("Bánh matcha", 130000, R.drawable.ban3, "Trà xanh"),
                 new SanPham("Bánh matcha", 130000, R.drawable.ban3, "Trà xanh"),
                new SanPham("Bánh matcha", 130000, R.drawable.ban3, "Trà xanh")
        );

        locsp = new ArrayList<>(tatca);
        sanPhamAdapter = new SanPhamAdapter(requireContext(),locsp);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(sanPhamAdapter);
    }
    private void filterByCategory(String category) {
        Log.d("BoLoc", "Lọc theo: " + category);
        locsp.clear();
        if (category.equals("Tất cả")) {
            locsp.addAll(tatca);
        } else {
            for (SanPham p : tatca) {
                if (p.getCateri() != null && p.getCateri().equalsIgnoreCase(category)) {
                    locsp.add(p);
                }
            }
        }
        Log.d("BoLoc", "Số sản phẩm sau lọc: " + locsp.size());
        sanPhamAdapter.notifyDataSetChanged();
    }
}