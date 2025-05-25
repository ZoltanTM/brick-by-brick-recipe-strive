package com.brickbybrick.recipes.ingredients;

public class IngredientNotFoundException extends RuntimeException {
    public IngredientNotFoundException(Long id) {
        super("Ingredient with ID " + id + " not found.");
    }
}
