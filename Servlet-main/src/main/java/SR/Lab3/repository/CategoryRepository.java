package SR.Lab3.repository;

import SR.Lab3.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);

    // Search categories by name or code
    @Query("SELECT c FROM Category c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:code IS NULL OR LOWER(c.code) LIKE LOWER(CONCAT('%', :code, '%')))")
    List<Category> searchCategories(@Param("name") String name, @Param("code") String code);

    // Используем JPQL для подсчета продуктов (предполагая связь One-to-Many)
    @Query("SELECT c.id, c.name, c.code, " +
            "(SELECT COUNT(p) FROM Product p WHERE p.category = c) " +
            "FROM Category c ORDER BY c.name")
    List<Object[]> findCategoriesWithProductCount();

    // Проверка наличия продуктов в категории через JPQL
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Long categoryId);
}
