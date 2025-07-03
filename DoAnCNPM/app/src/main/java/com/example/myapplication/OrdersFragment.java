package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OrdersFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    public TextInputEditText etTenBanh, etMoTa, etNgayGiao, etNguoiNhan, etSdt;
    public MaterialAutoCompleteTextView spinnerLoaiBanh;
    public ShapeableImageView imgAnhMau;
    public Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        etTenBanh = view.findViewById(R.id.et_ten_banh);
        etMoTa = view.findViewById(R.id.et_mo_ta);
        etNgayGiao = view.findViewById(R.id.et_ngay_giao);
        etNguoiNhan = view.findViewById(R.id.et_nguoi_nhan);
        etSdt = view.findViewById(R.id.et_sdt);
        spinnerLoaiBanh = view.findViewById(R.id.spinner_loai_banh);
        imgAnhMau = view.findViewById(R.id.img_anh_mau);

        Button btnChonAnh = view.findViewById(R.id.btn_chon_anh);
        Button btnDatBanh = view.findViewById(R.id.btn_dat_banh);

        String[] loaiBanhArray = getResources().getStringArray(R.array.loai_banh_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, loaiBanhArray);
        spinnerLoaiBanh.setAdapter(adapter);

        etNgayGiao.setOnClickListener(v -> showDatePicker());
        btnChonAnh.setOnClickListener(v -> openImagePicker());
        btnDatBanh.setOnClickListener(v -> submitForm());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    etNgayGiao.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
                imgAnhMau.setImageBitmap(bitmap);
                imgAnhMau.setAlpha(1.0f);
                imgAnhMau.setPadding(0, 0, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitForm() {
        String tenBanh = etTenBanh.getText().toString().trim();
        String moTa = etMoTa.getText().toString().trim();
        String ngayGiao = etNgayGiao.getText().toString().trim();
        String nguoiNhan = etNguoiNhan.getText().toString().trim();
        String sdt = etSdt.getText().toString().trim();
        String loaiBanh = spinnerLoaiBanh.getText().toString().trim();

        if (tenBanh.isEmpty() || loaiBanh.isEmpty() || moTa.isEmpty() || ngayGiao.isEmpty() || nguoiNhan.isEmpty() || sdt.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sdt.length() < 9 || sdt.length() > 11) {
            etSdt.setError("Số điện thoại không hợp lệ");
            etSdt.requestFocus();
            return;
        }

        if (selectedImageUri != null) {
            uploadImageAndSaveOrder(tenBanh, moTa, ngayGiao, nguoiNhan, sdt, loaiBanh);
        } else {
            saveOrderToFirestore(tenBanh, moTa, ngayGiao, nguoiNhan, sdt, loaiBanh, null);
        }
    }

    private void uploadImageAndSaveOrder(String tenBanh, String moTa, String ngayGiao, String nguoiNhan, String sdt, String loaiBanh) {
        String fileName = "images/" + System.currentTimeMillis() + ".jpg";
        FirebaseStorage.getInstance().getReference(fileName)
                .putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    saveOrderToFirestore(tenBanh, moTa, ngayGiao, nguoiNhan, sdt, loaiBanh, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi upload ảnh", Toast.LENGTH_SHORT).show());
    }

    private void saveOrderToFirestore(String tenBanh, String moTa, String ngayGiao, String nguoiNhan, String sdt, String loaiBanh, @Nullable String imageUrl) {
        String maDonHang = "DH" + System.currentTimeMillis();

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(getContext(), "Không thể xác định người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("maDonHang", maDonHang);
        order.put("tenBanh", tenBanh);
        order.put("moTa", moTa);
        order.put("ngayGiao", ngayGiao);
        order.put("nguoiNhan", nguoiNhan);
        order.put("sdt", sdt);
        order.put("loaiBanh", loaiBanh);
        order.put("status", "Chờ xác nhận");

        if (imageUrl != null) {
            order.put("linkAnhMau", imageUrl);
        }

        FirebaseFirestore.getInstance().collection("custom_orders")
                .document(maDonHang)
                .set(order)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Đặt bánh thành công!", Toast.LENGTH_LONG).show();
                    resetForm();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi gửi đơn", Toast.LENGTH_SHORT).show());
    }

    private void resetForm() {
        etTenBanh.setText("");
        etMoTa.setText("");
        etNgayGiao.setText("");
        etNguoiNhan.setText("");
        etSdt.setText("");
        spinnerLoaiBanh.setText("");

        imgAnhMau.setImageResource(android.R.drawable.ic_menu_gallery);
        imgAnhMau.setAlpha(0.4f);
        imgAnhMau.setPadding(40, 40, 40, 40);
        selectedImageUri = null;
    }
}
