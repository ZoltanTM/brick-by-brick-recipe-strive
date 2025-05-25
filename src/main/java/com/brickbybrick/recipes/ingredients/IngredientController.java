package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.RoleChecker;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<IngredientDto>> getIngredients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category
    ) {
        List<IngredientDto> results = ingredientService.getIngredients(name, category);
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

//    //Before Service
//    private final IngredientRepository ingredientRepository;
//    private final IngredientMapper ingredientMapper;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public IngredientController(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
//        this.ingredientRepository = ingredientRepository;
//        this.ingredientMapper = ingredientMapper;
//    }
//
//    @PostMapping("/ingredient")
//    public ResponseEntity<?> createIngredient( //IngredientDto
//            @RequestBody @Valid IngredientDto ingredientDto,
//            UriComponentsBuilder uriBuilder) {
//
//        if (ingredientRepository.findByName(ingredientDto.getName()).isPresent()) {
//            return ResponseEntity
//                    .status(HttpStatus.CONFLICT)
//                    .body("An ingredient with the same name already exists.");
//        }
//
//        var ingredient = ingredientMapper.toEntity(ingredientDto);
//        ingredientRepository.save(ingredient);
//
//        var savedDto = ingredientMapper.toDto(ingredient);
//        var uri = uriBuilder.path("/ingredient/{id}").buildAndExpand(savedDto.getId()).toUri();
//
//        return ResponseEntity.created(uri).body(savedDto);
//    }
//
////    @PostMapping("/ingredient")
////    @Transactional
////    public String addIngredient(@RequestBody @Valid Ingredient ingredient) {
////        entityManager.persist(ingredient);
////        return "Ingredient saved!";
////    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<IngredientDto> getIngredient(@PathVariable Integer id) {
//        return ingredientRepository.findById(id)
//                .map(ingredient -> ResponseEntity.ok(ingredientMapper.toDto(ingredient)))
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/ingredient")
//    public ResponseEntity<List<IngredientDto>> getIngredients(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String category
//    ) {
//        Specification<Ingredient> spec = Specification.where(null);
//
//        if (name != null) {
//            spec = spec.and(IngredientSpecification.hasNameLike(name));
//        }
//        if (category != null) {
//            spec = spec.and(IngredientSpecification.hasCategory(category));
//        }
//
////        if (filterDto.getCategory() != null && RoleChecker.isAdmin()) {
////            spec = spec.and(IngredientSpecification.hasCategory(filterDto.getCategory()));
////        }
//
//        List<IngredientDto> results = ingredientRepository.findAll(spec)
//                .stream()
//                .map(ingredientMapper::toDto)
//                .toList();
//
//        return ResponseEntity.ok(results);
//    }
//
////    public List<Ingredient> getIngredients() {
////        return entityManager.createQuery("SELECT i FROM Ingredient i", Ingredient.class).getResultList();
////    }
//
//    @PutMapping("/ingredient/{id}")
//    public ResponseEntity<IngredientDto> updateIngredient(
//            @PathVariable Long id,
//            @RequestBody @Valid IngredientDto ingredientDto) {
//
//        if (!ingredientRepository.existsById(Math.toIntExact(id))) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Ingredient ingredient = ingredientMapper.toEntity(ingredientDto);
//        ingredient.setId(Math.toIntExact(id)); // Ensure the ID matches the path
//        ingredientRepository.save(ingredient);
//
//        IngredientDto updatedDto = ingredientMapper.toDto(ingredient);
//        return ResponseEntity.ok(updatedDto);
//    }
//
//    @DeleteMapping("/ingredient/{id}")
//    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
//        if (!ingredientRepository.existsById(Math.toIntExact(id))) {
//            return ResponseEntity.notFound().build();
//        }
//
//        ingredientRepository.deleteById(Math.toIntExact(id));
//        return ResponseEntity.noContent().build();
//    }
//}

