package com.example.myapplication.MHSpalsh;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000; // 3 giây
    private View animationBackground;
    private View pulseRing;
    private FrameLayout particlesContainer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Khởi tạo các view
        animationBackground = findViewById(R.id.animationBackground);
        pulseRing = findViewById(R.id.pulseRing);
        particlesContainer = findViewById(R.id.particlesContainer);
        progressBar = findViewById(R.id.progressBar);

        // Khởi động các animation
        startBackgroundAnimation();
        startPulseAnimation();
        startParticleAnimation();
        updateProgressBar();

        // Chuyển sang MainActivity sau SPLASH_DURATION
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    private void startBackgroundAnimation() {
        if (animationBackground != null) {
            AnimationDrawable backgroundAnimation = (AnimationDrawable) animationBackground.getBackground();
            if (backgroundAnimation != null) {
                backgroundAnimation.start();
            }
        }
    }

    private void startPulseAnimation() {
        if (pulseRing != null) {
            try {
                // Sử dụng AnimationUtils thay vì AnimatorInflater
                Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);
                pulseRing.startAnimation(pulseAnimation);
            } catch (Exception e) {
                Log.e("SplashActivity", "Error loading pulse animation: " + e.getMessage());
            }
        }
    }

    private void startParticleAnimation() {
        if (particlesContainer != null) {
            ParticleView particleView = new ParticleView(this);
            particlesContainer.addView(particleView);
        }
    }

    private void updateProgressBar() {
        if (progressBar != null) {
            new Thread(() -> {
                for (int progress = 0; progress <= 100; progress += 5) {
                    try {
                        Thread.sleep(SPLASH_DURATION / 20); // Cập nhật mỗi 150ms
                        int finalProgress = progress;
                        runOnUiThread(() -> progressBar.setProgress(finalProgress));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}