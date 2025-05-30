package com.brickbybrick.recipes.recipes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDto {
    private Integer id;
    private String name;
    private String description;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer totalTimeMinutes;
//    private List<Integer> ingredientIds;
//    private List<String> ingredientNames;

    private List<IngredientQuantityDto> ingredients;

    public void setTotalTimeMinutes(int i) {
    }
}
