package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.ingredients.Ingredient;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class RecipeSpecifications {

    public static Specification<Recipe> hasIngredient(Integer ingredientId) {
        return (root, query, builder) -> {
            if (ingredientId == null) return null;
            Join<Recipe, Ingredient> ingredients = root.join("ingredients");
            return builder.equal(ingredients.get("id"), ingredientId);
        };
    }

    public static Specification<Recipe> prepTimeLessThan(Integer minutes) {
        return (root, query, builder) ->
                minutes == null ? null : builder.lessThan(root.get("prepTimeMinutes"), minutes);
    }

    public static Specification<Recipe> cookTimeLessThan(Integer minutes) {
        return (root, query, builder) ->
                minutes == null ? null : builder.lessThan(root.get("cookTimeMinutes"), minutes);
    }

    public static Specification<Recipe> hasNameLike(String name) {
        return (root, query, builder) ->
                name == null ? null : builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Recipe> hasIngredients(List<Integer> ingredientIds) {
        return (root, query, builder) -> {
            Join<Recipe, Ingredient> join = root.join("ingredients");
            return join.get("id").in(ingredientIds);
        };
    }

    public static Specification<Recipe> hasPrepTimeLessThan(Integer maxPrepTime) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("prepTimeMinutes"), maxPrepTime);
    }

    public static Specification<Recipe> hasCookTimeLessThan(Integer maxCookTime) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get("cookTimeMinutes"), maxCookTime);
    }
}