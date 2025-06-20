package SR.Lab3.service;

import SR.Lab3.entity.Supplier;
import java.util.List;
import java.util.Map;

public interface SupplierService extends Service<Supplier> {
    List<Supplier> readByProduct(Long productId);
    List<Supplier> readBySurname(String surname);
    Supplier update(Supplier supplier);


    List<Supplier> searchSuppliers(String name, String surname, String phone);
    List<Map<String, Object>> getSuppliersWithProductCount();
    boolean assignProductToSupplier(Long supplierId, Long productId);
    List<Map<String, Object>> getTopSuppliers(int limit);
}
