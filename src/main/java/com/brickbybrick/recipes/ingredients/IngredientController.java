package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.RoleChecker;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

//@PreAuthorize("hasRole('ADMIN')")
@RestController
public class IngredientController {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final IngredientService ingredientService;

    public IngredientController(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper, IngredientService ingredientService) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.ingredientService = ingredientService;
    }

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping("/ingredient")
    public ResponseEntity<?> createIngredient(@RequestBody IngredientDto dto) {
        try {
            IngredientDto saved = ingredientService.createIngredient(dto);
            return ResponseEntity.ok(saved);
        } catch (DuplicateIngredientException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Duplicate");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredient(@PathVariable Integer id) {
        return ingredientRepository.findById(id)
                .map(ingredient -> ResponseEntity.ok(ingredientMapper.toDto(ingredient)))
                .orElse(ResponseEntity.notFound().build());
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
