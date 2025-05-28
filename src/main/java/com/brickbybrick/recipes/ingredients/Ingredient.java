package com.brickbybrick.recipes.ingredients;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
@Table(
        name = "ingredient",
        uniqueConstraints = {
                @UniqueConstraint(name = "name_UNIQUE", columnNames = {"name"})
        }
)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;

    @Min(value = 0, message = "You can't have a negative quantity")
    private int quantity;

    @Column(name = "category")
    private String category;

    public Ingredient(int id, String name, int quantity, String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }
}
