package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout layoutIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        layoutIngredients = findViewById(R.id.layoutIngredients);

        int recipeId = getIntent().getIntExtra("RECIPE_ID", -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        Recipe recipe = dbHelper.getRecipeWithIngredients(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        TextView tvRecipeName = findViewById(R.id.tvRecipeName);
        TextView tvServings = findViewById(R.id.tvServings);
        TextView tvInstructions = findViewById(R.id.tvInstructions);

        tvRecipeName.setText(recipe.getName());
        tvServings.setText("Serves " + recipe.getServings());
        tvInstructions.setText(recipe.getInstructions());

        // Dynamically add each ingredient to the layout
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                View ingredientView = LayoutInflater.from(this).inflate(R.layout.item_ingredient_detail, layoutIngredients, false);

                TextView tvIngredientName = ingredientView.findViewById(R.id.tvIngredientName);
                TextView tvIngredientQuantity = ingredientView.findViewById(R.id.tvIngredientQuantity);

                String name = ingredient.getIngredientName();
                if (name != null && !name.isEmpty()) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    tvIngredientName.setText("• " + name);
                } else {
                    tvIngredientName.setText("• Unknown");
                }

                tvIngredientQuantity.setText(String.format("%.1f %s", ingredient.getQuantity(), ingredient.getUnit()));

                layoutIngredients.addView(ingredientView);
            }
        }
    }
}