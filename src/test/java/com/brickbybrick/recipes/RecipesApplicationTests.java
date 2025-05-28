package com.brickbybrick.recipes;

import com.brickbybrick.recipes.admin.SecurityService;
import com.brickbybrick.recipes.ingredients.*;
//import com.brickbybrick.recipes.SecurityTestConfig;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@AutoConfigureMockMvc
@SpringBootTest
class RecipesApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @Qualifier("mockIngredientRepository")
    //@Mock
    private IngredientRepository ingredientRepository;
//    @Bean(name = "mockIngredientRepository")
//    public IngredientRepository ingredientRepository() {
//        return Mockito.mock(IngredientRepository.class);
//    }

    @Autowired
    @Qualifier("mockIngredientMapper")
//    @Mock
    private IngredientMapper ingredientMapper;
//    @Bean(name = "mockIngredientMapper")
//    public IngredientMapper ingredientMapper() {
//        return Mockito.mock(IngredientMapper.class);
//    }

    //@Qualifier("mockSecurityService")
//    @Mock //Autowired doesn't work
    @Autowired
    private SecurityService securityService;

    @Autowired
    private IngredientService ingredientService;

    @TestConfiguration
    static class MockConfig {
        @Bean(name = "mockIngredientRepository")
        public IngredientRepository ingredientRepository() {
            return Mockito.mock(IngredientRepository.class);
        }

        @Bean(name = "mockIngredientMapper")
        public IngredientMapper ingredientMapper() {
            return Mockito.mock(IngredientMapper.class);
        }

        @Bean
        public SecurityService securityService() {
            return Mockito.mock(SecurityService.class);
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public IngredientService ingredientService(
                @Qualifier("mockIngredientRepository") IngredientRepository ingredientRepository,
                @Qualifier("mockIngredientMapper") IngredientMapper ingredientMapper,
                SecurityService securityService) {
            return new IngredientService(ingredientRepository, ingredientMapper, securityService);
        }
    }

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void setup() {
        //ingredientService = new IngredientService(ingredientRepository, ingredientMapper, securityService);

        Ingredient publicIngredient1 = new Ingredient(1, "butter", 2, "dairy");
        Ingredient privateIngredient = new Ingredient(2, "secret sauce", 4, "special");
        Ingredient publicIngredient3 = new Ingredient(3, "sugar", 6, "pantry");

        when(ingredientRepository.findById(1))
                .thenReturn(Optional.of(publicIngredient1));

        when(ingredientRepository.findById(2))
                .thenReturn(Optional.of(privateIngredient));

        when(ingredientRepository.findById(3))
                .thenReturn(Optional.of(publicIngredient3));

        when(ingredientRepository.findAll())
                .thenReturn(List.of(publicIngredient1, privateIngredient, publicIngredient3));

        when(ingredientRepository.existsById(1))
                .thenReturn(true);
        when(ingredientRepository.existsById(3))
                .thenReturn(true);

        //Test 12 correction
        when(ingredientMapper.toEntity(any(IngredientDto.class)))
                .thenAnswer(invocation -> {
                    IngredientDto dto = invocation.getArgument(0);
                    int id = dto.getId() != null ? dto.getId() : 0;
                    return new Ingredient(id, dto.getName(), dto.getQuantity(), dto.getCategory());
                });

        // Mock IngredientMapper
        when(ingredientMapper.toDto(publicIngredient1))
                .thenReturn(new IngredientDto(1, "butter", 2, "dairy"));
        when(ingredientMapper.toDto(privateIngredient))
                .thenReturn(new IngredientDto(2, "secret sauce", 4, "special"));
        when(ingredientMapper.toDto(publicIngredient3))
                .thenReturn(new IngredientDto(3, "sugar", 6, "pantry"));

        // Mock SecurityService for unauthenticated or user role
        when(securityService.isCurrentUserAdmin()).thenReturn(false);

        // Add specification mocks
        //Version when I had simulated 2 ingredients, 1 public, one private
//        when(ingredientRepository.findAll(any(Specification.class)))
//                .thenAnswer(invocation -> {
//                    Specification<Ingredient> spec = invocation.getArgument(0);
//                    // Simulate specification logic
//                    if (!spec.toString().contains("category=special")) {
//                        return List.of(publicIngredient);
//                    }
//                    // For non-admins, exclude "special" category
//                    if (!securityService.isCurrentUserAdmin()) {
//                        return List.of(publicIngredient);
//                    }
//                    return List.of(publicIngredient, privateIngredient);
//                });
        // Mock findAll with Specification
        when(ingredientRepository.findAll(any(Specification.class)))
                .thenAnswer(invocation -> {
                    Specification<Ingredient> spec = invocation.getArgument(0);
                    // If category=special is requested and user is not admin, exclude private ingredients
                    if (spec.toString().contains("category=special") && !securityService.isCurrentUserAdmin()) {
                        return List.of(); // Return empty list for non-admins requesting special category
                    }
                    // For no category filter, return public ingredients only for non-admins
                    if (!securityService.isCurrentUserAdmin()) {
                        return List.of(publicIngredient1, publicIngredient3); // Return butter and sugar
                    }
                    // For admins, return all ingredients
                    return List.of(publicIngredient1, privateIngredient, publicIngredient3);
                });
    }

    @Test
    void whenGetAllIngredientsWithoutAuth_thenOnlyNamesVisible() throws Exception {
        mockMvc.perform(get("/ingredient"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].category").doesNotExist());
    }

    @Test
    void whenGetPublicIngredient1WithoutAuth_thenNoCategory() throws Exception {
        mockMvc.perform(get("/ingredient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("butter"))  // example name
                .andExpect(jsonPath("$.category").doesNotExist());
    }

    @Test
    void whenGetPrivateIngredient2WithoutAuth_thenForbiddenOrNotFound() throws Exception {

        mockMvc.perform(get("/ingredient/2"))
                .andExpect(status().isNotFound());//Unauthorized//Forbidden());  // or `.isNotFound()` depending on your policy
    }

    @Test
    void whenPostIngredientWithoutAuth_thenUnauthorized() throws Exception {
        String ingredientJson = """
        {
            "name": "test",
            "quantity": 1,
            "category": "test"
        }
    """;

        mockMvc.perform(post("/ingredient")
                        .contentType("application/json")
                        .content(ingredientJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenUpdateIngredientWithoutAuth_thenForbidden() throws Exception {
        String updatedJson = """
        {
            "name": "updated",
            "quantity": 2,
            "category": "updated"
        }
        """;

        mockMvc.perform(put("/ingredient/1")
                        .contentType("application/json")
                        .content(updatedJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void whenDeleteIngredientWithoutAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(delete("/ingredient/1"))
                .andExpect(status().isUnauthorized());
    }

    // Admin-specific tests
    @Test
    void whenGetAllIngredientsAsAdmin_thenCategoryVisible() throws Exception {
        // Override SecurityService for admin role
        when(securityService.isCurrentUserAdmin()).thenReturn(true);

        mockMvc.perform(get("/ingredient"))//api //s
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].category").value("dairy"))
                .andExpect(jsonPath("$[1].category").value("special"));
    }

    @Test
    void whenGetPublicIngredient1AsAdmin_thenCategoryVisible() throws Exception {
        // Override SecurityService for admin role
        when(securityService.isCurrentUserAdmin()).thenReturn(true);

        mockMvc.perform(get("/ingredient/1"))//api //s
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("butter"))
                .andExpect(jsonPath("$.category").value("dairy"));
    }

    @Test
    void whenGetPrivateIngredient2AsAdmin_thenAccessible() throws Exception {
        // Override SecurityService for admin role
        when(securityService.isCurrentUserAdmin()).thenReturn(true);

        mockMvc.perform(get("/ingredient/2"))//api //s
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("secret sauce"))
                .andExpect(jsonPath("$.category").value("special"));
    }

    @Test
    void whenFilterByCategoryAsAdmin_thenAllowed() throws Exception {
        // Override SecurityService for admin role
        when(securityService.isCurrentUserAdmin()).thenReturn(true);

        mockMvc.perform(get("/ingredient?category=dairy"))//api//s
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("butter"))
                .andExpect(jsonPath("$[0].category").value("dairy"));
    }

    @Test
    void whenFilterByCategoryAsUser_thenNoRestrictedCategories() throws Exception {
        mockMvc.perform(get("/ingredient?category=dairy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category").value(
                        Matchers.everyItem(Matchers.not("special"))
                ));
    }
}
