package com.brickbybrick.recipes.ingredients;

import org.springframework.stereotype.Component;
import static com.brickbybrick.recipes.admin.RoleChecker.isAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class IngredientMapper {
    private static final Logger log = LoggerFactory.getLogger(IngredientMapper.class);
    public Ingredient toEntity(IngredientDto dto) {
        Ingredient entity = new Ingredient();
        entity.setName(dto.getName());
        entity.setQuantity(dto.getQuantity());
        if (isAdmin()) {
            entity.setCategory(dto.getCategory());
        }
        else if (dto.getCategory() != null) {
            log.warn("Non-admin attempted to set category: " + dto.getCategory());
            dto.setCategory(null);
        }
        return entity;
    }

    public IngredientDto toDto(Ingredient entity) {
        IngredientDto dto = new IngredientDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setQuantity(entity.getQuantity());
        if (isAdmin()) {
            dto.setCategory(entity.getCategory());
        }
        return dto;
    }
}
