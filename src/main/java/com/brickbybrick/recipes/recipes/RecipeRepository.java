package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.ingredients.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    Optional<Object> findByName(String name);

    @Query("SELECT DISTINCT r FROM Recipe r " +
            "JOIN r.recipeIngredients ri " +
            "JOIN ri.ingredient i " +
            "WHERE (:ingredientNames IS NULL OR i.name IN :ingredientNames) " +
            "AND (:maxPrepTime IS NULL OR r.prepTimeMinutes <= :maxPrepTime) " +
            "AND (:maxCookTime IS NULL OR r.cookTimeMinutes <= :maxCookTime)")
    List<Recipe> findWithFilters(
            @Param("ingredientNames") List<String> ingredientNames,
            @Param("maxPrepTime") Integer maxPrepTime,
            @Param("maxCookTime") Integer maxCookTime
    );
}
