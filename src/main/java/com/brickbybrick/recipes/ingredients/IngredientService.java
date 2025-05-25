package com.brickbybrick.recipes.ingredients;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public IngredientService(IngredientRepository repo, IngredientMapper mapper) {
        this.ingredientRepository = repo;
        this.ingredientMapper = mapper;
    }

    public IngredientDto createIngredient(IngredientDto dto) {
        if (ingredientRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateIngredientException(dto.getName());
        }
        Ingredient ingredient = ingredientMapper.toEntity(dto);
        ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(ingredient);
    }

    public IngredientDto getIngredient(Long id) {
        Ingredient ingredient = ingredientRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IngredientNotFoundException(id));
        return ingredientMapper.toDto(ingredient);
    }

    public List<IngredientDto> getIngredients(String name, String category) {
        Specification<Ingredient> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(IngredientSpecification.hasNameLike(name));
        }
        if (category != null) {
            spec = spec.and(IngredientSpecification.hasCategory(category));
        }
        return ingredientRepository.findAll(spec)
                .stream()
                .map(ingredientMapper::toDto)
                .toList();
    }

    public IngredientDto updateIngredient(Long id, IngredientDto dto) {
        if (!ingredientRepository.existsById(Math.toIntExact(id))) {
            throw new IngredientNotFoundException(id);
        }
        Ingredient ingredient = ingredientMapper.toEntity(dto);
        ingredient.setId(Math.toIntExact(id));
        ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(ingredient);
    }

    public void deleteIngredient(Long id) {
        if (!ingredientRepository.existsById(Math.toIntExact(id))) {
            throw new IngredientNotFoundException(id);
        }
        ingredientRepository.deleteById(Math.toIntExact(id));
    }
}
