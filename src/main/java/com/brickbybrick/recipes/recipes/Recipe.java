package com.brickbybrick.recipes.recipes;

import com.brickbybrick.recipes.ingredients.Ingredient;
import jakarta.persistence.Entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
@Table(
        name = "recipe",
        uniqueConstraints = {
                @UniqueConstraint(name = "name_UNIQUE", columnNames = {"name"})
        }
)
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(name = "prep_time_minutes")
    private Integer prepTimeMinutes;

    @Column(name = "cook_time_minutes")
    private Integer cookTimeMinutes;

    @ManyToMany
    @JoinTable(
            name = "recipe_ingredients",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private Set<Ingredient> ingredients = new HashSet<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @Transient
    public Integer getTotalTimeMinutes() {
        return (prepTimeMinutes != null ? prepTimeMinutes : 0)
                + (cookTimeMinutes != null ? cookTimeMinutes : 0);
    }

    public Recipe(Long id, String name, String description, Integer prepTimeMinutes, Integer cookTimeMinutes, HashSet<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.ingredients = ingredients;
    }
}