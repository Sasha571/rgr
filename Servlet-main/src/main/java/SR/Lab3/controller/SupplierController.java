package SR.Lab3.controller;

import SR.Lab3.entity.Supplier;
import SR.Lab3.service.SupplierService;
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
@RequestMapping("api/supplier")
@Validated
public class SupplierController extends AbstractController<Supplier> {

    @Autowired
    private SupplierService service;

    @Override
    public SupplierService getService() {
        return service;
    }

    // Поиск поставщиков с валидацией
    @GetMapping("/search")
    public ResponseEntity<?> searchSuppliers(
            @RequestParam(required = false) @Size(min = 2, max = 50) String name,
            @RequestParam(required = false) @Size(min = 2, max = 50) String surname,
            @RequestParam(required = false) @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$") String phone) {

        try {
            if (name == null && surname == null && phone == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Укажите хотя бы один параметр поиска"));
            }

            List<Supplier> suppliers = service.searchSuppliers(name, surname, phone);

            Map<String, Object> response = new HashMap<>();
            response.put("suppliers", suppliers);
            response.put("count", suppliers.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка поиска: " + e.getMessage()));
        }
    }

    // Получение поставщиков с количеством продуктов
    @GetMapping("/with-product-count")
    public ResponseEntity<?> getSuppliersWithProductCount() {
        try {
            List<Map<String, Object>> suppliersWithCount = service.getSuppliersWithProductCount();
            return ResponseEntity.ok(Map.of("suppliers", suppliersWithCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения данных: " + e.getMessage()));
        }
    }

    // Назначение поставщика продукту
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping("/{supplierId}/assign-product/{productId}")
    public ResponseEntity<?> assignProductToSupplier(
            @PathVariable Long supplierId,
            @PathVariable Long productId) {

        try {
            boolean success = service.assignProductToSupplier(supplierId, productId);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "Продукт успешно назначен поставщику"));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Не удалось назначить продукт"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка назначения: " + e.getMessage()));
        }
    }

    // Получение топ поставщиков по количеству продуктов
    @GetMapping("/top")
    public ResponseEntity<?> getTopSuppliers(
            @RequestParam(defaultValue = "5") int limit) {

        try {
            if (limit > 20) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Максимальный лимит: 20"));
            }

            List<Map<String, Object>> topSuppliers = service.getTopSuppliers(limit);
            return ResponseEntity.ok(Map.of("topSuppliers", topSuppliers));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ошибка получения топа: " + e.getMessage()));
        }
    }

    // Методы с валидацией (убираем @Override)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateSupplier(@RequestBody @Valid SupplierRequest supplierRequest) {
        try {
            Supplier supplier = convertToSupplier(supplierRequest);
            service.save(supplier);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка обновления: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSupplier(@RequestBody @Valid SupplierRequest supplierRequest) {
        try {
            Supplier supplier = convertToSupplier(supplierRequest);
            service.save(supplier);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка создания: " + e.getMessage());
        }
    }

    private Supplier convertToSupplier(SupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setSurname(request.getSurname());
        supplier.setPhoneNumber(request.getPhoneNumber());
        return supplier;
    }

    // DTO для валидации поставщика
    public static class SupplierRequest {
        @NotBlank(message = "Имя не может быть пустым")
        @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
        private String name;

        @NotBlank(message = "Фамилия не может быть пустой")
        @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
        private String surname;

        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Неверный формат телефона")
        private String phoneNumber;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}

