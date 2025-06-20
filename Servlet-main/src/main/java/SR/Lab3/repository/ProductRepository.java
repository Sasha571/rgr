package SR.Lab3.repository;

import SR.Lab3.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SR.Lab3.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findByPrName(String name);
    boolean existsByPrName(String prName);
    List<Product> findBySuppliers(Supplier supplier);
    Page<Product> findByPrNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT c.name, COUNT(p) FROM Product p JOIN p.category c GROUP BY c.name")
    List<Object[]> countProductsByCategory();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.suppliers IS EMPTY")
    long countProductsWithoutSuppliers();
}
