package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThanhToanActivity extends AppCompatActivity {
    private ImageView back;
    private EditText ten,diachi,sodiethoai;
    private RadioGroup phuongthucthanhtoan;
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

        back.setOnClickListener(v -> {
            Log.d("Trchitietsp","quay về trang chi tiết");
            finish();
        });
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
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(this);
                alertDialog.setTitle("Thanh toán thành công");

                alertDialog.setPositiveButton("OK",(dialog,which)->{
                    finish();
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }else {
                Toast.makeText(this,"Vui lòng chọn phương thức thanh toán",Toast.LENGTH_SHORT).show();
            }
        });
        pay.setEnabled(false);
    }
}