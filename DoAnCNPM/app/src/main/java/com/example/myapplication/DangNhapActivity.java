package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

public class DangNhapActivity extends AppCompatActivity {
    private static final String TAG = "DangNhapActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView btback;
    private TextInputEditText email, password;
    private CheckBox nho;
    private MaterialButton login, gg, fb;
    private TextView qmk, taotk;

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupClickListeners();
        loadSavedLoginInfo();
    }

    private void initViews() {
        btback = findViewById(R.id.btn_back);
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        nho = findViewById(R.id.cbRemember);
        login = findViewById(R.id.btnLogin);
        gg = findViewById(R.id.btnGoogleLogin);
        fb = findViewById(R.id.btnFacebookLogin);
        qmk = findViewById(R.id.tvForgotPassword);
        taotk = findViewById(R.id.tvCreateAccount);
    }

    private void setupClickListeners() {
        btback.setOnClickListener(v -> onBackPressed());
        login.setOnClickListener(v -> Login());
        qmk.setOnClickListener(v -> startActivity(new Intent(this, QuenMKActivity.class)));
        taotk.setOnClickListener(v -> startActivity(new Intent(this, DangKiActivity.class)));
        gg.setOnClickListener(v -> DangNhapGG());
        fb.setOnClickListener(v -> Toast.makeText(this, "Tính năng đăng nhập Facebook đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void DangNhapGG() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        navigateToProfile(user);
                    } else {
                        Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Login() {
        String Email = email.getText() != null ? email.getText().toString().trim() : "";
        String Pw = password.getText() != null ? password.getText().toString().trim() : "";

        if (!kiemTraEmailPassword(Email, Pw)) return;

        mAuth.signInWithEmailAndPassword(Email, Pw)
                .addOnCompleteListener(this, task -> {
                    login.setEnabled(true);
                    login.setText("Đăng nhập");
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (nho.isChecked()) {
                            saveLoginInfo(Email);
                        } else {
                            clearSavedLoginInfo();
                        }
                        navigateToProfile(user);
                    } else {
                        Toast.makeText(this, getErrorMessage(task.getException()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getErrorMessage(Exception exception) {
        if (exception == null) return "Đăng nhập thất bại";
        String err = exception.getMessage();
        if (err != null) {
            if (err.contains("WRONG_PASSWORD")) return "Mật khẩu không đúng";
            if (err.contains("INVALID_EMAIL")) return "Email không đúng";
            if (err.contains("USER_NOT_FOUND")) return "Tài khoản không tồn tại";
            if (err.contains("USER_DISABLED")) return "Tài khoản đã bị vô hiệu hóa";
            if (err.contains("TOO_MANY_REQUESTS")) return "Quá nhiều lần đăng nhập";
        }
        return "Đăng nhập thất bại. Vui lòng thử lại sau";
    }

    private void navigateToProfile(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("USER_EMAIL", user.getEmail());
            intent.putExtra("USER_NAME", user.getDisplayName());
            intent.putExtra("USER_UID", user.getUid());
            intent.putExtra("SELECTED_TAB", R.id.nav_account);
            startActivity(intent);
            finish();
        }
    }

    private boolean kiemTraEmailPassword(String Email, String Password) {
        if (Email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        }
        if (Password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
        if (Password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }
        return true;
    }

    private void saveLoginInfo(String emailText) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, emailText);
        editor.putBoolean(KEY_REMEMBER, true);
        editor.apply();
    }

    private void clearSavedLoginInfo() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_EMAIL);
        editor.putBoolean(KEY_REMEMBER, false);
        editor.apply();
    }

    private void loadSavedLoginInfo() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean(KEY_REMEMBER, false);
        if (rememberMe) {
            String savedEmail = prefs.getString(KEY_EMAIL, "");
            if (!savedEmail.isEmpty()) {
                email.setText(savedEmail);
                nho.setChecked(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedLoginInfo();
    }
}
