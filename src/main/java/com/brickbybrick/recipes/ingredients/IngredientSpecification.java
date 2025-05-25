package com.brickbybrick.recipes.ingredients;

import org.springframework.data.jpa.domain.Specification;

public class IngredientSpecification {
    public static Specification<Ingredient> hasNameLike(String name) {
        return (root, query, builder) ->
                name == null ? null : builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Ingredient> hasCategory(String category) {
        return (root, query, builder) ->
                category == null ? null : builder.equal(root.get("category"), category);
    }
}
