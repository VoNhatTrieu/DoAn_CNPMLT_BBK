package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Profile.lsdh_order;
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
    private int tongtien = 0;
    private int tiencoc = 0;
    private int tienconlai = 0;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button pay;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private List<Province> provinceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        back = findViewById(R.id.trove);
        ten = findViewById(R.id.et_receiver_name);
        diachiCT = findViewById(R.id.et_address); // địa chỉ chi tiết
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

        back.setOnClickListener(v -> {
            Log.d("Trchitietsp", "quay về trang chi tiết");
            finish();
        });

        tongtien = ghmanager.getInstance().tinhTong();
        tiencoc = tongtien / 2;
        tienconlai = tongtien - tiencoc;
        updatePTTT();

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
            int size = is.available();
            byte[] buffer = new byte[size];
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
            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
                    wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        alertDialog.setMessage(String.format("Bạn sẽ thanh toán cọc %,dđ (50%% tổng đơn hàng) bằng %s.\n\nSố tiền còn lại %,dđ sẽ được thanh toán khi nhận hàng.\n\nXác nhận thanh toán?", tiencoc, pt, tienconlai));
        alertDialog.setPositiveButton("Xác nhận", (dialog, which) -> LuuDHFB(pt));
        alertDialog.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void LuuDHFB(String payment) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thanh toán", Toast.LENGTH_SHORT).show();
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
        String orderId = db.collection("orders").document().getId();

        lsdh_order order = new lsdh_order(orderId, userId, name, address, phone, payment, tongtien, tiencoc, tienconlai, cartItems, "Pending");
        db.collection("orders").document(orderId).set(order)
                .addOnSuccessListener(aVoid -> {
                    ShowTTTC();
                    ghmanager.getInstance().cleatCart();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show());
    }

    private void ShowTTTC() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Thanh toán thành công!");
        alert.setMessage(String.format("Đã thanh toán cọc: %,dđ\nSố tiền còn lại: %,dđ (thanh toán khi nhận hàng)\n\nĐơn hàng của bạn đang được xử lý.", tiencoc, tienconlai));
        alert.setPositiveButton("OK", (dialog, which) -> finish());
        alert.setCancelable(false);
        alert.show();
    }
}


