package com.example.agriconnect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity {

    private LinearLayout contentContainer, bottomCard, statsRow;
    private MaterialButton btnGetStarted;
    private TextView tvSignIn;
    private View decorCircleTop, decorCircleBottom, dividerLine, logoGlowRing;
    private ParticleView particleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_welcome);

        initViews();
        startEntranceAnimations();
        setupClickListeners();
    }

    private void initViews() {
        contentContainer = findViewById(R.id.contentContainer);
        bottomCard       = findViewById(R.id.bottomCard);
        statsRow         = findViewById(R.id.statsRow);
        btnGetStarted    = findViewById(R.id.btnGetStarted);
        tvSignIn         = findViewById(R.id.tvSignIn);
        decorCircleTop   = findViewById(R.id.decorCircleTop);
        decorCircleBottom= findViewById(R.id.decorCircleBottom);
        dividerLine      = findViewById(R.id.dividerLine);
        logoGlowRing     = findViewById(R.id.logoGlowRing);
        particleView     = findViewById(R.id.particleView);
    }

    // ─────────────────────────────────────────────
    //  Entrance animation sequence
    // ─────────────────────────────────────────────
    private void startEntranceAnimations() {

        // 1. Decorative circles fade + scale in
        animateDecorCircles();

        // 2. Content: fade + rise after 300 ms
        contentContainer.postDelayed(() -> {
            ObjectAnimator fadeIn  = ObjectAnimator.ofFloat(contentContainer, "alpha", 0f, 1f);
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(contentContainer, "translationY", 60f, 0f);
            fadeIn.setDuration(700);
            slideUp.setDuration(700);
            slideUp.setInterpolator(new DecelerateInterpolator(2f));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(fadeIn, slideUp);
            set.start();
        }, 300);

        // 3. Divider line expand after content lands (1100 ms total)
        dividerLine.postDelayed(() -> {
            ObjectAnimator expand = ObjectAnimator.ofFloat(dividerLine, "scaleX", 0f, 1f);
            expand.setDuration(600);
            expand.setInterpolator(new DecelerateInterpolator());
            expand.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationStart(Animator animation) {
                    dividerLine.setVisibility(View.VISIBLE);
                }
            });
            expand.start();
        }, 1100);

        // 4. Stats row pop-in after 1400 ms
        statsRow.postDelayed(() -> {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(statsRow, "alpha", 0f, 1f);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(statsRow, "scaleX", 0.7f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(statsRow, "scaleY", 0.7f, 1f);
            fadeIn.setDuration(500);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            scaleX.setInterpolator(new OvershootInterpolator(1.4f));
            scaleY.setInterpolator(new OvershootInterpolator(1.4f));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(fadeIn, scaleX, scaleY);
            set.start();
        }, 1400);

        // 5. Bottom card slide up after 600 ms
        bottomCard.postDelayed(() -> {
            ObjectAnimator fadeIn  = ObjectAnimator.ofFloat(bottomCard, "alpha", 0f, 1f);
            ObjectAnimator slideUp = ObjectAnimator.ofFloat(bottomCard, "translationY", 100f, 0f);
            fadeIn.setDuration(700);
            slideUp.setDuration(700);
            slideUp.setInterpolator(new DecelerateInterpolator(2f));
            AnimatorSet set = new AnimatorSet();
            set.playTogether(fadeIn, slideUp);
            set.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    startLoopAnimations();
                }
            });
            set.start();
        }, 600);

        // 6. Particles fade in after 500 ms
        particleView.postDelayed(() -> {
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(particleView, "alpha", 0f, 1f);
            fadeIn.setDuration(1000);
            fadeIn.start();
        }, 500);
    }

    private void animateDecorCircles() {
        for (View v : new View[]{decorCircleTop, decorCircleBottom}) {
            long delay = (v == decorCircleBottom) ? 200L : 0L;
            v.postDelayed(() -> {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                ObjectAnimator scale  = ObjectAnimator.ofFloat(v, "scaleX", 0.3f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 0.3f, 1f);
                fadeIn.setDuration(900);
                scale.setDuration(900);
                scaleY.setDuration(900);
                scale.setInterpolator(new DecelerateInterpolator(2f));
                scaleY.setInterpolator(new DecelerateInterpolator(2f));
                AnimatorSet set = new AnimatorSet();
                set.playTogether(fadeIn, scale, scaleY);
                set.start();
            }, delay);
        }
    }

    // ─────────────────────────────────────────────
    //  Looping ambient animations (after entrance)
    // ─────────────────────────────────────────────
    private void startLoopAnimations() {
        // Glow ring pulse
        ValueAnimator glow = ValueAnimator.ofFloat(0.3f, 0.7f);
        glow.setDuration(2200);
        glow.setRepeatCount(ValueAnimator.INFINITE);
        glow.setRepeatMode(ValueAnimator.REVERSE);
        glow.setInterpolator(new AccelerateDecelerateInterpolator());
        glow.addUpdateListener(a -> logoGlowRing.setAlpha((Float) a.getAnimatedValue()));
        glow.start();

        // Button gentle pulse scale
        ValueAnimator btnPulse = ValueAnimator.ofFloat(1f, 1.025f);
        btnPulse.setDuration(1800);
        btnPulse.setRepeatCount(ValueAnimator.INFINITE);
        btnPulse.setRepeatMode(ValueAnimator.REVERSE);
        btnPulse.setInterpolator(new AccelerateDecelerateInterpolator());
        btnPulse.addUpdateListener(a -> {
            float scale = (Float) a.getAnimatedValue();
            btnGetStarted.setScaleX(scale);
            btnGetStarted.setScaleY(scale);
        });
        btnPulse.start();

        // Decor circles slow rotate
        ObjectAnimator rotateTop    = ObjectAnimator.ofFloat(decorCircleTop, "rotation", 0f, 360f);
        rotateTop.setDuration(25000);
        rotateTop.setRepeatCount(ValueAnimator.INFINITE);
        rotateTop.setInterpolator(null); // linear
        rotateTop.start();

        ObjectAnimator rotateBottom = ObjectAnimator.ofFloat(decorCircleBottom, "rotation", 360f, 0f);
        rotateBottom.setDuration(20000);
        rotateBottom.setRepeatCount(ValueAnimator.INFINITE);
        rotateBottom.setInterpolator(null);
        rotateBottom.start();
    }

    // ─────────────────────────────────────────────
    //  Click listeners with tactile feedback
    // ─────────────────────────────────────────────
    private void setupClickListeners() {
        btnGetStarted.setOnClickListener(v -> {
            // Quick press-scale feedback
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction(() ->
                    v.animate().scaleX(1f).scaleY(1f).setDuration(80).withEndAction(() -> {
                        navigateToSelection();
                    }).start()
            ).start();
        });

        if (tvSignIn != null) {
            tvSignIn.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
            });
        }
    }

    private void navigateToSelection() {
        // Shared element-style exit: content shrinks & fades
        AnimatorSet exit = new AnimatorSet();
        ObjectAnimator fadeOut  = ObjectAnimator.ofFloat(contentContainer, "alpha", 1f, 0f);
        ObjectAnimator scaleX   = ObjectAnimator.ofFloat(contentContainer, "scaleX", 1f, 0.9f);
        ObjectAnimator scaleY   = ObjectAnimator.ofFloat(contentContainer, "scaleY", 1f, 0.9f);
        ObjectAnimator bottomFade = ObjectAnimator.ofFloat(bottomCard, "alpha", 1f, 0f);
        fadeOut.setDuration(350);
        scaleX.setDuration(350);
        scaleY.setDuration(350);
        bottomFade.setDuration(350);
        exit.playTogether(fadeOut, scaleX, scaleY, bottomFade);
        exit.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.scale_out);
                finish();
            }
        });
        exit.start();
    }
}