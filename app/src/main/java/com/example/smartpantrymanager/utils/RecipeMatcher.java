package com.example.smartpantrymanager.utils;

import android.util.Log;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    private static final String TAG = "RecipeMatcher";

    /**
     * Main method: Returns list of recipes that can be made with current pantry
     */
    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> suggestedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantryItems)) {
                suggestedRecipes.add(recipe);
            }
        }
        return suggestedRecipes;
    }

    /**
     * Check if a single recipe can be made with the pantry
     * STRICT: Returns true ONLY if ALL ingredients are available
     */
    public static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        List<RecipeIngredient> requiredIngredients = recipe.getIngredients();

        if (requiredIngredients == null || requiredIngredients.isEmpty()) {
            return false;
        }

        for (RecipeIngredient required : requiredIngredients) {
            if (!hasIngredientInPantry(required, pantryItems)) {
                return false; // One missing ingredient = recipe excluded completely
            }
        }
        return true; // All ingredients present
    }

    /**
     * Check if a specific required ingredient is in the pantry with enough quantity
     */
    private static boolean hasIngredientInPantry(RecipeIngredient required, List<PantryItem> pantryItems) {
        String normalizedRequiredName = normalizeIngredientName(required.getIngredientName());

        for (PantryItem pantryItem : pantryItems) {
            String normalizedPantryName = normalizeIngredientName(pantryItem.getName());

            if (normalizedRequiredName.equals(normalizedPantryName)) {
                // Name matches - now check quantity
                if (hasEnoughQuantity(required, pantryItem)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if pantry has enough quantity of the ingredient
     */
    private static boolean hasEnoughQuantity(RecipeIngredient required, PantryItem pantryItem) {
        double requiredQty = required.getQuantity();
        double availableQty = pantryItem.getQuantity();

        String requiredUnit = normalizeUnit(required.getUnit());
        String availableUnit = normalizeUnit(pantryItem.getUnit());

        if (requiredUnit.equals(availableUnit)) {
            return availableQty >= requiredQty;
        }

        double convertedQty = convertUnits(availableQty, availableUnit, requiredUnit);
        if (convertedQty >= 0) {
            return convertedQty >= requiredQty;
        }

        // If units are incompatible, just check if we have any
        return availableQty > 0;
    }

    /**
     * Normalize ingredient names (handles case, whitespace, and plurals)
     */
    public static String normalizeIngredientName(String name) {
        if (name == null) return "";
        String normalized = name.toLowerCase(Locale.ROOT).trim();
        normalized = normalized.replaceAll("\\s+", " ");
        return handlePluralForms(normalized);
    }

    private static String handlePluralForms(String name) {
        if (name.equals("tomatoes")) return "tomato";
        if (name.equals("potatoes")) return "potato";
        if (name.equals("eggs")) return "egg";
        if (name.equals("onions")) return "onion";
        if (name.equals("carrots")) return "carrot";
        if (name.equals("bananas")) return "banana";
        if (name.equals("strawberries")) return "strawberry";
        if (name.equals("cloves")) return "clove";
        if (name.equals("slices")) return "slice";
        if (name.equals("pieces")) return "piece";
        if (name.equals("tablespoons")) return "tablespoon";
        if (name.equals("teaspoons")) return "teaspoon";
        if (name.equals("grams")) return "gram";
        if (name.equals("kilograms")) return "kilogram";
        if (name.equals("milliliters")) return "milliliter";
        if (name.equals("liters")) return "liter";
        if (name.equals("cups")) return "cup";
        if (name.equals("heads")) return "head";

        if (name.endsWith("es") && name.length() > 3) {
            String singular = name.substring(0, name.length() - 2);
            if (!singular.endsWith("s") && !name.equals("cheese") && !name.equals("sauce")) {
                return singular;
            }
        }
        if (name.endsWith("s") && !name.endsWith("ss") && name.length() > 2) {
            if (!name.equals("rice") && !name.equals("pasta") && !name.equals("hummus") && !name.equals("asparagus")) {
                return name.substring(0, name.length() - 1);
            }
        }
        return name;
    }

    /**
     * Normalize units for comparison
     */
    public static String normalizeUnit(String unit) {
        if (unit == null || unit.isEmpty()) return "";
        String normalized = unit.toLowerCase(Locale.ROOT).trim();

        switch (normalized) {
            case "g":
            case "gr": return "gram";
            case "kg": return "kilogram";
            case "ml": return "milliliter";
            case "l":
            case "lt": return "liter";
            case "tbsp":
            case "tbs": return "tablespoon";
            case "tsp": return "teaspoon";
            case "pc":
            case "pcs": return "piece";
            default: return normalized;
        }
    }

    /**
     * Convert units
     */
    private static double convertUnits(double quantity, String fromUnit, String toUnit) {
        if (fromUnit.equals(toUnit)) return quantity;

        if (fromUnit.equals("kilogram") && toUnit.equals("gram")) return quantity * 1000;
        if (fromUnit.equals("gram") && toUnit.equals("kilogram")) return quantity / 1000;
        if (fromUnit.equals("liter") && toUnit.equals("milliliter")) return quantity * 1000;
        if (fromUnit.equals("milliliter") && toUnit.equals("liter")) return quantity / 1000;
        if (fromUnit.equals("tablespoon") && toUnit.equals("teaspoon")) return quantity * 3;
        if (fromUnit.equals("teaspoon") && toUnit.equals("tablespoon")) return quantity / 3;

        return -1; // Cannot convert
    }

    // --- BONUS: "Almost There" feature logic ---

    public static List<Recipe> getAlmostThereRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> almostThere = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            if (countMissingIngredients(recipe, pantryItems) == 1) {
                almostThere.add(recipe);
            }
        }
        return almostThere;
    }

    public static int countMissingIngredients(Recipe recipe, List<PantryItem> pantryItems) {
        int missing = 0;
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!hasIngredientInPantry(required, pantryItems)) {
                missing++;
            }
        }
        return missing;
    }

    public static String getMissingIngredientName(Recipe recipe, List<PantryItem> pantryItems) {
        for (RecipeIngredient required : recipe.getIngredients()) {
            if (!hasIngredientInPantry(required, pantryItems)) {
                return required.getIngredientName();
            }
        }
        return null;
    }
}