package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientDto {
    @JsonView(Views.UserView.class)
    private Integer id;

    @JsonView(Views.UserView.class)
    @NotBlank(message = "Name is required")
    private String name;

    @JsonView(Views.UserView.class)
    @Min(value = 0, message = "Quantity must be positive")
    private int quantity;

    @JsonView(Views.AdminView.class)
    private String category;
}
