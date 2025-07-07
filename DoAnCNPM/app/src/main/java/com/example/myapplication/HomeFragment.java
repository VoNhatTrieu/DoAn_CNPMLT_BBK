package com.example.myapplication;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.Yeuthich.DSYTActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    // Views from layout
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private EditText timkiem;
    private ImageView boloc, yeuthich, thongbao;
    private RecyclerView recyclerView;
    private TextView tvViTri;
    private FloatingActionButton fabScrollTop;
    private NestedScrollView nestedScrollView;

    // Data
    private List<SanPham> tatca;
    private List<SanPham> locsp;
    private Map<String, String> productIdMap;
    private Runnable truotbanner;
    private SanPhamAdapter sanPhamAdapter;

    // Handlers
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        initFirestore();
        setupBanner();
        setupBoLoc();
        setupTimKiem();
        setupRecyclerView();
        setupYeuThich();
        setupThongBao();
        setupScrollToTop();
        loadProductsFromFirestore();

        return view;
    }

    private void initViews(View view) {
        // Banner components
        viewPager2 = view.findViewById(R.id.Banner);
        tabLayout = view.findViewById(R.id.chamtron);

        // Search and filter
        timkiem = view.findViewById(R.id.edTimKiem);
        boloc = view.findViewById(R.id.igBoLoc);
        yeuthich = view.findViewById(R.id.igYeuThich);
        thongbao = view.findViewById(R.id.igThongbao);

        // Location
        tvViTri = view.findViewById(R.id.tvViTri);

        // RecyclerView
        recyclerView = view.findViewById(R.id.dsSP);

        // Scroll components
        fabScrollTop = view.findViewById(R.id.fabScrollTop);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Set initial location
        tvViTri.setText("Hóc Môn, Việt Nam");
        // Add click animation for location
        tvViTri.setOnClickListener(v -> animateLocationClick());
    }

    private void initFirestore() {
        db = FirebaseFirestore.getInstance();
        tatca = new ArrayList<>();
        locsp = new ArrayList<>();
        productIdMap = new HashMap<>();
    }

    private void setupRecyclerView() {
        sanPhamAdapter = new SanPhamAdapter(requireContext(), locsp, sanPham -> {
            // Lấy documentId từ Map
            String productKey = generateProductKey(sanPham);
            String documentId = productIdMap.get(productKey);

            Intent intent = new Intent(requireContext(), Trchitietsp.class);
            if (documentId != null) {
                intent.putExtra("productId", documentId);
            } else {
                intent.putExtra("sanPham", sanPham);
            }
            startActivity(intent);
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(sanPhamAdapter);

        // Add spacing between items
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
    }

    private void loadProductsFromFirestore() {
        // Show loading animation
        showLoadingAnimation();

        db.collection("sanpham")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tatca.clear();
                    productIdMap.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            SanPham product = document.toObject(SanPham.class);
                            tatca.add(product);

                            // Lưu mapping giữa product key và document ID
                            String productKey = generateProductKey(product);
                            productIdMap.put(productKey, document.getId());

                        } catch (Exception e) {
                            Log.e("HomeFragment", "Lỗi đọc sản phẩm: " + e.getMessage());
                        }
                    }

                    locsp.clear();
                    locsp.addAll(tatca);
                    sanPhamAdapter.notifyDataSetChanged();

                    if (tatca.isEmpty()) {
                        Toast.makeText(requireContext(), "Chưa có sản phẩm nào", Toast.LENGTH_SHORT).show();
                    }

                    hideLoadingAnimation();
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Lỗi tải sản phẩm: " + e.getMessage());
                    Toast.makeText(requireContext(), "Lỗi khi tải sản phẩm", Toast.LENGTH_SHORT).show();
                    loadDefaultProducts();
                    hideLoadingAnimation();
                });
    }

    private String generateProductKey(SanPham product) {
        return product.getTen() + "_" + product.getGia() + "_" + product.getCateri();
    }

    private void loadDefaultProducts() {
        tatca = Arrays.asList(
                new SanPham("Bánh kem dâu", 120000, R.drawable.ban5, "Kem"),
                new SanPham("Bánh chocolate", 150000, R.drawable.ban1, "Chocolate"),
                new SanPham("Bánh tiramisu", 140000, R.drawable.ban2, "Kem"),
                new SanPham("Bánh matcha", 130000, R.drawable.ban3, "Trà xanh")
        );

        locsp.clear();
        locsp.addAll(tatca);
        sanPhamAdapter.notifyDataSetChanged();
    }

    private void setupBanner() {
        List<Integer> qc = Arrays.asList(
                R.drawable.ban1,
                R.drawable.ban2,
                R.drawable.ban3,
                R.drawable.ban4
        );

        Banner banner = new Banner(qc);
        viewPager2.setAdapter(banner);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            // Empty implementation - just for indicator dots
        }).attach();

        // Auto-scroll banner
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

        // Reset timer when user manually scrolls
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bannerHandler.removeCallbacks(truotbanner);
                bannerHandler.postDelayed(truotbanner, 4000);
            }
        });
    }

    private void setupTimKiem() {
        timkiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString().trim();
                if (search.isEmpty()) {
                    locsp.clear();
                    locsp.addAll(tatca);
                    sanPhamAdapter.notifyDataSetChanged();
                } else {
                    Timkiemsp(search);
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
            String ten = sp.getTen() != null ? sp.getTen() : "";
            ten = Normalizer.normalize(ten, Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase();
            if (ten.contains(query)) {
                locsp.add(sp);
            }
        }

        if (locsp.isEmpty()) {
            Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
        }

        sanPhamAdapter.notifyDataSetChanged();
    }

    private void setupBoLoc() {
        boloc.setOnClickListener(v -> {
            // Add click animation
            animateButtonClick(boloc);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String[] loai = {"Tất cả", "Kem", "Chocolate", "Trà xanh", "Bánh ngọt", "Bánh mặn"};

            builder.setTitle("Chọn loại bánh")
                    .setItems(loai, (dialog, which) -> {
                        filterByCategory(loai[which]);
                        Toast.makeText(requireContext(),
                                "Đã lọc theo: " + loai[which],
                                Toast.LENGTH_SHORT).show();
                    })
                    .create()
                    .show();
        });
    }

    private void filterByCategory(String category) {
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

        if (locsp.isEmpty()) {
            Toast.makeText(requireContext(), "Không có sản phẩm trong danh mục này", Toast.LENGTH_SHORT).show();
        }

        sanPhamAdapter.notifyDataSetChanged();
    }

    private void setupYeuThich() {
        yeuthich.setOnClickListener(v -> {
            // Add click animation
            animateButtonClick(yeuthich);

            Intent intent = new Intent(requireContext(), DSYTActivity.class);
            startActivity(intent);
        });
    }

    private void setupThongBao() {
        if (thongbao != null) {
            thongbao.setOnClickListener(v -> {
                // Add click animation
                animateButtonClick(thongbao);

                // Handle notification click
                Toast.makeText(requireContext(), "Bạn chưa có thông báo mới", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupScrollToTop() {
        if (fabScrollTop != null && nestedScrollView != null) {
            // Show/hide FAB based on scroll position
            nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                    (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (scrollY > 300) {
                            fabScrollTop.show();
                        } else {
                            fabScrollTop.hide();
                        }
                    });

            // Click to scroll to top
            fabScrollTop.setOnClickListener(v -> {
                nestedScrollView.smoothScrollTo(0, 0);
                animateButtonClick(fabScrollTop);
            });
        }
    }

    // Animation methods
    private void animateButtonClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.9f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.9f, 1.0f);

        scaleX.setDuration(200);
        scaleY.setDuration(200);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        scaleX.start();
        scaleY.start();
    }

    private void animateLocationClick() {
        // Animate location text
        ObjectAnimator animator = ObjectAnimator.ofFloat(tvViTri, "alpha", 1.0f, 0.7f, 1.0f);
        animator.setDuration(300);
        animator.start();

        // Show location options
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String[] locations = {"Hóc Môn, Việt Nam", "Quận 1, TP.HCM", "Quận 7, TP.HCM", "Bình Thạnh, TP.HCM"};

        builder.setTitle("Chọn vị trí")
                .setItems(locations, (dialog, which) -> {
                    tvViTri.setText(locations[which]);
                    Toast.makeText(requireContext(),
                            "Đã thay đổi vị trí: " + locations[which],
                            Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private void showLoadingAnimation() {
        // You can add a loading spinner or skeleton view here
        if (recyclerView != null) {
            recyclerView.setAlpha(0.5f);
        }
    }

    private void hideLoadingAnimation() {
        if (recyclerView != null) {
            ObjectAnimator.ofFloat(recyclerView, "alpha", 0.5f, 1.0f)
                    .setDuration(300)
                    .start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(truotbanner);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume banner auto-scroll
        if (truotbanner != null) {
            bannerHandler.postDelayed(truotbanner, 3000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause banner auto-scroll
        if (bannerHandler != null) {
            bannerHandler.removeCallbacks(truotbanner);
        }
    }
}