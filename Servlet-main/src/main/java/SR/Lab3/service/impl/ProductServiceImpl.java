package SR.Lab3.service.impl;

import SR.Lab3.entity.Product;
import SR.Lab3.repository.ProductRepository;
import SR.Lab3.service.ProductService;
import SR.Lab3.controller.ProductController.ProductCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Override
    public Product read(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Product> read() {
        return repository.findAll();
    }

    @Override
    public Product save(Product entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void edit(Product entity) {
        repository.save(entity);
    }

    @Override
    public Page<Product> searchByName(String name, Pageable pageable) {
        return repository.findByPrNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Product> findByCategoryId(Long categoryId, Pageable pageable) {
        return repository.findByCategoryId(categoryId, pageable);
    }

    @Override
    public List<Product> createBatch(List<ProductCreateRequest> productRequests) {
        List<Product> products = new ArrayList<>();
        for (ProductCreateRequest request : productRequests) {
            Product product = new Product();
            product.setPrName(request.getPrName());
            products.add(product);
        }
        return repository.saveAll(products);
    }

    @Override
    public long getTotalCount() {
        return repository.count();
    }

    @Override
    public Map<String, Long> getProductCountByCategory() {
        List<Object[]> results = repository.countProductsByCategory();
        Map<String, Long> countMap = new HashMap<>();
        for (Object[] result : results) {
            countMap.put((String) result[0], (Long) result[1]);
        }
        return countMap;
    }

    @Override
    public long getProductsWithoutSuppliersCount() {
        return repository.countProductsWithoutSuppliers();
    }
}
