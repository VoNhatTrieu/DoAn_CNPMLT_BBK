package com.example.myapplication.MHSpalsh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class ParticleView extends View {
    private final ArrayList<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint();
    private final Random random = new Random();
    private static final int PARTICLE_COUNT = 20;
    private static final float MAX_SPEED = 5f;
    private static final float MAX_RADIUS = 10f;

    public ParticleView(Context context) {
        super(context);
        init();
    }

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#FFCCBC")); // Màu hồng nhạt
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(new Particle());
        }
        post(animateRunnable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Particle particle : particles) {
            paint.setAlpha((int) (particle.alpha * 255));
            canvas.drawCircle(particle.x, particle.y, particle.radius, paint);
        }
    }

    private final Runnable animateRunnable = new Runnable() {
        @Override
        public void run() {
            for (Particle particle : particles) {
                particle.update(getWidth(), getHeight());
            }
            invalidate();
            postDelayed(this, 16); // ~60fps
        }
    };

    private class Particle {
        float x, y, vx, vy, radius, alpha;

        Particle() {
            reset();
        }

        void reset() {
            x = random.nextFloat() * getWidth();
            y = random.nextFloat() * getHeight();
            vx = (random.nextFloat() - 0.5f) * MAX_SPEED;
            vy = (random.nextFloat() - 0.5f) * MAX_SPEED;
            radius = 2 + random.nextFloat() * MAX_RADIUS;
            alpha = 0.3f + random.nextFloat() * 0.5f;
        }

        void update(int width, int height) {
            x += vx;
            y += vy;
            if (x < 0 || x > width) vx = -vx;
            if (y < 0 || y > height) vy = -vy;
            alpha -= 0.001f;
            if (alpha < 0) reset();
        }
    }
}