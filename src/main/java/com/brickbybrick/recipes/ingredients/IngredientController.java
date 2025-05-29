package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.SecurityService;
import com.brickbybrick.recipes.admin.Views;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

//@PreAuthorize("hasRole('ADMIN')")
@RestController
@SecurityRequirement(name = "basicAuth") //For Swagger
public class IngredientController {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final IngredientService ingredientService;
    private final SecurityService securityService;

    public IngredientController(IngredientRepository ingredientRepository,
                                IngredientMapper ingredientMapper,
                                IngredientService ingredientService,
                                SecurityService securityService) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.ingredientService = ingredientService;
        this.securityService = securityService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/ingredient")
    public ResponseEntity<?> createIngredient(@RequestBody IngredientDto dto) {
        try {
            IngredientDto saved = ingredientService.createIngredient(dto);
            return ResponseEntity.ok(saved);
        } catch (DuplicateIngredientException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate");
        }
    }

    @GetMapping("/ingredient/{id}")//at first, didn't have ingredient
    public ResponseEntity<MappingJacksonValue> getIngredient(@PathVariable Integer id) {//IngredientDto //Long
        IngredientDto ingredientDto = ingredientService.getIngredientById(id);

        Class<?> view = securityService.isCurrentUserAdmin()
                ? Views.AdminView.class
                : Views.UserView.class;

        MappingJacksonValue wrapper = new MappingJacksonValue(ingredientDto);

//        if (!securityService.isCurrentUserAdmin()) {
//            SimpleFilterProvider filters = new SimpleFilterProvider()
//                    .addFilter("ingredientFilter", SimpleBeanPropertyFilter.serializeAllExcept("category"));
//            wrapper.setFilters(filters);
//        }
        wrapper.setSerializationView(view);

        return ResponseEntity.ok(wrapper);
    }

    @GetMapping("/ingredient")
    public ResponseEntity<MappingJacksonValue> getIngredients(//List<IngredientDto>
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category
    ) {
        //List<IngredientDto> results = ingredientService.getIngredients(name, category);
        MappingJacksonValue results = ingredientService.getIngredients(name, category);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/ingredient/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(
            @PathVariable Long id,
            @RequestBody @Valid IngredientDto dto) {
        IngredientDto updated = ingredientService.updateIngredient(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/ingredient/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
