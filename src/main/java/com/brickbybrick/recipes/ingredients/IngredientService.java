package com.brickbybrick.recipes.ingredients;

import com.brickbybrick.recipes.admin.SecurityService;
import com.brickbybrick.recipes.admin.Views;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;
    private final SecurityService securityService;

    public IngredientService(IngredientRepository repo, IngredientMapper mapper, SecurityService securityService) {
        this.ingredientRepository = repo;
        this.ingredientMapper = mapper;
        this.securityService = securityService;
    }

    public IngredientDto createIngredient(IngredientDto dto) {
        if (ingredientRepository.findByName(dto.getName()).isPresent()) {
            throw new DuplicateIngredientException(dto.getName());
        }
        Ingredient ingredient = ingredientMapper.toEntity(dto);
        ingredientRepository.save(ingredient);
        return ingredientMapper.toDto(ingredient);
    }

    public MappingJacksonValue getIngredient(@PathVariable Long id) {//Long id //IngredientDto
//        Ingredient ingredient = ingredientRepository.findById(Math.toIntExact(id))
//                .orElseThrow(() -> new IngredientNotFoundException(id));
//        return ingredientMapper.toDto(ingredient);
        //v2
//        Ingredient ingredient = ingredientRepository.findById(Math.toIntExact(id))
//                .orElseThrow(() -> new IngredientNotFoundException(id));
//        return ingredientMapper.toDto(ingredient);
        Ingredient ingredient = ingredientRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new IngredientNotFoundException(id));

        IngredientDto dto = ingredientMapper.toDto(ingredient);

        MappingJacksonValue wrapper = new MappingJacksonValue(dto);
        Class<?> view = securityService.isCurrentUserAdmin() ? Views.AdminView.class : Views.UserView.class;
        wrapper.setSerializationView(view);

        return wrapper;
    }

    @JsonView(Views.UserView.class)
    public MappingJacksonValue getIngredients(String name, String category) {//public List<IngredientDto> getIngredients(String name, String category)
        boolean isAdmin = securityService.isCurrentUserAdmin(); //apparently this is always true? It has a false condition

        //prior version, without hidden special fields
//        if (!isAdmin) {
//            category = null;
//        }

        Specification<Ingredient> spec = Specification.where(null);
        if (name != null) {
            spec = spec.and(IngredientSpecification.hasNameLike(name));
        }
        if (category != null) {//if (category != null && isAdmin)
            spec = spec.and(IngredientSpecification.hasCategory(category));
        }

        if (!isAdmin) {
            spec = spec.and(IngredientSpecification.notSpecialCategory());
        }

        List<IngredientDto> results = ingredientRepository.findAll(spec)
                .stream()
                .map(ingredientMapper::toDto)
                .toList();

//        if (!isAdmin) {
//            results.forEach(dto -> dto.setCategory(null));
//        }

        MappingJacksonValue mapping = new MappingJacksonValue(results);
        mapping.setSerializationView(isAdmin ? Views.AdminView.class : Views.UserView.class);

        return mapping;//results;
    }

    public IngredientDto getIngredientById(Integer id) {
        //the alternating tests version
//        Ingredient ingredient = ingredientRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//
//        if ("special".equalsIgnoreCase(ingredient.getCategory())) {
//            if (!securityService.isCurrentUserAdmin()) {//!currentUserHasAccess()
//                // Either:
//                //throw new ResponseStatusException(HttpStatus.FORBIDDEN);
//                // OR, if you want to hide it:
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//            }
//        }
//
//        return ingredientMapper.toDto(ingredient);
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if ("special".equalsIgnoreCase(ingredient.getCategory()) && !securityService.isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ingredientMapper.toDto(ingredient);
    }

//    private boolean currentUserHasAccess() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return auth != null && auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//    }

    public IngredientDto updateIngredient(Long id, IngredientDto dto) {
        if (!ingredientRepository.existsById(Math.toIntExact(id))) {
            throw new IngredientNotFoundException(id);
        }
        if (!securityService.isCurrentUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
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
