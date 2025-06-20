package SR.Lab3.service;

import SR.Lab3.entity.Category;
import java.util.List;
import java.util.Map;

public interface CategoryService extends Service<Category> {
    List<Category> readAll();
    Category readByName(String name);
    Category update(Category category);

    // Add missing methods
    List<Category> searchCategories(String name, String code);
    List<Map<String, Object>> getCategoriesWithProductCount();
    boolean existsByCode(String code);
    boolean hasProducts(Long categoryId);
}
