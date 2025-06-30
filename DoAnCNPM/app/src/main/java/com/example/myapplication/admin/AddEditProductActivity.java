package com.example.myapplication.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.SanPham;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddEditProductActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView ivProductImage, btnSelectImage;
    private TextInputEditText etProductName, etProductPrice, etProductQuantity, etProductMota;
    private Spinner spinnerCategory;
    private Button btnCancel, btnSave;

    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri selectedImageUri;
    private String imageUrl = "";

    private SanPham currentProduct;
    private boolean isEditMode = false;
    private String productId = "";

    private String[] categories = {"Kem", "Chocolate", "Trà xanh", "Bánh ngọt", "Bánh mặn"};
    private int[] defaultImages = {
            R.drawable.ban1, R.drawable.ban2, R.drawable.ban3,
            R.drawable.ban4, R.drawable.ban5, R.drawable.ban6,
            R.drawable.ban7
    };
    private int selectedDefaultImage = R.drawable.ban1;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);

        initViews();
        initFirebase();
        setupSpinner();
        setupImagePicker();
        setupListeners();
        checkEditMode();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        etProductMota = findViewById(R.id.etProductMota);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveProduct());
        btnCancel.setOnClickListener(v -> onBackPressed());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        ivProductImage.setImageURI(selectedImageUri);
                        selectedDefaultImage = 0;
                    }
                }
        );
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void checkEditMode() {
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false) || intent.getBooleanExtra("isEdit", false);

        if (isEditMode) {
            currentProduct = (SanPham) intent.getSerializableExtra("product");
            productId = intent.getStringExtra("productId");

            if (currentProduct != null) {
                fillFields();
                toolbar.setTitle("Sửa sản phẩm");
            } else {
                toolbar.setTitle("Thêm sản phẩm");
                isEditMode = false;
            }
        } else {
            toolbar.setTitle("Thêm sản phẩm");
        }
    }

    private void fillFields() {
        etProductName.setText(currentProduct.getTen());
        etProductPrice.setText(String.valueOf(currentProduct.getGia()));
        etProductQuantity.setText(String.valueOf(currentProduct.getSoLuong()));

        // Fill mô tả
        if (currentProduct.getMota() != null) {
            etProductMota.setText(currentProduct.getMota());
        }

        // Set category
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(currentProduct.getCateri())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Load image
        if (currentProduct.getImageUrl() != null && !currentProduct.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(currentProduct.getImageUrl())
                    .placeholder(R.drawable.ban1)
                    .error(R.drawable.ban1)
                    .into(ivProductImage);
            imageUrl = currentProduct.getImageUrl();
        } else if (currentProduct.getAnh() != 0) {
            ivProductImage.setImageResource(currentProduct.getAnh());
            selectedDefaultImage = currentProduct.getAnh();
        }
    }

    private void openImagePicker() {
        String[] options = {"Chọn ảnh từ thư viện", "Chọn ảnh mặc định"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn hình ảnh")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openGallery();
                    } else {
                        showDefaultImageDialog();
                    }
                }).show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void showDefaultImageDialog() {
        String[] imgName = {"Ảnh 1", "Ảnh 2", "Ảnh 3", "Ảnh 4", "Ảnh 5", "Ảnh 6", "Ảnh 7"};
        new AlertDialog.Builder(this)
                .setTitle("Chọn ảnh mặc định")
                .setItems(imgName, (dialog, which) -> {
                    if (which < defaultImages.length) {
                        selectedDefaultImage = defaultImages[which];
                        ivProductImage.setImageResource(selectedDefaultImage);
                        selectedImageUri = null;
                        imageUrl = ""; // Clear URL when using default image
                    }
                }).show();
    }

    private void saveProduct() {
        if (!validateFields()) return;

        showLoading(true);

        String name = etProductName.getText().toString().trim();
        int price = Integer.parseInt(etProductPrice.getText().toString().trim());
        int quantity = Integer.parseInt(etProductQuantity.getText().toString().trim());
        String category = spinnerCategory.getSelectedItem().toString();
        String mota = etProductMota.getText().toString().trim();

        if (selectedImageUri != null) {
            uploadImageAndSave(name, price, quantity, category, mota);
        } else {
            saveToFirestore(name, price, quantity, category, selectedDefaultImage, imageUrl, mota);
        }
    }

    private void uploadImageAndSave(String name, int price, int quantity, String category, String mota) {
        String fileName = "product_images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference fileRef = storageRef.child(fileName);

        fileRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        imageUrl = uri.toString();
                        saveToFirestore(name, price, quantity, category, 0, imageUrl, mota);
                    });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(this, "Lỗi tải ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToFirestore(String name, int price, int quantity, String category, int imageResource, String imageUrl, String mota) {
        Map<String, Object> product = new HashMap<>();
        product.put("ten", name);
        product.put("gia", price);
        product.put("soLuong", quantity); // ✅ Sửa key name
        product.put("cateri", category);
        product.put("anh", imageResource);
        product.put("imageUrl", imageUrl); // ✅ Sửa key name
        product.put("mota", mota);
        product.put("timestamp", System.currentTimeMillis());

        if (isEditMode && !productId.isEmpty()) {
            // Update existing product
            db.collection("sanpham").document(productId)
                    .update(product)
                    .addOnSuccessListener(aVoid -> {
                        showLoading(false);
                        Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Add new product
            db.collection("sanpham")
                    .add(product)
                    .addOnSuccessListener(documentReference -> {
                        showLoading(false);
                        Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        showLoading(false);
                        Toast.makeText(this, "Lỗi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean validateFields() {
        String name = etProductName.getText().toString().trim();
        String price = etProductPrice.getText().toString().trim();
        String quantity = etProductQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Vui lòng nhập tên sản phẩm");
            etProductName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(price)) {
            etProductPrice.setError("Vui lòng nhập giá sản phẩm");
            etProductPrice.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(quantity)) {
            etProductQuantity.setError("Vui lòng nhập số lượng");
            etProductQuantity.requestFocus();
            return false;
        }

        try {
            int priceValue = Integer.parseInt(price);
            int quantityValue = Integer.parseInt(quantity);

            if (priceValue <= 0) {
                etProductPrice.setError("Giá phải lớn hơn 0");
                etProductPrice.requestFocus();
                return false;
            }

            if (quantityValue < 0) {
                etProductQuantity.setError("Số lượng không được âm");
                etProductQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showLoading(boolean show) {
        if (show) {
            btnSave.setEnabled(false);
            btnSave.setText("Đang lưu...");
        } else {
            btnSave.setEnabled(true);
            btnSave.setText("Lưu");
        }
    }
}