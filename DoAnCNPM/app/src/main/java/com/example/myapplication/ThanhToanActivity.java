package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Profile.lsdh_order;
import com.example.myapplication.admin.DHitem;
import com.example.myapplication.admin.DonHang;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ThanhToanActivity extends AppCompatActivity {
    private ImageView back;
    private EditText ten, diachiCT, sodiethoai;
    private RadioGroup phuongthucthanhtoan;
    private TextView tvtongtien, tvtiencoc, tvtienconlai;
    private int tongtien = 0, tiencoc = 0, tienconlai = 0;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button pay;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private List<Province> provinceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // Ánh xạ
        back = findViewById(R.id.trove);
        ten = findViewById(R.id.et_receiver_name);
        diachiCT = findViewById(R.id.et_address);
        sodiethoai = findViewById(R.id.et_phone_number);
        phuongthucthanhtoan = findViewById(R.id.payment_options);
        pay = findViewById(R.id.btn_pay);
        tvtongtien = findViewById(R.id.tv_total_amount);
        tvtiencoc = findViewById(R.id.tv_deposit_amount);
        tvtienconlai = findViewById(R.id.tv_remaining_amount);
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerDistrict = findViewById(R.id.spinner_district);
        spinnerWard = findViewById(R.id.spinner_ward);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        loadAddressData();

        tongtien = ghmanager.getInstance().tinhTong();
        tiencoc = tongtien / 2;
        tienconlai = tongtien - tiencoc;
        updatePTTT();

        back.setOnClickListener(v -> finish());

        phuongthucthanhtoan.setOnCheckedChangeListener((group, checkedId) -> pay.setEnabled(checkedId != -1));

        pay.setOnClickListener(v -> {
            String name = ten.getText().toString().trim();
            String dcChiTiet = diachiCT.getText().toString().trim();
            String phone = sodiethoai.getText().toString().trim();

            if (name.isEmpty() || dcChiTiet.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^0[0-9]{9}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            int chon = phuongthucthanhtoan.getCheckedRadioButtonId();
            if (chon != -1) {
                RadioButton slec = findViewById(chon);
                String pttt = slec.getText().toString();
                showPTTHdialog(pttt);
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            }
        });

        pay.setEnabled(false);
    }

    private void loadAddressData() {
        try {
            InputStream is = getAssets().open("address_data.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Type type = new TypeToken<List<Province>>() {}.getType();
            provinceList = gson.fromJson(json, type);

            List<String> provinceNames = new ArrayList<>();
            for (Province p : provinceList) {
                provinceNames.add(p.getName());
            }

            ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provinceNames);
            spinnerProvince.setAdapter(provinceAdapter);

            spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    List<District> districts = provinceList.get(position).getDistricts();
                    List<String> districtNames = new ArrayList<>();
                    for (District d : districts) {
                        districtNames.add(d.getName());
                    }

                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(ThanhToanActivity.this, android.R.layout.simple_spinner_item, districtNames);
                    spinnerDistrict.setAdapter(districtAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int provincePos = spinnerProvince.getSelectedItemPosition();
                    List<District> districts = provinceList.get(provincePos).getDistricts();
                    List<String> wards = districts.get(position).getWards();

                    ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(ThanhToanActivity.this, android.R.layout.simple_spinner_item, wards);
                    spinnerWard.setAdapter(wardAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đọc dữ liệu địa chỉ", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePTTT() {
        tvtongtien.setText(String.format("Tổng tiền: %,dđ", tongtien));
        tvtiencoc.setText(String.format("Tiền cọc (50%%): %,dđ", tiencoc));
        tvtienconlai.setText(String.format("Còn lại: %,dđ", tienconlai));
    }

    private void showPTTHdialog(String pt) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Xác nhận thanh toán cọc");
        alertDialog.setMessage(String.format("Bạn sẽ thanh toán cọc %,dđ bằng %s.\nSố còn lại %,dđ sẽ thanh toán khi nhận hàng.\n\nXác nhận không?", tiencoc, pt, tienconlai));
        alertDialog.setPositiveButton("Xác nhận", (dialog, which) -> LuuDHFB(pt));
        alertDialog.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void LuuDHFB(String payment) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String name = ten.getText().toString().trim();
        String tinh = spinnerProvince.getSelectedItem().toString();
        String huyen = spinnerDistrict.getSelectedItem().toString();
        String xa = spinnerWard.getSelectedItem().toString();
        String dcChiTiet = diachiCT.getText().toString().trim();
        String address = dcChiTiet + ", " + xa + ", " + huyen + ", " + tinh;
        String phone = sodiethoai.getText().toString().trim();

        List<SanPham> cartItems = ghmanager.getInstance().getCartItems();
        List<DHitem> danhSachSanPham = new ArrayList<>();

        for (SanPham sp : cartItems) {
            String hinhAnh = sp.getImageUrl(); // Phải là URL từ Firestore
            Log.d("CHECK_IMAGE", "SP: " + sp.getTen() + " | imageUrl: " + hinhAnh); // ✅ kiểm tra
            danhSachSanPham.add(new DHitem(sp.getTen(), sp.getSoLuong(), sp.getGia(), hinhAnh));
        }

        String maDonHang = db.collection("don_hang").document().getId();

        DonHang donHang = new DonHang();
        donHang.setMaDonHang(maDonHang);
        donHang.setUserId(userId);
        donHang.setTenKhachHang(name);
        donHang.setSoDienThoai(phone);
        donHang.setDiaChi(address);
        donHang.setDanhSachSanPham(danhSachSanPham);
        donHang.setTongTien(tongtien);
        donHang.setTrangThai("cho_xu_ly");
        donHang.setNgayTao(com.google.firebase.Timestamp.now());
        donHang.setNgayCapNhat(com.google.firebase.Timestamp.now());
        donHang.setGhiChu("");
        donHang.setLaBanhTheoYeuCau(false);
        donHang.setLinkAnhMau("");
        donHang.setChiPhiNguyenLieu(0);
        donHang.setThongTinBaoGia("");
        donHang.setDaBaoGia(false);

        // Đơn hàng cho người dùng
        lsdh_order order = new lsdh_order(maDonHang, userId, name, address, phone, payment, tongtien, tiencoc, tienconlai, cartItems, "Pending");

        // Lưu vào Firestore
        db.collection("don_hang").document(maDonHang).set(donHang);
        db.collection("orders").document(maDonHang).set(order)
                .addOnSuccessListener(aVoid -> {
                    ShowTTTC();
                    ghmanager.getInstance().cleatCart();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show());
    }

    private void ShowTTTC() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Thanh toán thành công!");
        alert.setMessage(String.format("Đã thanh toán cọc: %,dđ\nCòn lại: %,dđ sẽ thanh toán khi nhận hàng.", tiencoc, tienconlai));
        alert.setPositiveButton("OK", (dialog, which) -> finish());
        alert.setCancelable(false);
        alert.show();
    }
}
