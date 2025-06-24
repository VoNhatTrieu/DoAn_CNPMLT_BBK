package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.Profile.lsdh_order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ThanhToanActivity extends AppCompatActivity {
    private ImageView back;
    private EditText ten,diachi,sodiethoai;
    private RadioGroup phuongthucthanhtoan;
    private TextView tvtongtien,tvtiencoc,tvtienconlai;
    private  int tongtien=0;
    private  int tiencoc=0;
    private  int tienconlai=0;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);
        back=findViewById(R.id.trove);
        ten=findViewById(R.id.et_receiver_name);
        diachi=findViewById(R.id.et_address);
        sodiethoai=findViewById(R.id.et_phone_number);
        phuongthucthanhtoan=findViewById(R.id.payment_options);
        pay=findViewById(R.id. btn_pay);
        tvtongtien=findViewById(R.id.tv_total_amount);
        tvtiencoc=findViewById(R.id.tv_deposit_amount);
        tvtienconlai=findViewById(R.id.tv_remaining_amount);

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        back.setOnClickListener(v -> {
            Log.d("Trchitietsp","quay về trang chi tiết");
            finish();
        });
        tongtien = ghmanager.getInstance().tinhTong();
        tiencoc = tongtien / 2;
        tienconlai = tongtien - tiencoc;
        PTTTT();


        phuongthucthanhtoan.setOnCheckedChangeListener((group, checkedId) -> {
            pay.setEnabled(checkedId!=-1);
        });
        pay.setOnClickListener(v -> {
                String name=ten.getText().toString().trim();
                String adress=diachi.getText().toString().trim();
                String phone=sodiethoai.getText().toString().trim();
                if(name.isEmpty()||adress.isEmpty()||phone.isEmpty()){
                    Toast.makeText(this,"Vui lòng nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!phone.matches("^0[0-9]{9}$")){
                    Toast.makeText(this,"Số điện thoại không hợp lệ",Toast.LENGTH_SHORT).show();
                }

            int chon=phuongthucthanhtoan.getCheckedRadioButtonId();
            if(chon !=-1){
                RadioButton slec=findViewById(chon);
                String pttt=slec.getText().toString();
                showPTTHdialog(pttt);
            }else {
                Toast.makeText(this,"Vui lòng chọn phương thức thanh toán",Toast.LENGTH_SHORT).show();
            }
        });
        pay.setEnabled(false);
    }
    private void PTTTT(){
        tvtongtien.setText(String.format("Tổng tiền: %,dđ",tongtien));
        tvtiencoc.setText(String.format("Tiền cọc (50%%): %,dđ", tiencoc));
        tvtienconlai.setText(String.format("Còn lại: %,dđ",tienconlai));
    }
    private void showPTTHdialog(String pt){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
        alertDialog.setTitle("Xác nhận thanh toán cọc");
        alertDialog.setMessage(String.format("Bạn sẽ thanh toán cọc %,dđ (50%% tổng đơn hàng) bằng %s.\n\n" +
                "Số tiền còn lại %,dđ sẽ được thanh toán khi nhận hàng.\n\n" +
                "Xác nhận thanh toán?", tongtien,tiencoc,tienconlai));
        alertDialog.setPositiveButton("Xác nhận",(dialog, which) -> {
            LuuDHFB(pt);
        });
        alertDialog.setNegativeButton("Hủy",(dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void LuuDHFB(String payment){
        if(mAuth.getCurrentUser()==null){
            Toast.makeText(this,"Vui lòng đăng nhập để thanh toán",Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();
        String name = ten.getText().toString().trim();
        String address = diachi.getText().toString().trim();
        String phone = sodiethoai.getText().toString().trim();
        List<SanPham> cartItems = ghmanager.getInstance().getCartItems();
        String orderId = db.collection("orders").document().getId();

        lsdh_order order = new lsdh_order(orderId, userId, name, address, phone, payment, tongtien, tiencoc, tienconlai, cartItems, "Pending");
        db.collection("orders").document(orderId).set(order).addOnSuccessListener(aVoid ->{
                ShowTTTC();
            ghmanager.getInstance().cleatCart();

        }).addOnFailureListener(e -> {
            Toast.makeText(this,"Thanh toán thất bại",Toast.LENGTH_SHORT).show();
        });

    }
    private  void ShowTTTC(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setTitle("Thanh toán thành công!");
        alert.setMessage(String.format("Đã thanh toán cọc: %,dđ\n" +
                "Số tiền còn lại: %,dđ (thanh toán khi nhận hàng)\n\n" +
                "Đơn hàng của bạn đang được xử lý.", tiencoc, tienconlai));
        alert.setPositiveButton("OK",(dialog, which) -> {
            finish();
        });
        alert.setCancelable(false);
        alert.show();

    }
}