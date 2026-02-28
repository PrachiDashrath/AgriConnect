package com.example.agriconnect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Custom view that renders soft floating leaf/circle particles
 * to give the welcome screen an organic, living feel.
 */
public class ParticleView extends View {

    private static final int PARTICLE_COUNT = 18;

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private long lastFrameTime = 0;

    // Palette: white/green tones at low opacity
    private final int[] colors = {
            Color.argb(40,  255, 255, 255),
            Color.argb(30,  165, 214, 167),
            Color.argb(25,  200, 230, 201),
            Color.argb(20,  255, 255, 255),
    };

    public ParticleView(Context context) { super(context); init(); }
    public ParticleView(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public ParticleView(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init(); }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        particles.clear();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            particles.add(createParticle(w, h, true));
        }
    }

    private Particle createParticle(int w, int h, boolean randomY) {
        Particle p = new Particle();
        p.x     = random.nextFloat() * w;
        p.y     = randomY ? random.nextFloat() * h : h + 40;
        p.size  = 6 + random.nextFloat() * 18;
        p.speedY= 0.3f + random.nextFloat() * 0.6f;
        p.speedX= (random.nextFloat() - 0.5f) * 0.4f;
        p.rotation = random.nextFloat() * 360f;
        p.rotSpeed = (random.nextFloat() - 0.5f) * 0.8f;
        p.color = colors[random.nextInt(colors.length)];
        p.isLeaf= random.nextBoolean();
        p.wobblePhase = random.nextFloat() * (float)(Math.PI * 2);
        return p;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getWidth() == 0) return;

        long now = System.currentTimeMillis();
        float dt = lastFrameTime == 0 ? 16f : Math.min(now - lastFrameTime, 50f);
        lastFrameTime = now;

        for (Particle p : particles) {
            p.wobblePhase += 0.012f;
            float wobble = (float) Math.sin(p.wobblePhase) * 0.8f;

            p.x += (p.speedX + wobble) * dt * 0.06f;
            p.y -= p.speedY * dt * 0.06f;
            p.rotation += p.rotSpeed * dt * 0.06f;

            // Wrap around
            if (p.y < -p.size * 2) {
                Particle fresh = createParticle(getWidth(), getHeight(), false);
                p.x = fresh.x; p.y = fresh.y; p.size = fresh.size;
                p.speedY = fresh.speedY; p.speedX = fresh.speedX;
                p.wobblePhase = fresh.wobblePhase; p.color = fresh.color;
                p.isLeaf = fresh.isLeaf;
            }
            if (p.x < -p.size) p.x = getWidth() + p.size;
            if (p.x > getWidth() + p.size) p.x = -p.size;

            canvas.save();
            canvas.translate(p.x, p.y);
            canvas.rotate(p.rotation);
            paint.setColor(p.color);

            if (p.isLeaf) {
                drawLeaf(canvas, p.size);
            } else {
                canvas.drawCircle(0, 0, p.size * 0.5f, paint);
            }
            canvas.restore();
        }

        postInvalidateOnAnimation();
    }

    private void drawLeaf(Canvas canvas, float size) {
        Path path = new Path();
        path.moveTo(0, -size);
        path.cubicTo(size * 0.8f, -size * 0.5f, size * 0.8f, size * 0.5f, 0, size);
        path.cubicTo(-size * 0.8f, size * 0.5f, -size * 0.8f, -size * 0.5f, 0, -size);
        canvas.drawPath(path, paint);
    }

    private static class Particle {
        float x, y, size, speedX, speedY, rotation, rotSpeed, wobblePhase;
        int color;
        boolean isLeaf;
    }
}