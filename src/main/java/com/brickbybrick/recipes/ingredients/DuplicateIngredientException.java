package com.brickbybrick.recipes.ingredients;

public class DuplicateIngredientException extends RuntimeException {
    public DuplicateIngredientException(String ingredientName) {
        super("Ingredient with name '" + ingredientName + "' already exists.");
    }
}
