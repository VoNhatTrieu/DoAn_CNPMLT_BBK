package com.example.myapplication.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {

    private TextInputEditText etName, etPhone, etAddress;
    private RadioGroup rgRole;
    private Switch swStatus;
    private Button btnSave, btnCancel;
    private ImageView btnBack;
    private String uid;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        uid = getIntent().getStringExtra("uid");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        rgRole = findViewById(R.id.rgRole);
        swStatus = findViewById(R.id.swStatus);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnBack=findViewById(R.id.btnBack);
        loadUserInfo();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String role = rgRole.getCheckedRadioButtonId() == R.id.rbAdmin ? "admin" : "user";
            boolean isActive = swStatus.isChecked();

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("phoneNumber", phone);
            updates.put("role", role);
            updates.put("isEmailVerified", isActive);

            userRef.updateChildren(updates).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });

        btnCancel.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUserInfo() {
        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                etName.setText(snapshot.child("name").getValue(String.class));
                etPhone.setText(snapshot.child("phoneNumber").getValue(String.class));
                etAddress.setText(snapshot.child("address").getValue(String.class)); // nếu có
                String role = snapshot.child("role").getValue(String.class);
                boolean isVerified = Boolean.TRUE.equals(snapshot.child("isEmailVerified").getValue(Boolean.class));

                if ("admin".equals(role)) rgRole.check(R.id.rbAdmin);
                else rgRole.check(R.id.rbUser);

                swStatus.setChecked(isVerified);
            }
        });
    }
}