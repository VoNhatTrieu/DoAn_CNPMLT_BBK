package com.example.myapplication.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<QLUSER> userList;
    private final Context context;
    private final OnUserClickListener listener;
    private final Runnable refreshCallback;

    public interface OnUserClickListener {
        void onUserClick(QLUSER user);
    }

    public UserAdapter(Context context, List<QLUSER> userList, OnUserClickListener listener, Runnable refreshCallback) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
        this.refreshCallback = refreshCallback;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ql_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        QLUSER user = userList.get(position);

        holder.tvName.setText(user.name != null ? user.name : "Không rõ");
        holder.tvEmail.setText(user.email != null ? user.email : "Không rõ");
        holder.tvRole.setText(user.role != null ? user.role : "user");

        String dateStr = user.createdAt > 0
                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(user.createdAt))
                : "Không rõ";
        holder.tvCreatedAt.setText(dateStr);

        holder.tvStatus.setText(user.isEmailVerified ? "Hoạt động" : "Bị khóa");
        holder.tvStatus.setTextColor(
                context.getResources().getColor(user.isEmailVerified ? R.color.gradient_start : R.color.gray)
        );

        holder.itemView.setOnClickListener(v -> listener.onUserClick(user));

        holder.btnMenu.setOnClickListener(v -> showPopupMenu(v, user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole, tvCreatedAt, tvStatus;
        ImageView btnMenu;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedDate);
            tvStatus = itemView.findViewById(R.id.tvUserStatus);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }
    }

    private void showPopupMenu(View view, QLUSER user) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.menu_user_options);

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.action_edit_user) {
                Intent intent = new Intent(context, EditUserActivity.class);
                intent.putExtra("uid", user.uid);
                context.startActivity(intent);
                return true;

            } else if (id == R.id.action_disable_user) {
                updateStatus(user.uid, false);
                return true;

            } else if (id == R.id.action_enable_user) {
                updateStatus(user.uid, true);
                return true;

            } else if (id == R.id.action_delete_user) {
                new AlertDialog.Builder(context)
                        .setTitle("Xóa người dùng")
                        .setMessage("Bạn có chắc chắn muốn xóa người dùng này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(user.uid)
                                    .removeValue()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(context, "Đã xóa người dùng", Toast.LENGTH_SHORT).show();
                                        if (refreshCallback != null) refreshCallback.run();
                                    });
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void updateStatus(String uid, boolean active) {
        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .child("isEmailVerified")
                .setValue(active)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(context, active ? "Đã mở khóa" : "Đã khóa tài khoản", Toast.LENGTH_SHORT).show();
                    if (refreshCallback != null) refreshCallback.run();
                });
    }
}
