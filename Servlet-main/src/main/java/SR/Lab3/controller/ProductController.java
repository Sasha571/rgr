package SR.Lab3.controller;

import SR.Lab3.entity.Product;
import SR.Lab3.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping(value = "api/product", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class ProductController extends AbstractController<Product> {

    @Autowired
    private ProductService service;

    @Override
    public ProductService getService() {
        return service;
    }

    // Поиск продуктов по имени с валидацией
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam @NotBlank @Size(min = 2, max = 50) String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = service.searchByName(name, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("products", products.getContent());
            response.put("totalElements", products.getTotalElements());
            response.put("totalPages", products.getTotalPages());
            response.put("currentPage", page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка поиска: " + e.getMessage()));
        }
    }

    // Получение продуктов по категории с пагинацией
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getProductsByCategory(
            @PathVariable @Min(1) Long categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = service.findByCategoryId(categoryId, pageable);

            if (products.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("products", products.getContent());
            response.put("categoryId", categoryId);
            response.put("totalElements", products.getTotalElements());
            response.put("currentPage", page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения продуктов: " + e.getMessage()));
        }
    }

    // Массовое создание продуктов
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/batch")
    public ResponseEntity<?> createProductsBatch(@RequestBody @Valid List<ProductCreateRequest> products) {
        try {
            if (products.size() > 50) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Максимум 50 продуктов за раз"));
            }

            List<Product> createdProducts = service.createBatch(products);

            Map<String, Object> response = new HashMap<>();
            response.put("created", createdProducts.size());
            response.put("products", createdProducts);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка создания продуктов: " + e.getMessage()));
        }
    }

    // Получение статистики по продуктам
    @GetMapping("/stats")
    public ResponseEntity<?> getProductStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProducts", service.getTotalCount());
            stats.put("productsByCategory", service.getProductCountByCategory());
            stats.put("productsWithoutSuppliers", service.getProductsWithoutSuppliersCount());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения статистики: " + e.getMessage()));
        }
    }

    // Убираем @Override и меняем сигнатуру метода, чтобы избежать конфликта валидации
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateProduct(@RequestBody @Valid Product entity) {
        try {
            service.save(entity);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка обновления: " + e.getMessage());
        }
    }

    // Убираем @Override и меняем сигнатуру метода
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createProduct(@RequestBody @Valid Product entity) {
        try {
            service.save(entity);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка создания: " + e.getMessage());
        }
    }

    // Альтернативно, создайте методы с DTO для избежания конфликтов
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/create-with-dto", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProductWithDto(@RequestBody @Valid ProductCreateRequest request) {
        try {
            Product product = convertToProduct(request);
            Product savedProduct = service.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка создания продукта: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(value = "/{id}/update-with-dto", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProductWithDto(
            @PathVariable Long id,
            @RequestBody @Valid ProductCreateRequest request) {
        try {
            Product existingProduct = service.read(id);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            existingProduct.setPrName(request.getPrName());
            // Установите другие поля по необходимости

            Product updatedProduct = service.save(existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка обновления продукта: " + e.getMessage()));
        }
    }

    private Product convertToProduct(ProductCreateRequest request) {
        Product product = new Product();
        product.setPrName(request.getPrName());
        // Установите categoryId и другие поля по необходимости
        return product;
    }

    // DTO для создания продуктов
    public static class ProductCreateRequest {
        @NotBlank(message = "Название продукта не может быть пустым")
        @Size(min = 2, max = 100, message = "Название должно быть от 2 до 100 символов")
        private String prName;

        @Min(value = 1, message = "ID категории должен быть больше 0")
        private Long categoryId;

        // Getters and setters
        public String getPrName() { return prName; }
        public void setPrName(String prName) { this.prName = prName; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    }
}
