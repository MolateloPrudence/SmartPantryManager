package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;
import com.example.smartpantrymanager.models.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Info
    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Pantry Table Columns
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe Table Columns
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_INSTRUCTIONS = "instructions";
    public static final String COL_SERVINGS = "servings";

    // Recipe Ingredients Columns
    public static final String COL_RECIPE_ID = "recipe_id";
    public static final String COL_INGREDIENT_NAME = "ingredient_name";

    // Create Tables SQL
    private static final String CREATE_TABLE_PANTRY = "CREATE TABLE " + TABLE_PANTRY + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_NAME + " TEXT NOT NULL,"
            + COL_QUANTITY + " REAL NOT NULL,"
            + COL_UNIT + " TEXT,"
            + COL_EXPIRY + " TEXT"
            + ")";

    private static final String CREATE_TABLE_RECIPES = "CREATE TABLE " + TABLE_RECIPES + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_RECIPE_NAME + " TEXT NOT NULL,"
            + COL_INSTRUCTIONS + " TEXT,"
            + COL_SERVINGS + " INTEGER DEFAULT 2"
            + ")";

    private static final String CREATE_TABLE_RECIPE_INGREDIENTS = "CREATE TABLE "
            + TABLE_RECIPE_INGREDIENTS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COL_RECIPE_ID + " INTEGER,"
            + COL_INGREDIENT_NAME + " TEXT NOT NULL,"
            + COL_QUANTITY + " REAL NOT NULL,"
            + COL_UNIT + " TEXT,"
            + "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PANTRY);
        db.execSQL(CREATE_TABLE_RECIPES);
        db.execSQL(CREATE_TABLE_RECIPE_INGREDIENTS);

        // Seed initial recipes
        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==================== PANTRY CRUD OPERATIONS ====================

    public long addPantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().toLowerCase().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());

        long id = db.insert(TABLE_PANTRY, null, values);
        db.close();
        return id;
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PANTRY + " ORDER BY " + COL_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
                item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public PantryItem getPantryItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        PantryItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new PantryItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
            item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
            item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
            cursor.close();
        }
        db.close();
        return item;
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName().toLowerCase().trim());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());

        int rowsAffected = db.update(TABLE_PANTRY, values, COL_ID + "=?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rowsAffected;
    }

    public void deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public PantryItem findPantryItemByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PANTRY, null, COL_NAME + "=?",
                new String[]{name.toLowerCase().trim()}, null, null, null);

        PantryItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new PantryItem();
            item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
            item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            item.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
            item.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
            item.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY)));
            cursor.close();
        }
        db.close();
        return item;
    }

    // ==================== RECIPE OPERATIONS ====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_RECIPES + " ORDER BY " + COL_RECIPE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                recipe.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
                recipe.setInstructions(cursor.getString(cursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
                recipe.setServings(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SERVINGS)));
                recipes.add(recipe);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recipes;
    }

    public Recipe getRecipeWithIngredients(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor recipeCursor = db.query(TABLE_RECIPES, null, COL_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);

        Recipe recipe = null;
        if (recipeCursor != null && recipeCursor.moveToFirst()) {
            recipe = new Recipe();
            recipe.setId(recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(COL_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_RECIPE_NAME)));
            recipe.setInstructions(recipeCursor.getString(recipeCursor.getColumnIndexOrThrow(COL_INSTRUCTIONS)));
            recipe.setServings(recipeCursor.getInt(recipeCursor.getColumnIndexOrThrow(COL_SERVINGS)));
            recipeCursor.close();
        }

        if (recipe != null) {
            List<RecipeIngredient> ingredients = new ArrayList<>();
            Cursor ingCursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                    COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)},
                    null, null, null);

            if (ingCursor.moveToFirst()) {
                do {
                    RecipeIngredient ing = new RecipeIngredient();
                    ing.setId(ingCursor.getInt(ingCursor.getColumnIndexOrThrow(COL_ID)));
                    ing.setRecipeId(ingCursor.getInt(ingCursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
                    ing.setIngredientName(ingCursor.getString(ingCursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME)));
                    ing.setQuantity(ingCursor.getDouble(ingCursor.getColumnIndexOrThrow(COL_QUANTITY)));
                    ing.setUnit(ingCursor.getString(ingCursor.getColumnIndexOrThrow(COL_UNIT)));
                    ingredients.add(ing);
                } while (ingCursor.moveToNext());
            }
            ingCursor.close();
            recipe.setIngredients(ingredients);
        }

        db.close();
        return recipe;
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_RECIPE_INGREDIENTS, null,
                COL_RECIPE_ID + "=?", new String[]{String.valueOf(recipeId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                RecipeIngredient ing = new RecipeIngredient();
                ing.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                ing.setRecipeId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_RECIPE_ID)));
                ing.setIngredientName(cursor.getString(cursor.getColumnIndexOrThrow(COL_INGREDIENT_NAME)));
                ing.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                ing.setUnit(cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIT)));
                ingredients.add(ing);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return ingredients;
    }

    // ==================== SEED DATA ====================

    private void seedRecipes(SQLiteDatabase db) {
        long r1 = insertRecipe(db, "Scrambled Eggs", "1. Crack eggs.\n2. Whisk.\n3. Cook in butter.\n4. Season.", 2);
        insertRecipeIngredient(db, r1, "egg", 3, "pieces");
        insertRecipeIngredient(db, r1, "butter", 1, "tablespoon");
        insertRecipeIngredient(db, r1, "salt", 1, "pinch");
        insertRecipeIngredient(db, r1, "pepper", 1, "pinch");

        long r2 = insertRecipe(db, "Tomato Pasta", "1. Boil pasta.\n2. Fry tomatoes and garlic.\n3. Mix.", 2);
        insertRecipeIngredient(db, r2, "pasta", 200, "grams");
        insertRecipeIngredient(db, r2, "tomato", 3, "pieces");
        insertRecipeIngredient(db, r2, "olive oil", 2, "tablespoons");
        insertRecipeIngredient(db, r2, "garlic", 2, "cloves");

        long r3 = insertRecipe(db, "Cheese Omelette", "1. Beat eggs.\n2. Cook in pan.\n3. Add cheese.\n4. Fold.", 1);
        insertRecipeIngredient(db, r3, "egg", 2, "pieces");
        insertRecipeIngredient(db, r3, "cheese", 50, "grams");
        insertRecipeIngredient(db, r3, "butter", 1, "tablespoon");
        insertRecipeIngredient(db, r3, "salt", 1, "pinch");

        long r4 = insertRecipe(db, "Vegetable Stir Fry", "1. Chop veg.\n2. Fry in oil.\n3. Add soy sauce.", 3);
        insertRecipeIngredient(db, r4, "carrot", 2, "pieces");
        insertRecipeIngredient(db, r4, "broccoli", 1, "head");
        insertRecipeIngredient(db, r4, "bell pepper", 1, "piece");
        insertRecipeIngredient(db, r4, "soy sauce", 2, "tablespoons");
        insertRecipeIngredient(db, r4, "oil", 2, "tablespoons");

        long r5 = insertRecipe(db, "Pancakes", "1. Mix dry and wet.\n2. Cook on pan.\n3. Flip.", 4);
        insertRecipeIngredient(db, r5, "flour", 200, "grams");
        insertRecipeIngredient(db, r5, "milk", 250, "ml");
        insertRecipeIngredient(db, r5, "egg", 2, "pieces");
        insertRecipeIngredient(db, r5, "sugar", 2, "tablespoons");
        insertRecipeIngredient(db, r5, "butter", 2, "tablespoons");

        long r6 = insertRecipe(db, "Garlic Bread", "1. Mix butter and garlic.\n2. Spread on bread.\n3. Bake.", 4);
        insertRecipeIngredient(db, r6, "bread", 4, "slices");
        insertRecipeIngredient(db, r6, "butter", 3, "tablespoons");
        insertRecipeIngredient(db, r6, "garlic", 3, "cloves");

        long r7 = insertRecipe(db, "Fruit Smoothie", "1. Add all to blender.\n2. Blend.", 2);
        insertRecipeIngredient(db, r7, "banana", 1, "piece");
        insertRecipeIngredient(db, r7, "strawberry", 100, "grams");
        insertRecipeIngredient(db, r7, "yogurt", 150, "ml");
        insertRecipeIngredient(db, r7, "honey", 1, "tablespoon");

        long r8 = insertRecipe(db, "Grilled Cheese", "1. Butter bread.\n2. Add cheese.\n3. Grill.", 1);
        insertRecipeIngredient(db, r8, "bread", 2, "slices");
        insertRecipeIngredient(db, r8, "cheese", 50, "grams");
        insertRecipeIngredient(db, r8, "butter", 1, "tablespoon");

        long r9 = insertRecipe(db, "Onion Soup", "1. Fry onions.\n2. Add broth.\n3. Simmer.", 4);
        insertRecipeIngredient(db, r9, "onion", 4, "pieces");
        insertRecipeIngredient(db, r9, "butter", 2, "tablespoons");
        insertRecipeIngredient(db, r9, "broth", 1, "liter");
        insertRecipeIngredient(db, r9, "salt", 1, "pinch");

        long r10 = insertRecipe(db, "Rice Pudding", "1. Cook rice in milk.\n2. Add sugar.\n3. Simmer.", 4);
        insertRecipeIngredient(db, r10, "rice", 200, "grams");
        insertRecipeIngredient(db, r10, "milk", 500, "ml");
        insertRecipeIngredient(db, r10, "sugar", 3, "tablespoons");
        insertRecipeIngredient(db, r10, "cinnamon", 1, "teaspoon");

        long r11 = insertRecipe(db, "Tomato Soup", "1. Fry onions.\n2. Add tomatoes and broth.\n3. Blend.", 4);
        insertRecipeIngredient(db, r11, "tomato", 6, "pieces");
        insertRecipeIngredient(db, r11, "onion", 1, "piece");
        insertRecipeIngredient(db, r11, "garlic", 2, "cloves");
        insertRecipeIngredient(db, r11, "broth", 500, "ml");

        long r12 = insertRecipe(db, "French Toast", "1. Dip bread in egg mix.\n2. Fry in butter.", 2);
        insertRecipeIngredient(db, r12, "bread", 4, "slices");
        insertRecipeIngredient(db, r12, "egg", 2, "pieces");
        insertRecipeIngredient(db, r12, "milk", 100, "ml");
        insertRecipeIngredient(db, r12, "butter", 2, "tablespoons");

        long r13 = insertRecipe(db, "Pasta Salad", "1. Cook pasta.\n2. Mix with veg and oil.", 4);
        insertRecipeIngredient(db, r13, "pasta", 250, "grams");
        insertRecipeIngredient(db, r13, "tomato", 2, "pieces");
        insertRecipeIngredient(db, r13, "cucumber", 1, "piece");
        insertRecipeIngredient(db, r13, "olive oil", 3, "tablespoons");

        long r14 = insertRecipe(db, "Mashed Potatoes", "1. Boil potatoes.\n2. Mash with butter and milk.", 4);
        insertRecipeIngredient(db, r14, "potato", 4, "pieces");
        insertRecipeIngredient(db, r14, "butter", 2, "tablespoons");
        insertRecipeIngredient(db, r14, "milk", 100, "ml");
        insertRecipeIngredient(db, r14, "salt", 1, "pinch");

        long r15 = insertRecipe(db, "Banana Bread", "1. Mix everything.\n2. Bake at 180C for 45 mins.", 8);
        insertRecipeIngredient(db, r15, "banana", 3, "pieces");
        insertRecipeIngredient(db, r15, "flour", 250, "grams");
        insertRecipeIngredient(db, r15, "egg", 2, "pieces");
        insertRecipeIngredient(db, r15, "sugar", 150, "grams");
        insertRecipeIngredient(db, r15, "butter", 100, "grams");

        long r16 = insertRecipe(db, "Cheese Quesadilla", "1. Add cheese to tortilla.\n2. Cook in pan.", 2);
        insertRecipeIngredient(db, r16, "tortilla", 2, "pieces");
        insertRecipeIngredient(db, r16, "cheese", 100, "grams");
        insertRecipeIngredient(db, r16, "butter", 1, "tablespoon");

        long r17 = insertRecipe(db, "Vegetable Curry", "1. Fry onions.\n2. Add veg and coconut milk.\n3. Simmer.", 4);
        insertRecipeIngredient(db, r17, "onion", 2, "pieces");
        insertRecipeIngredient(db, r17, "potato", 2, "pieces");
        insertRecipeIngredient(db, r17, "carrot", 2, "pieces");
        insertRecipeIngredient(db, r17, "coconut milk", 400, "ml");
        insertRecipeIngredient(db, r17, "curry powder", 2, "tablespoons");

        long r18 = insertRecipe(db, "Lemonade", "1. Squeeze lemons.\n2. Add water and sugar.", 4);
        insertRecipeIngredient(db, r18, "lemon", 4, "pieces");
        insertRecipeIngredient(db, r18, "sugar", 4, "tablespoons");
        insertRecipeIngredient(db, r18, "water", 1, "liter");

        long r19 = insertRecipe(db, "Egg Fried Rice", "1. Scramble eggs.\n2. Add rice and soy sauce.", 4);
        insertRecipeIngredient(db, r19, "rice", 300, "grams");
        insertRecipeIngredient(db, r19, "egg", 2, "pieces");
        insertRecipeIngredient(db, r19, "soy sauce", 2, "tablespoons");
        insertRecipeIngredient(db, r19, "carrot", 1, "piece");

        long r20 = insertRecipe(db, "Mug Cake", "1. Mix in mug.\n2. Microwave 2 mins.", 1);
        insertRecipeIngredient(db, r20, "flour", 4, "tablespoons");
        insertRecipeIngredient(db, r20, "sugar", 2, "tablespoons");
        insertRecipeIngredient(db, r20, "cocoa powder", 2, "tablespoons");
        insertRecipeIngredient(db, r20, "milk", 3, "tablespoons");
        insertRecipeIngredient(db, r20, "oil", 2, "tablespoons");
    }

    private long insertRecipe(SQLiteDatabase db, String name, String instructions, int servings) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_NAME, name);
        values.put(COL_INSTRUCTIONS, instructions);
        values.put(COL_SERVINGS, servings);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private long insertRecipeIngredient(SQLiteDatabase db, long recipeId, String ingredientName, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(COL_RECIPE_ID, recipeId);
        values.put(COL_INGREDIENT_NAME, ingredientName.toLowerCase().trim());
        values.put(COL_QUANTITY, quantity);
        values.put(COL_UNIT, unit);
        return db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }
}