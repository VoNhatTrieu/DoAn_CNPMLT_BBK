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
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.Calendar;

public class OrdersFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    public TextInputEditText etTenBanh, etMoTa, etKichCo, etNgayGiao, etNguoiNhan, etSdt;
    public Spinner spinnerLoaiBanh;
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
        ImageView btnBack = view.findViewById(R.id.btn_back);

        // Khởi tạo spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.loai_banh_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoaiBanh.setAdapter(adapter);

        // Chọn ngày
        etNgayGiao.setOnClickListener(v -> showDatePicker());

        // Chọn ảnh
        btnChonAnh.setOnClickListener(v -> openImagePicker());

        // Gửi yêu cầu
        btnDatBanh.setOnClickListener(v -> submitForm());

        // Quay lại
        btnBack.setOnClickListener(v -> {
            // Điều hướng về HomeFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();

            // Đổi mục được chọn trong BottomNavigationView
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // nav_home là id của item "Trang chủ"
        });



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
        String loaiBanh = spinnerLoaiBanh.getSelectedItem().toString();

        if (tenBanh.isEmpty() || moTa.isEmpty() || kichCo.isEmpty() || ngayGiao.isEmpty() || nguoiNhan.isEmpty() || sdt.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi yêu cầu (ở đây chỉ hiển thị thông báo mẫu)
        Toast.makeText(getContext(), "Đã gửi yêu cầu đặt bánh!", Toast.LENGTH_LONG).show();
    }
}
