package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.admin.SecurityService;
import com.brickbybrick.recipes.admin.Views;
import com.brickbybrick.recipes.ingredients.Ingredient;
import com.brickbybrick.recipes.ingredients.IngredientRepository;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeMapper recipeMapper;

    public List<RecipeDto> findRecipes(List<Integer> ingredientIds, Integer maxPrepTime, Integer maxCookTime) {
        Specification<Recipe> spec = Specification.where(null);

        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            spec = spec.and(RecipeSpecifications.hasIngredients(ingredientIds));
        }
        if (maxPrepTime != null) {
            spec = spec.and(RecipeSpecifications.hasPrepTimeLessThan(maxPrepTime));
        }
        if (maxCookTime != null) {
            spec = spec.and(RecipeSpecifications.hasCookTimeLessThan(maxCookTime));
        }

        return recipeRepository.findAll(spec).stream()
                .map(recipeMapper::toDto)
                .collect(Collectors.toList());
    }

    public RecipeDto createRecipe(RecipeDto recipeDto) {
//        HashSet<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(recipeDto.getIngredientIds()));
//        Recipe recipe = recipeMapper.toEntity(recipeDto, ingredients);
//        Recipe saved = recipeRepository.save(recipe);
//        return recipeMapper.toDto(saved);

//        List<String> ingredientNames = recipeDto.getIngredientNames();
        List<String> ingredientNames = recipeDto.getIngredients().stream()
                .map(IngredientQuantityDto::getIngredientName)
                .collect(Collectors.toList());

        // Load ingredients from DB
        List<Ingredient> ingredients = ingredientRepository.findByNameIn(ingredientNames);

        // Check if we found all requested ingredients
        if (ingredients.size() != ingredientNames.size()) {
            throw new RuntimeException("One or more ingredient names not found.");
        }

        Recipe recipe = recipeMapper.toEntity(recipeDto, ingredients);//new HashSet<>(ingredients));
        Recipe saved = recipeRepository.save(recipe);
        return recipeMapper.toDto(saved);
    }

    public RecipeDto updateRecipe(Integer id, RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id " + id));

        recipe.setName(recipeDto.getName());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setPrepTimeMinutes(recipeDto.getPrepTimeMinutes());
        recipe.setCookTimeMinutes(recipeDto.getCookTimeMinutes());

        // Update ingredients
        //List<Ingredient> ingredients = ingredientRepository.findAllById(recipeDto.getIngredientIds());
        //recipe.setIngredients(new HashSet<>(ingredients));

        // Clear old ingredients
        recipe.getRecipeIngredients().clear();

        // Rebuild recipe-ingredient links
        for (IngredientQuantityDto iqDto : recipeDto.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findByName(iqDto.getIngredientName())
                    .orElseThrow(() -> new EntityNotFoundException("Ingredient not found: " + iqDto.getIngredientName()));

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(iqDto.getQuantity());

            recipe.getRecipeIngredients().add(recipeIngredient);
        }

        Recipe updated = recipeRepository.save(recipe);
        return recipeMapper.toDto(updated);
    }

    public void deleteRecipe(Integer id) {
        recipeRepository.deleteById(id);
    }
}

