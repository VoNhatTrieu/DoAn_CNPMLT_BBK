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
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

public class OrdersFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    public TextInputEditText etTenBanh, etMoTa, etKichCo, etNgayGiao, etNguoiNhan, etSdt;
    public MaterialAutoCompleteTextView spinnerLoaiBanh;
    public ShapeableImageView imgAnhMau;
    public Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        // Ánh xạ view
        etTenBanh = view.findViewById(R.id.et_ten_banh);
        etMoTa = view.findViewById(R.id.et_mo_ta);
        etKichCo = view.findViewById(R.id.et_kich_co);
        etNgayGiao = view.findViewById(R.id.et_ngay_giao);
        etNguoiNhan = view.findViewById(R.id.et_nguoi_nhan);
        etSdt = view.findViewById(R.id.et_sdt);
        spinnerLoaiBanh = view.findViewById(R.id.spinner_loai_banh);
        imgAnhMau = view.findViewById(R.id.img_anh_mau);

        Button btnChonAnh = view.findViewById(R.id.btn_chon_anh);
        Button btnDatBanh = view.findViewById(R.id.btn_dat_banh);


        // Khởi tạo dropdown cho loại bánh
        String[] loaiBanhArray = getResources().getStringArray(R.array.loai_banh_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, loaiBanhArray);
        spinnerLoaiBanh.setAdapter(adapter);

        // Chọn ngày
        etNgayGiao.setOnClickListener(v -> showDatePicker());

        // Chọn ảnh
        btnChonAnh.setOnClickListener(v -> openImagePicker());

        // Gửi yêu cầu
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

        // Đặt ngày tối thiểu là ngày hiện tại
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
                imgAnhMau.setAlpha(1.0f); // Bỏ mờ ảnh mặc định
                imgAnhMau.setPadding(0, 0, 0, 0); // Bỏ padding khi có ảnh
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Không thể chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void submitForm() {
        String tenBanh = etTenBanh.getText().toString().trim();
        String moTa = etMoTa.getText().toString().trim();
        String kichCo = etKichCo.getText().toString().trim();
        String ngayGiao = etNgayGiao.getText().toString().trim();
        String nguoiNhan = etNguoiNhan.getText().toString().trim();
        String sdt = etSdt.getText().toString().trim();
        String loaiBanh = spinnerLoaiBanh.getText().toString().trim();

        // Validation
        if (tenBanh.isEmpty()) {
            etTenBanh.setError("Vui lòng nhập tên bánh");
            etTenBanh.requestFocus();
            return;
        }

        if (loaiBanh.isEmpty()) {
            spinnerLoaiBanh.setError("Vui lòng chọn loại bánh");
            spinnerLoaiBanh.requestFocus();
            return;
        }

        if (moTa.isEmpty()) {
            etMoTa.setError("Vui lòng nhập mô tả");
            etMoTa.requestFocus();
            return;
        }

        if (kichCo.isEmpty()) {
            etKichCo.setError("Vui lòng nhập kích thước");
            etKichCo.requestFocus();
            return;
        }

        if (ngayGiao.isEmpty()) {
            etNgayGiao.setError("Vui lòng chọn ngày giao");
            etNgayGiao.requestFocus();
            return;
        }

        if (nguoiNhan.isEmpty()) {
            etNguoiNhan.setError("Vui lòng nhập tên người nhận");
            etNguoiNhan.requestFocus();
            return;
        }

        if (sdt.isEmpty()) {
            etSdt.setError("Vui lòng nhập số điện thoại");
            etSdt.requestFocus();
            return;
        }

        if (sdt.length() < 9 || sdt.length() > 11) {
            etSdt.setError("Số điện thoại không hợp lệ");
            etSdt.requestFocus();
            return;
        }

        // Tạo thông báo thành công với animation
        Toast.makeText(getContext(), "Đã gửi yêu cầu đặt bánh thành công!\nChúng tôi sẽ liên hệ với bạn sớm nhất có thể.", Toast.LENGTH_LONG).show();

        // Reset form sau khi gửi thành công
        resetForm();
    }

    private void resetForm() {
        etTenBanh.setText("");
        etMoTa.setText("");
        etKichCo.setText("");
        etNgayGiao.setText("");
        etNguoiNhan.setText("");
        etSdt.setText("");
        spinnerLoaiBanh.setText("");

        // Reset image
        imgAnhMau.setImageResource(android.R.drawable.ic_menu_gallery);
        imgAnhMau.setAlpha(0.4f);
        imgAnhMau.setPadding(40, 40, 40, 40);
        selectedImageUri = null;
    }
}