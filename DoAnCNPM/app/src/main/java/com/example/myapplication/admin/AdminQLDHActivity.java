package com.example.myapplication.admin;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminQLDHActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextView tvTotalOrders, tvPendingOrders, tvCustomOrders;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private MaterialAutoCompleteTextView spinnerStatusFilter;
    private MaterialButton btnRefresh;
    private LinearLayout layoutEmptyState, layoutLoading;

    private CollectionReference donHangRef;
    private CollectionReference customOrdersRef;
    private OrderPagerAdapter pagerAdapter;

    private int tongDonHangThuong = 0;
    private int donHangThuongChoXuLy = 0;
    private int tongDonHangTheoYeuCau = 0;
    private int donHangTheoYeuCauChoXuLy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qldhactivity);

        initViews();
        setupFirestore();
        setupEventListeners();
        setupTabLayout();
        setupStatusFilter();
        setupToolbar();

        taiThongKeDonHang();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvPendingOrders = findViewById(R.id.tv_pending_orders);
        tvCustomOrders = findViewById(R.id.tv_custom_orders);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        spinnerStatusFilter = findViewById(R.id.spinner_status_filter);
        btnRefresh = findViewById(R.id.btn_refresh);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        layoutLoading = findViewById(R.id.layout_loading);
    }

    private void setupFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        donHangRef = db.collection("don_hang");
        customOrdersRef = db.collection("custom_orders");
    }

    private void setupEventListeners() {
        btnRefresh.setOnClickListener(v -> lamMoiDuLieu());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Quản lý đơn hàng");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupTabLayout() {
        pagerAdapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Đơn hàng thường");
                    } else {
                        tab.setText("Bánh theo yêu cầu");
                    }
                }).attach();
    }

    private void setupStatusFilter() {
        String[] trangThaiArray = {
                "Tất cả",
                "Chờ xử lý",
                "Đang làm",
                "Hoàn tất",
                "Đã hủy",
                "Chờ báo giá",
                "Đã báo giá"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, trangThaiArray);
        spinnerStatusFilter.setAdapter(adapter);
        spinnerStatusFilter.setText("Tất cả", false);

        spinnerStatusFilter.setOnItemClickListener((parent, view, position, id) -> {
            String trangThaiChon = trangThaiArray[position];
            locDonHangTheoTrangThai(trangThaiChon);
        });
    }

    private void taiThongKeDonHang() {
        hienThiLoading(true);

        // Load cả hai collection
        donHangRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    xuLyThongKeDonHangThuong(snapshot);
                }
            }
            // Load custom orders
            customOrdersRef.get().addOnCompleteListener(customTask -> {
                if (customTask.isSuccessful()) {
                    QuerySnapshot customSnapshot = customTask.getResult();
                    if (customSnapshot != null) {
                        xuLyThongKeDonHangTheoYeuCau(customSnapshot);
                    }
                }
                capNhatThongKeUI();
                taiDanhSachDonHangTuFragment();
            });
        });
    }

    private void xuLyThongKeDonHangThuong(QuerySnapshot snapshot) {
        tongDonHangThuong = 0;
        donHangThuongChoXuLy = 0;

        for (QueryDocumentSnapshot doc : snapshot) {
            try {
                DonHang donHang = doc.toObject(DonHang.class);
                if (donHang != null) {
                    tongDonHangThuong++;
                    if (isChoXuLy(donHang.getTrangThai())) {
                        donHangThuongChoXuLy++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void xuLyThongKeDonHangTheoYeuCau(QuerySnapshot snapshot) {
        tongDonHangTheoYeuCau = 0;
        donHangTheoYeuCauChoXuLy = 0;

        for (QueryDocumentSnapshot doc : snapshot) {
            try {
                OrderModel orderModel = doc.toObject(OrderModel.class);
                if (orderModel != null) {
                    tongDonHangTheoYeuCau++;
                    if (isChoXuLyCustom(orderModel.getStatus())) {
                        donHangTheoYeuCauChoXuLy++;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isChoXuLy(String trangThai) {
        return "cho_xu_ly".equals(trangThai) || "dang_lam".equals(trangThai);
    }

    private boolean isChoXuLyCustom(String status) {
        return "Chờ xác nhận".equals(status) || "Chờ báo giá".equals(status);
    }

    private void capNhatThongKeUI() {
        int tongTatCaDonHang = tongDonHangThuong + tongDonHangTheoYeuCau;
        int tongDonHangChoXuLy = donHangThuongChoXuLy + donHangTheoYeuCauChoXuLy;

        tvTotalOrders.setText(String.valueOf(tongTatCaDonHang));
        tvPendingOrders.setText(String.valueOf(tongDonHangChoXuLy));
        tvCustomOrders.setText(String.valueOf(tongDonHangTheoYeuCau));

        hienThiLoading(false);
        hienThiTrangThaiTrong(tongTatCaDonHang == 0);

        locDonHangTheoTrangThai(spinnerStatusFilter.getText().toString());
    }

    private void locDonHangTheoTrangThai(String trangThai) {
        Fragment currentFragment = pagerAdapter.getCurrentFragment(viewPager.getCurrentItem());

        if (currentFragment != null) {
            if (currentFragment instanceof Fragment_DonHangThuong) {
                ((Fragment_DonHangThuong) currentFragment).filterByStatus(trangThai);
            } else if (currentFragment instanceof FragmentBanhTheoYeuCau) {
                ((FragmentBanhTheoYeuCau) currentFragment).filterByStatus(trangThai);
            }
        }
    }

    private void lamMoiDuLieu() {
        hienThiLoading(true);
        taiThongKeDonHang();

        Fragment currentFragment = pagerAdapter.getCurrentFragment(viewPager.getCurrentItem());
        if (currentFragment != null) {
            if (currentFragment instanceof Fragment_DonHangThuong) {
                ((Fragment_DonHangThuong) currentFragment).refreshData();
            } else if (currentFragment instanceof FragmentBanhTheoYeuCau) {
                ((FragmentBanhTheoYeuCau) currentFragment).refreshData();
            }
        }

        Toast.makeText(this, "Đã làm mới dữ liệu", Toast.LENGTH_SHORT).show();
    }

    private void hienThiLoading(boolean hien) {
        if (layoutLoading != null && viewPager != null) {
            layoutLoading.setVisibility(hien ? View.VISIBLE : View.GONE);
            viewPager.setVisibility(hien ? View.GONE : View.VISIBLE);
        }
    }

    private void hienThiTrangThaiTrong(boolean hien) {
        if (layoutEmptyState != null && viewPager != null) {
            layoutEmptyState.setVisibility(hien ? View.VISIBLE : View.GONE);
            viewPager.setVisibility(hien ? View.GONE : View.VISIBLE);
        }
    }

    private void taiDanhSachDonHangTuFragment() {
        for (int i = 0; i < 2; i++) {
            Fragment fragment = pagerAdapter.getCurrentFragment(i);
            if (fragment != null) {
                if (fragment instanceof Fragment_DonHangThuong) {
                    ((Fragment_DonHangThuong) fragment).refreshData();
                } else if (fragment instanceof FragmentBanhTheoYeuCau) {
                    ((FragmentBanhTheoYeuCau) fragment).refreshData();
                }
            }
        }
    }

    public CollectionReference getDonHangRef() {
        return donHangRef;
    }

    public CollectionReference getCustomOrdersRef() {
        return customOrdersRef;
    }

    private static class OrderPagerAdapter extends FragmentStateAdapter {
        private final SparseArray<Fragment> fragments = new SparseArray<>();

        public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            if (position == 0) {
                fragment = Fragment_DonHangThuong.newInstance(false);
            } else {
                fragment = FragmentBanhTheoYeuCau.newInstance(true);
            }
            fragments.put(position, fragment);
            return fragment;
        }

        public Fragment getCurrentFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public void clearFragments() {
            fragments.clear();
        }
    }
}