package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "SmartPantryPrefs";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SwitchMaterial switchExpiryAlerts = findViewById(R.id.switchExpiryAlerts);

        // Load saved preference
        switchExpiryAlerts.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));

        // Save preference on change
        switchExpiryAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
        });
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_settings);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_pantry) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_recipes) {
                    startActivity(new Intent(this, SuggestedRecipesActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    return true;
                }
                return false;
            });
        }
    }
}