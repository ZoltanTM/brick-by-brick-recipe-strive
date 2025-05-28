package com.brickbybrick.recipes.ingredients;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer>, JpaSpecificationExecutor<Ingredient> {

    Optional<Object> findByName(String name);//
}
