package com.example.smartpantrymanager;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ImageView ivFoodIcon = findViewById(R.id.ivFoodIcon);
        TextView tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle);
        Button btnGetStarted = findViewById(R.id.btnGetStarted);

        // --- ANIMATION SETUP ---

        // 1. Set initial states (invisible and shifted up)
        ivFoodIcon.setAlpha(0f);
        ivFoodIcon.setTranslationY(-100f);

        tvWelcomeTitle.setAlpha(0f);
        tvWelcomeTitle.setTranslationY(50f);

        // 2. Animate the food icon: Fade in and slide down
        ivFoodIcon.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 3. Animate the title: Fade in and slide up
        tvWelcomeTitle.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(600) // Starts slightly after the icon
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // --- BUTTON CLICK ---
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}