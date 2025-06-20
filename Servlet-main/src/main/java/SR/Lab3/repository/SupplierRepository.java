package SR.Lab3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import SR.Lab3.entity.Product;
import SR.Lab3.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByProduct(Product product);

    List<Supplier> findBySurname(String surname);

   
    @Query("SELECT s FROM Supplier s WHERE " +
            "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:surname IS NULL OR LOWER(s.surname) LIKE LOWER(CONCAT('%', :surname, '%'))) AND " +
            "(:phone IS NULL OR s.phoneNumber LIKE CONCAT('%', :phone, '%'))")
    List<Supplier> searchSuppliers(@Param("name") String name,
                                   @Param("surname") String surname,
                                   @Param("phone") String phone);

    
    @Query(value = "SELECT s.id, s.name, s.surname, s.phone_number, " +
            "COALESCE(pc.product_count, 0) as product_count " +
            "FROM supplier s " +
            "LEFT JOIN (SELECT supplier_id, COUNT(*) as product_count " +
            "          FROM product_supplier GROUP BY supplier_id) pc " +
            "ON s.id = pc.supplier_id " +
            "ORDER BY s.surname, s.name", nativeQuery = true)
    List<Object[]> findSuppliersWithProductCount();

    @Query(value = "SELECT s.id, s.name, s.surname, s.phone_number, " +
            "COALESCE(pc.product_count, 0) as product_count " +
            "FROM supplier s " +
            "LEFT JOIN (SELECT supplier_id, COUNT(*) as product_count " +
            "          FROM product_supplier GROUP BY supplier_id) pc " +
            "ON s.id = pc.supplier_id " +
            "ORDER BY COALESCE(pc.product_count, 0) DESC", nativeQuery = true)
    List<Object[]> findTopSuppliersByProductCount();
}
