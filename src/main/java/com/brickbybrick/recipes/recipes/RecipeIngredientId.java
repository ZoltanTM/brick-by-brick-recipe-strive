package com.brickbybrick.recipes.recipes;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@Embeddable
public class RecipeIngredientId implements Serializable {
    private Long recipeId;
    private Long ingredientId;

}
