package com.brickbybrick.recipes.recipes;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Recipe}
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(String message) {
        super(message);
    }
}