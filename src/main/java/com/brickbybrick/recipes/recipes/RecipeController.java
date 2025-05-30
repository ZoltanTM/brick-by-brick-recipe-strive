package com.brickbybrick.recipes.recipes;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
//@RequestMapping("/recipes")
//@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.recipeMapper = recipeMapper;
    }

    @GetMapping("/recipe")
    public List<RecipeDto> listRecipes(
            @RequestParam(required = false) List<String> ingredientNames,
            //@RequestParam(required = false) List<Integer> ingredientIds,
            @RequestParam(required = false) Integer maxPrepTime,
            @RequestParam(required = false) Integer maxCookTime
    ) {
        //return recipeService.findRecipes(ingredientIds, maxPrepTime, maxCookTime);
        List<Recipe> recipes = recipeService.findRecipesWithFilters(ingredientNames, maxPrepTime, maxCookTime);
        return recipes.stream().map(recipeMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/recipe/{id}")
    public RecipeDto getRecipeById(@PathVariable Integer id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return recipeMapper.toDto(recipe);
    }

    @PostMapping("/recipe")
    public RecipeDto createRecipe(@RequestBody RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    @PutMapping("/recipe/{id}")
    public RecipeDto updateRecipe(@PathVariable Integer id, @RequestBody RecipeDto recipeDto) {
        return recipeService.updateRecipe(id, recipeDto);
    }

    @DeleteMapping("/recipe/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteRecipe(@PathVariable Integer id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
        //recipeService.deleteRecipe(id);
    }
}

