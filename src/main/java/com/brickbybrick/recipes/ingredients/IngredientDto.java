package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.Views;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
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

    // Getters and setters
//    public Integer getId() { return id; }
//    public void setId(Integer id) { this.id = id; }
//
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//
//    public int getQuantity() { return quantity; }
//    public void setQuantity(int quantity) { this.quantity = quantity; }
}
