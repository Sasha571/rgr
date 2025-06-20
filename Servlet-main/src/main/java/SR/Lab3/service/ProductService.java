package SR.Lab3.service;

import SR.Lab3.entity.Product;
import SR.Lab3.controller.ProductController.ProductCreateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface ProductService extends Service<Product> {
    Page<Product> searchByName(String name, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    List<Product> createBatch(List<ProductCreateRequest> products);
    long getTotalCount();
    Map<String, Long> getProductCountByCategory();
    long getProductsWithoutSuppliersCount();
}
