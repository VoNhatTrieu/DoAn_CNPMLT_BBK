package com.example.myapplication.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.SanPham;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QLSanPhamActivity extends AppCompatActivity implements AdminProductAdapter.OnProductActionListener {

    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<SanPham> productList;
    private List<SanPham> filteredList;
    private Map<String, String> productIdMap;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private Toolbar toolbar;

    private FirebaseFirestore db;
    private static final String TAG = "QLSanPhamActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlsan_pham);

        initViews();
        setupFirestore();
        setupRecyclerView();
        setupListeners();
        loadProducts();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAdd);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupFirestore() {
        db = FirebaseFirestore.getInstance();
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productIdMap = new HashMap<>();
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter(this, filteredList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        // Back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Add product button
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            intent.putExtra("isEditMode", false);
            startActivity(intent);
        });

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadProducts() {
        showLoading(true);

        db.collection("sanpham")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    productIdMap.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            SanPham product = document.toObject(SanPham.class);
                            if (product != null) {
                                productList.add(product);
                                String key = generateProductKey(product);
                                productIdMap.put(key, document.getId());

                                Log.d(TAG, "Loaded product: " + product.getTen() +
                                        ", ID: " + document.getId());
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing product: " + e.getMessage());
                        }
                    }

                    filteredList.clear();
                    filteredList.addAll(productList);
                    adapter.updateData(filteredList);

                    showLoading(false);
                    updateEmptyState();

                    Log.d(TAG, "Total products loaded: " + productList.size());
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading products: " + e.getMessage());
                    showToast("Lỗi khi tải danh sách sản phẩm: " + e.getMessage());
                    updateEmptyState();
                });
    }

    private String generateProductKey(SanPham product) {
        if (product == null) return "";

        String ten = product.getTen() != null ? product.getTen() : "";
        String cateri = product.getCateri() != null ? product.getCateri() : "";

        return ten + "_" + product.getGia() + "_" + cateri;
    }

    private void filterProducts(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(productList);
        } else {
            String searchQuery = normalizeString(query.toLowerCase().trim());

            for (SanPham product : productList) {
                if (product == null) continue;

                String ten = product.getTen() != null ? normalizeString(product.getTen().toLowerCase()) : "";
                String cateri = product.getCateri() != null ? normalizeString(product.getCateri().toLowerCase()) : "";

                if (ten.contains(searchQuery) || cateri.contains(searchQuery)) {
                    filteredList.add(product);
                }
            }
        }

        adapter.updateData(filteredList);
        updateEmptyState();
    }

    private String normalizeString(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (layoutEmpty != null) {
            if (filteredList.isEmpty()) {
                layoutEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onEditProduct(SanPham product) {
        String productKey = generateProductKey(product);
        String documentId = productIdMap.get(productKey);

        Log.d(TAG, "Edit product: " + product.getTen() + ", Key: " + productKey + ", ID: " + documentId);

        if (documentId != null && !documentId.isEmpty()) {
            Intent intent = new Intent(this, AddEditProductActivity.class);
            intent.putExtra("product", product);
            intent.putExtra("documentId", documentId);
            intent.putExtra("isEditMode", true);
            startActivity(intent);
        } else {
            Log.e(TAG, "Document ID not found for product: " + product.getTen());
            showToast("Không thể chỉnh sửa sản phẩm này");
        }
    }

    @Override
    public void onDeleteProduct(SanPham product) {
        String productKey = generateProductKey(product);
        String documentId = productIdMap.get(productKey);

        Log.d(TAG, "Delete product: " + product.getTen() + ", Key: " + productKey + ", ID: " + documentId);

        if (documentId != null && !documentId.isEmpty()) {
            showDeleteConfirmDialog(product, documentId);
        } else {
            Log.e(TAG, "Document ID not found for product: " + product.getTen());
            showToast("Không thể xóa sản phẩm này");
        }
    }

    private void showDeleteConfirmDialog(SanPham product, String documentId) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getTen() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct(product, documentId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(SanPham product, String documentId) {
        showLoading(true);

        db.collection("sanpham")
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    showToast("Đã xóa sản phẩm thành công");

                    // Remove from local lists
                    productList.remove(product);
                    filteredList.remove(product);

                    // Remove from ID map
                    String productKey = generateProductKey(product);
                    productIdMap.remove(productKey);

                    // Update UI
                    adapter.updateData(filteredList);
                    updateEmptyState();

                    Log.d(TAG, "Product deleted successfully: " + product.getTen());
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error deleting product: " + e.getMessage());
                    showToast("Lỗi khi xóa sản phẩm: " + e.getMessage());
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload products when returning from Add/Edit activity
        loadProducts();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clear references to prevent memory leaks
        if (adapter != null) {
            adapter = null;
        }
        if (productList != null) {
            productList.clear();
        }
        if (filteredList != null) {
            filteredList.clear();
        }
        if (productIdMap != null) {
            productIdMap.clear();
        }
    }
}