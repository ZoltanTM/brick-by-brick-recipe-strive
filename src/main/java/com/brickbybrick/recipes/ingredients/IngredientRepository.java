package com.brickbybrick.recipes.ingredients;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {

    Optional<Object> findByName(String name);
}
