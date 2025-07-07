package com.example.myapplication.admin;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class BaoCaoDoanhThuActivity extends AppCompatActivity {

    private TextInputEditText edtFromDate, edtToDate;
    private MaterialButton btnToday, btnThisWeek, btnThisMonth, btnFilter;
    private TextView tvTotalRevenue, tvTotalOrders;
    private ImageView btnBack;
    private LineChart lineChart;
    private RecyclerView recyclerViewTopProducts;
    private ProgressBar progressBar;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private Calendar fromDate, toDate;

    private int tongDonThuong = 0, tongDonCustom = 0;
    private double doanhThuThuong = 0, doanhThuCustom = 0;

    private Map<String, TopProduct> topProductMap = new HashMap<>();
    private List<TopProduct> topProducts = new ArrayList<>();
    private Map<Long, Float> ngayDoanhThuMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bao_cao_doanh_thu);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        initializeDates();
    }

    private void initializeViews() {
        edtFromDate = findViewById(R.id.edtFromDate);
        edtToDate = findViewById(R.id.edtToDate);
        btnToday = findViewById(R.id.btnToday);
        btnThisWeek = findViewById(R.id.btnThisWeek);
        btnThisMonth = findViewById(R.id.btnThisMonth);
        btnFilter = findViewById(R.id.btnFilter);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalOrders = findViewById(R.id.tvTotalOrders);
        lineChart = findViewById(R.id.lineChart);
        recyclerViewTopProducts = findViewById(R.id.recyclerViewTopProducts);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        recyclerViewTopProducts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnFilter.setOnClickListener(v -> {
            if (validateDateRange()) {
                reportData();
            }
        });
        edtFromDate.setOnClickListener(v -> showDatePicker(true));
        edtToDate.setOnClickListener(v -> showDatePicker(false));

        btnToday.setOnClickListener(v -> {
            setDateRange(DateRange.TODAY);
            updateDateFields();
        });

        btnThisWeek.setOnClickListener(v -> {
            setDateRange(DateRange.THIS_WEEK);
            updateDateFields();
        });

        btnThisMonth.setOnClickListener(v -> {
            setDateRange(DateRange.THIS_MONTH);
            updateDateFields();
        });
    }

    private void initializeDates() {
        fromDate = Calendar.getInstance();
        toDate = Calendar.getInstance();
        updateDateFields();
    }

    private boolean validateDateRange() {
        if (fromDate.after(toDate)) {
            Toast.makeText(this, "Ngày bắt đầu phải trước ngày kết thúc", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnFilter.setEnabled(!isLoading);
    }

    private void reportData() {
        setLoadingState(true);
        resetData();

        // Điều chỉnh timezone - lấy theo giờ địa phương
        Calendar fromCal = Calendar.getInstance();
        fromCal.setTime(fromDate.getTime());
        fromCal.set(Calendar.HOUR_OF_DAY, 0);
        fromCal.set(Calendar.MINUTE, 0);
        fromCal.set(Calendar.SECOND, 0);
        fromCal.set(Calendar.MILLISECOND, 0);

        Calendar toCal = Calendar.getInstance();
        toCal.setTime(toDate.getTime());
        toCal.set(Calendar.HOUR_OF_DAY, 23);
        toCal.set(Calendar.MINUTE, 59);
        toCal.set(Calendar.SECOND, 59);
        toCal.set(Calendar.MILLISECOND, 999);

        long from = fromCal.getTimeInMillis();
        long to = toCal.getTimeInMillis();

        Log.d("BaoCaoDoanhThu", "Filtering from: " + new Date(from) + " to: " + new Date(to));

        // Lấy dữ liệu từ collection "orders"
        db.collection("orders")
                .whereGreaterThanOrEqualTo("createdAt", new Date(from))
                .whereLessThanOrEqualTo("createdAt", new Date(to))
                .limit(1000) // Giới hạn để tối ưu performance
                .get()
                .addOnSuccessListener(orderDocs -> {
                    Log.d("BaoCaoDoanhThu", "Found " + orderDocs.size() + " orders");

                    for (DocumentSnapshot doc : orderDocs) {
                        Log.d("BaoCaoDoanhThu", "Processing order: " + doc.getId());
                        processOrder(doc, false);
                    }

                    // Lấy dữ liệu từ collection "custom_orders"
                    db.collection("custom_orders")
                            .whereGreaterThanOrEqualTo("createdAt", new Date(from))
                            .whereLessThanOrEqualTo("createdAt", new Date(to))
                            .limit(1000)
                            .get()
                            .addOnSuccessListener(customDocs -> {
                                Log.d("BaoCaoDoanhThu", "Found " + customDocs.size() + " custom orders");

                                for (DocumentSnapshot doc : customDocs) {
                                    Log.d("BaoCaoDoanhThu", "Processing custom order: " + doc.getId());
                                    processOrder(doc, true);
                                }
                                updateUI();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("BaoCaoDoanhThu", "Error getting custom_orders", e);
                                showError("Lỗi lấy custom_orders: " + e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("BaoCaoDoanhThu", "Error getting orders", e);
                    showError("Lỗi lấy orders: " + e.getMessage());
                });
    }

    private void processOrder(DocumentSnapshot doc, boolean isCustom) {
        try {
            Date createdAt = doc.getDate("createdAt");
            Log.d("BaoCaoDoanhThu", "Processing order ID: " + doc.getId() + ", createdAt: " + createdAt);

            if (isCustom) tongDonCustom++;
            else tongDonThuong++;

            // Xử lý totalAmount với nhiều kiểu dữ liệu
            double amount = extractAmount(doc);
            Log.d("BaoCaoDoanhThu", "Order amount: " + amount);

            if (isCustom) doanhThuCustom += amount;
            else doanhThuThuong += amount;

            // Xử lý dữ liệu biểu đồ theo ngày
            addToChartData(createdAt, amount);

            // Xử lý sản phẩm
            processProducts(doc);

        } catch (Exception e) {
            Log.e("BaoCaoDoanhThu", "Error processing order: " + doc.getId(), e);
        }
    }

    private double extractAmount(DocumentSnapshot doc) {
        double amount = 0;
        if (doc.getDouble("totalAmount") != null) {
            amount = doc.getDouble("totalAmount");
        } else if (doc.getLong("totalAmount") != null) {
            amount = doc.getLong("totalAmount").doubleValue();
        } else if (doc.get("totalAmount") != null) {
            Object totalAmountObj = doc.get("totalAmount");
            if (totalAmountObj instanceof Number) {
                amount = ((Number) totalAmountObj).doubleValue();
            }
        }
        return amount;
    }

    private void addToChartData(Date createdAt, double amount) {
        if (createdAt != null) {
            // Chuyển timestamp thành đầu ngày (00:00:00) để group theo ngày
            Calendar cal = Calendar.getInstance();
            cal.setTime(createdAt);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long dayMillis = cal.getTimeInMillis();
            float currentAmount = ngayDoanhThuMap.getOrDefault(dayMillis, 0f);
            ngayDoanhThuMap.put(dayMillis, currentAmount + (float) amount);

            Log.d("BaoCaoDoanhThu", "Added to chart - Date: " +
                    sdf.format(new Date(dayMillis)) + ", Amount: " + amount);
        }
    }

    private void processProducts(DocumentSnapshot doc) {
        List<Map<String, Object>> products = (List<Map<String, Object>>) doc.get("products");
        if (products != null) {
            Log.d("BaoCaoDoanhThu", "Processing " + products.size() + " products");

            for (Map<String, Object> p : products) {
                String name = (String) p.get("ten");

                // Xử lý số lượng
                int qty = extractQuantity(p);

                // Xử lý giá
                double price = extractPrice(p);

                String imageUrl = p.get("imageUrl") != null ? p.get("imageUrl").toString() : "";

                Log.d("BaoCaoDoanhThu", "Product: " + name + ", Qty: " + qty + ", Price: " + price);

                if (name != null && qty > 0) {
                    updateTopProduct(name, qty, price, imageUrl);
                }
            }
        }
    }

    private int extractQuantity(Map<String, Object> product) {
        int qty = 0;
        if (product.get("soLuong") instanceof Long) {
            qty = ((Long) product.get("soLuong")).intValue();
        } else if (product.get("soLuong") instanceof Integer) {
            qty = (Integer) product.get("soLuong");
        }
        return qty;
    }

    private double extractPrice(Map<String, Object> product) {
        double price = 0;
        if (product.get("gia") instanceof Number) {
            price = ((Number) product.get("gia")).doubleValue();
        }
        return price;
    }

    private void updateTopProduct(String name, int qty, double price, String imageUrl) {
        if (!topProductMap.containsKey(name)) {
            topProductMap.put(name, new TopProduct(name, qty, price * qty, imageUrl));
        } else {
            TopProduct tp = topProductMap.get(name);
            tp.setSoLuong(tp.getSoLuong() + qty);
            tp.setDoanhThu(tp.getDoanhThu() + price * qty);
        }
    }

    private void updateUI() {
        setLoadingState(false);

        int tongDon = tongDonCustom + tongDonThuong;
        double tongDoanhThu = doanhThuCustom + doanhThuThuong;

        Log.d("BaoCaoDoanhThu", "Total orders: " + tongDon + ", Total revenue: " + tongDoanhThu);
        Log.d("BaoCaoDoanhThu", "Chart data points: " + ngayDoanhThuMap.size());

        tvTotalOrders.setText(String.valueOf(tongDon));
        tvTotalRevenue.setText(formatCurrency(tongDoanhThu));

        // Nếu không có dữ liệu biểu đồ, thêm điểm mặc định
        if (ngayDoanhThuMap.isEmpty()) {
            addDefaultChartData();
        }

        drawChart(ngayDoanhThuMap);
        updateTopProductsList();

        Log.d("BaoCaoDoanhThu", "Top products: " + topProducts.size());
    }

    private void addDefaultChartData() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        ngayDoanhThuMap.put(today.getTimeInMillis(), 0f);
    }

    private void updateTopProductsList() {
        topProducts.clear();
        topProducts.addAll(topProductMap.values());
        Collections.sort(topProducts, (a, b) -> Double.compare(b.getDoanhThu(), a.getDoanhThu()));

        if (recyclerViewTopProducts.getAdapter() == null) {
            recyclerViewTopProducts.setAdapter(new TopProductAdapter(topProducts));
        } else {
            recyclerViewTopProducts.getAdapter().notifyDataSetChanged();
        }
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "%,.0f VNĐ", amount);
    }

    private void drawChart(Map<Long, Float> map) {
        List<Entry> entries = new ArrayList<>();

        // Sắp xếp dữ liệu theo thời gian
        List<Map.Entry<Long, Float>> sortedEntries = new ArrayList<>(map.entrySet());
        Collections.sort(sortedEntries, Map.Entry.comparingByKey());

        // Tạo entries với index tăng dần thay vì dùng timestamp
        int index = 0;
        Map<Integer, String> indexToDateMap = new HashMap<>();
        SimpleDateFormat chartDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

        for (Map.Entry<Long, Float> entry : sortedEntries) {
            entries.add(new Entry(index, entry.getValue()));
            indexToDateMap.put(index, chartDateFormat.format(new Date(entry.getKey())));
            index++;
        }

        // Kiểm tra nếu không có dữ liệu
        if (entries.isEmpty()) {
            entries.add(new Entry(0, 0));
            indexToDateMap.put(0, "N/A");
        }

        LineDataSet dataSet = createLineDataSet(entries);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        configureChart(indexToDateMap, entries.size());
    }

    private LineDataSet createLineDataSet(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Doanh thu");
        dataSet.setColor(Color.parseColor("#6366F1"));
        dataSet.setCircleColor(Color.parseColor("#EF4444"));
        dataSet.setCircleRadius(4f);
        dataSet.setCircleHoleRadius(2f);
        dataSet.setLineWidth(3f);
        dataSet.setValueTextSize(10f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextColor(Color.parseColor("#374151"));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);

        // Format giá trị hiển thị trên biểu đồ
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatChartValue(value);
            }
        });

        return dataSet;
    }

    private void configureChart(Map<Integer, String> indexToDateMap, int entriesSize) {
        // Cấu hình trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGridColor(Color.parseColor("#E5E7EB"));
        xAxis.setGridLineWidth(1f);
        xAxis.setAxisLineColor(Color.parseColor("#9CA3AF"));
        xAxis.setTextColor(Color.parseColor("#6B7280"));
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-45f);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(Math.min(entriesSize, 7));

        // Custom formatter cho trục X
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int intValue = (int) value;
                return indexToDateMap.getOrDefault(intValue, "");
            }
        });

        // Cấu hình trục Y trái
        configureYAxis();

        // Cấu hình chung cho biểu đồ
        configureChartAppearance();
    }

    private void configureYAxis() {
        lineChart.getAxisLeft().setDrawGridLines(true);
        lineChart.getAxisLeft().setGridColor(Color.parseColor("#E5E7EB"));
        lineChart.getAxisLeft().setGridLineWidth(1f);
        lineChart.getAxisLeft().setAxisLineColor(Color.parseColor("#9CA3AF"));
        lineChart.getAxisLeft().setTextColor(Color.parseColor("#6B7280"));
        lineChart.getAxisLeft().setTextSize(10f);
        lineChart.getAxisLeft().setSpaceTop(15f);
        lineChart.getAxisLeft().setSpaceBottom(15f);

        // Format trục Y
        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return formatChartValue(value);
            }
        });

        // Ẩn trục Y phải
        lineChart.getAxisRight().setEnabled(false);
    }

    private void configureChartAppearance() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawBorders(false);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setDrawGridBackground(false);

        // Cấu hình legend
        lineChart.getLegend().setEnabled(false);

        // Cấu hình tương tác
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        // Padding
        lineChart.setExtraTopOffset(20f);
        lineChart.setExtraBottomOffset(20f);
        lineChart.setExtraLeftOffset(20f);
        lineChart.setExtraRightOffset(20f);

        // Animation
        lineChart.animateX(1000);

        // Refresh biểu đồ
        lineChart.invalidate();
    }

    private String formatChartValue(float value) {
        if (value >= 1000000) {
            return String.format("%.1fM", value / 1000000);
        } else if (value >= 1000) {
            return String.format("%.0fK", value / 1000);
        } else {
            return String.format("%.0f", value);
        }
    }

    private void showDatePicker(boolean isFrom) {
        Calendar calendar = isFrom ? fromDate : toDate;
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            if (isFrom) edtFromDate.setText(sdf.format(calendar.getTime()));
            else edtToDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDateFields() {
        edtFromDate.setText(sdf.format(fromDate.getTime()));
        edtToDate.setText(sdf.format(toDate.getTime()));
    }

    private void setDateRange(DateRange range) {
        Calendar today = Calendar.getInstance();

        switch (range) {
            case TODAY:
                fromDate = (Calendar) today.clone();
                toDate = (Calendar) today.clone();
                break;

            case THIS_WEEK:
                fromDate = (Calendar) today.clone();
                fromDate.set(Calendar.DAY_OF_WEEK, fromDate.getFirstDayOfWeek());
                toDate = (Calendar) today.clone();
                break;

            case THIS_MONTH:
                fromDate = (Calendar) today.clone();
                fromDate.set(Calendar.DAY_OF_MONTH, 1);
                toDate = (Calendar) today.clone();
                break;
        }
    }

    private void resetData() {
        tongDonThuong = tongDonCustom = 0;
        doanhThuThuong = doanhThuCustom = 0;
        topProductMap.clear();
        ngayDoanhThuMap.clear();
        topProducts.clear();
    }

    private void showError(String msg) {
        setLoadingState(false);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private enum DateRange {
        TODAY, THIS_WEEK, THIS_MONTH
    }
}