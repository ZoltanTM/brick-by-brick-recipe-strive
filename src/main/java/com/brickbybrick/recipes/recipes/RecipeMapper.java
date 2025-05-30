package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.ingredients.Ingredient;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {
    private final RecipeRepository recipeRepository;

    public RecipeMapper(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public Recipe toEntity(RecipeDto dto, List<Ingredient> ingredients) { //Set alternative
        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setName(dto.getName());
        recipe.setDescription(dto.getDescription());
        recipe.setPrepTimeMinutes(dto.getPrepTimeMinutes());
        recipe.setCookTimeMinutes(dto.getCookTimeMinutes());
        //recipe.setIngredients(ingredients);  // assumes you load them from DB by IDs

        Set<RecipeIngredient> recipeIngredients = dto.getIngredients().stream()
                .map(iqDto -> {
                    Ingredient ingredient = ingredients.stream()
                            .filter(ing -> ing.getName().equalsIgnoreCase(iqDto.getIngredientName()))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Ingredient not found: " + iqDto.getIngredientName()));

                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipe(recipe);
                    recipeIngredient.setIngredient(ingredient);
                    recipeIngredient.setQuantity(iqDto.getQuantity());
                    return recipeIngredient;
                })
                .collect(Collectors.toSet());

        recipe.setRecipeIngredients(recipeIngredients);
        return recipe;
    }

    public RecipeDto toDto(Recipe recipe) {
        RecipeDto dto = new RecipeDto();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setPrepTimeMinutes(recipe.getPrepTimeMinutes());
        dto.setCookTimeMinutes(recipe.getCookTimeMinutes());
        dto.setTotalTimeMinutes(recipe.getPrepTimeMinutes() + recipe.getCookTimeMinutes());
//        dto.setIngredientIds(
//                recipe.getIngredients().stream().map(Ingredient::getId).collect(Collectors.toList())
//        );

        int prep = recipe.getPrepTimeMinutes() != null ? recipe.getPrepTimeMinutes() : 0;
        int cook = recipe.getCookTimeMinutes() != null ? recipe.getCookTimeMinutes() : 0;
        dto.setTotalTimeMinutes(prep + cook);

        dto.setIngredients(
                recipe.getRecipeIngredients().stream()
                        .map(ri -> {
                            IngredientQuantityDto iqDto = new IngredientQuantityDto();
                            iqDto.setIngredientName(ri.getIngredient().getName());
                            iqDto.setQuantity(ri.getQuantity());
                            return iqDto;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }
}
