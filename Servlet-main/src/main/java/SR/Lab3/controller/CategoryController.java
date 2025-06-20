package SR.Lab3.controller;

import SR.Lab3.entity.Category;
import SR.Lab3.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/category")
@Validated
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.readAll();
            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("count", categories.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения категорий: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.read(id);
            if (category == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения категории: " + e.getMessage()));
        }
    }

    // Поиск категорий по коду или имени
    @GetMapping("/search")
    public ResponseEntity<?> searchCategories(
            @RequestParam(required = false) @Size(min = 2, max = 50) String name,
            @RequestParam(required = false) @Size(min = 2, max = 20) String code) {

        try {
            if (name == null && code == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Укажите параметр поиска: name или code"));
            }

            List<Category> categories = categoryService.searchCategories(name, code);

            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("count", categories.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка поиска: " + e.getMessage()));
        }
    }

    // Получение категорий с количеством продуктов
    @GetMapping("/with-product-count")
    public ResponseEntity<?> getCategoriesWithProductCount() {
        try {
            List<Map<String, Object>> categoriesWithCount = categoryService.getCategoriesWithProductCount();
            return ResponseEntity.ok(Map.of("categories", categoriesWithCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения данных: " + e.getMessage()));
        }
    }

    // Проверка уникальности кода категории
    @GetMapping("/check-code/{code}")
    public ResponseEntity<?> checkCodeUniqueness(@PathVariable @Size(min = 2, max = 20) String code) {
        try {
            boolean exists = categoryService.existsByCode(code);
            Map<String, Object> response = new HashMap<>();
            response.put("code", code);
            response.put("exists", exists);
            response.put("available", !exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка проверки: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        try {
            // Проверяем уникальность кода
            if (categoryService.existsByCode(categoryRequest.getCode())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Категория с таким кодом уже существует"));
            }

            Category category = new Category(categoryRequest.getName(), categoryRequest.getCode());
            Category createdCategory = categoryService.save(category);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка создания категории: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryRequest categoryRequest) {

        try {
            Category existingCategory = categoryService.read(id);
            if (existingCategory == null) {
                return ResponseEntity.notFound().build();
            }

            // Проверяем уникальность кода (если код изменился)
            if (!existingCategory.getCode().equals(categoryRequest.getCode()) &&
                    categoryService.existsByCode(categoryRequest.getCode())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Категория с таким кодом уже существует"));
            }

            existingCategory.setName(categoryRequest.getName());
            existingCategory.setCode(categoryRequest.getCode());

            Category updatedCategory = categoryService.update(existingCategory);
            return ResponseEntity.ok(updatedCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка обновления категории: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            // Проверяем, есть ли продукты в этой категории
            if (categoryService.hasProducts(id)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Нельзя удалить категорию с продуктами"));
            }

            categoryService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Категория успешно удалена"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка удаления категории: " + e.getMessage()));
        }
    }

    // DTO для валидации категории
    public static class CategoryRequest {
        @NotBlank(message = "Название категории не может быть пустым")
        @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
        private String name;

        @NotBlank(message = "Код категории не может быть пустым")
        @Size(min = 2, max = 20, message = "Код должен быть от 2 до 20 символов")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Код может содержать только заглавные буквы, цифры и подчеркивания")
        private String code;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
}
