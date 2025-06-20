package SR.Lab3.service.impl;

import SR.Lab3.entity.Category;
import SR.Lab3.repository.CategoryRepository;
import SR.Lab3.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category read(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<Category> read() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> readAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category save(Category entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void edit(Category entity) {
        categoryRepository.save(entity);
    }

    @Override
    public Category readByName(String name) {
        return categoryRepository.findByName(name).orElse(null);
    }

    @Override
    public Category update(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> searchCategories(String name, String code) {
        return categoryRepository.searchCategories(name, code);
    }

    @Override
    public List<Map<String, Object>> getCategoriesWithProductCount() {
        List<Object[]> results = categoryRepository.findCategoriesWithProductCount();
        List<Map<String, Object>> categoriesWithCount = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("id", result[0]);
            categoryData.put("name", result[1]);
            categoryData.put("code", result[2]);
            categoryData.put("productCount", result[3]);
            categoriesWithCount.add(categoryData);
        }

        return categoriesWithCount;
    }

    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }

    @Override
    public boolean hasProducts(Long categoryId) {
        return categoryRepository.hasProducts(categoryId);
    }
}
