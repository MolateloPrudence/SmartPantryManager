package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.RecipeMatcher;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private DatabaseHelper dbHelper;
    private LinearLayout layoutEmptyState;
    private TextView tvEmptyMessage;
    private TabLayout tabLayout;

    private List<Recipe> allRecipes;
    private List<PantryItem> pantryItems;

    private boolean showingAlmostThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        dbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerViewRecipes);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        tabLayout = findViewById(R.id.tabLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add the two tabs programmatically
        tabLayout.addTab(tabLayout.newTab().setText("Can Make Now"));
        tabLayout.addTab(tabLayout.newTab().setText("Almost There"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showingAlmostThere = tab.getPosition() == 1;
                loadRecipes();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes(); // Refresh when returning to this screen
    }

    private void loadRecipes() {
        allRecipes = dbHelper.getAllRecipes();
        pantryItems = dbHelper.getAllPantryItems();

        // Load ingredients for each recipe
        for (Recipe recipe : allRecipes) {
            recipe.setIngredients(dbHelper.getRecipeIngredients(recipe.getId()));
        }

        List<Recipe> displayRecipes;

        if (showingAlmostThere) {
            displayRecipes = RecipeMatcher.getAlmostThereRecipes(allRecipes, pantryItems);
            if (displayRecipes.isEmpty()) {
                showEmptyState("No recipes are close to being ready.\nKeep adding ingredients!");
            } else {
                hideEmptyState();
            }
        } else {
            displayRecipes = RecipeMatcher.getSuggestedRecipes(allRecipes, pantryItems);
            if (displayRecipes.isEmpty()) {
                showEmptyState("No recipes match your pantry yet.\nAdd more ingredients!");
            } else {
                hideEmptyState();
            }
        }

        adapter = new RecipeAdapter(this, displayRecipes, pantryItems, this, showingAlmostThere);
        recyclerView.setAdapter(adapter);
    }

    private void showEmptyState(String message) {
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvEmptyMessage.setText(message);
    }

    private void hideEmptyState() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        // We will create RecipeDetailActivity in the next step, so ignore red text for a moment
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID", recipe.getId());
        startActivity(intent);
    }
}