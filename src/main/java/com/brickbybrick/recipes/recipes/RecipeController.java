package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.admin.SecurityService;
import com.brickbybrick.recipes.admin.Views;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
//@RequestMapping("/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public List<RecipeDto> listRecipes(
            @RequestParam(required = false) List<Integer> ingredientIds,
            @RequestParam(required = false) Integer maxPrepTime,
            @RequestParam(required = false) Integer maxCookTime
    ) {
        return recipeService.findRecipes(ingredientIds, maxPrepTime, maxCookTime);
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
    public void deleteRecipe(@PathVariable Integer id) {
        recipeService.deleteRecipe(id);
    }
}

