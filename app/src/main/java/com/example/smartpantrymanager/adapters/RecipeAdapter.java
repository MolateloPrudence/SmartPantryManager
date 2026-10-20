package com.example.smartpantrymanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.utils.RecipeMatcher;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipes;
    private List<PantryItem> pantryItems;
    private OnRecipeClickListener listener;
    private boolean showMissingIngredient;

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, List<Recipe> recipes, List<PantryItem> pantryItems,
                         OnRecipeClickListener listener, boolean showMissingIngredient) {
        this.context = context;
        this.recipes = recipes;
        this.pantryItems = pantryItems;
        this.listener = listener;
        this.showMissingIngredient = showMissingIngredient;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        holder.tvRecipeName.setText(recipe.getName());
        holder.tvIngredientCount.setText(recipe.getIngredients().size() + " ingredients");
        holder.tvServings.setText("Serves " + recipe.getServings());

        if (showMissingIngredient) {
            String missing = RecipeMatcher.getMissingIngredientName(recipe, pantryItems);
            if (missing != null) {
                holder.tvMissing.setVisibility(View.VISIBLE);
                holder.tvMissing.setText("Missing: " + missing);
            } else {
                holder.tvMissing.setVisibility(View.GONE);
            }
        } else {
            holder.tvMissing.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipeClick(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName, tvIngredientCount, tvServings, tvMissing;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvIngredientCount = itemView.findViewById(R.id.tvIngredientCount);
            tvServings = itemView.findViewById(R.id.tvServings);
            tvMissing = itemView.findViewById(R.id.tvMissing);
        }
    }
}