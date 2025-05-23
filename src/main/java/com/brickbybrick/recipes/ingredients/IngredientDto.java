package com.brickbybrick.recipes.ingredients;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
public class IngredientDto {

    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 0, message = "Quantity must be positive")
    private int quantity;

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
