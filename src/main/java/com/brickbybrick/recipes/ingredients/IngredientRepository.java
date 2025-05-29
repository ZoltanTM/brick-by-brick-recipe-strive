package com.brickbybrick.recipes.ingredients;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer>, JpaSpecificationExecutor<Ingredient> {

    @Query("SELECT i FROM Ingredient i WHERE i.name = :name")
    Optional<Ingredient> findByName(@Param("name") String name);
    //Optional<Object> findByName(String name);
    List<Ingredient> findByNameIn(List<String> names);//
}
