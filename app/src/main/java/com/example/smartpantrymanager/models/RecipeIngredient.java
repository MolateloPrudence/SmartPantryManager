package com.example.smartpantrymanager.models;

public class RecipeIngredient {
    private int id;
    private int recipeId;
    private String ingredientName;
    private double quantity;
    private String unit;

    public RecipeIngredient() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}