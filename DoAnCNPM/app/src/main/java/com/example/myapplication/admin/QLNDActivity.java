package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QLNDActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvUserCount;
    private EditText etSearch;
    private ImageView btnSearch;
    private LinearLayout llEmptyState;
    private ImageView btnBack;
    private List<QLUSER> userList = new ArrayList<>();
    private UserAdapter adapter;

    private static final int REQUEST_USER_DETAILS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qlndactivity);

        rvUsers = findViewById(R.id.rvUsers);
        progressBar = findViewById(R.id.progressBar);
        tvUserCount = findViewById(R.id.tvUserCount);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        llEmptyState = findViewById(R.id.llEmptyState);
        btnBack = findViewById(R.id.btnBack);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, userList, user -> {
            Intent intent = new Intent(QLNDActivity.this, UserDetailsActivity.class);
            intent.putExtra("uid", user.uid);
            startActivityForResult(intent, REQUEST_USER_DETAILS);
        }, this::loadUsers); // callback refresh

        rvUsers.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim().toLowerCase();
            filterUsers(keyword);
        });

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            QLUSER user = userSnap.getValue(QLUSER.class);
                            if (user != null) userList.add(user);
                        }

                        adapter.notifyDataSetChanged();
                        tvUserCount.setText("Tổng số người dùng: " + userList.size());

                        rvUsers.setVisibility(userList.isEmpty() ? View.GONE : View.VISIBLE);
                        llEmptyState.setVisibility(userList.isEmpty() ? View.VISIBLE : View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void filterUsers(String keyword) {
        List<QLUSER> filtered = new ArrayList<>();
        for (QLUSER user : userList) {
            if (user.email != null && user.email.toLowerCase().contains(keyword)) {
                filtered.add(user);
            }
        }

        UserAdapter filteredAdapter = new UserAdapter(this, filtered, user -> {
            Intent intent = new Intent(QLNDActivity.this, UserDetailsActivity.class);
            intent.putExtra("uid", user.uid);
            startActivityForResult(intent, REQUEST_USER_DETAILS);
        }, this::loadUsers);

        rvUsers.setAdapter(filteredAdapter);
        rvUsers.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
        llEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USER_DETAILS) {
            loadUsers(); // refresh lại danh sách khi quay về
        }
    }
}
