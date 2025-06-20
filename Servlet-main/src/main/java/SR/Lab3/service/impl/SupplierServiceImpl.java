package SR.Lab3.service.impl;

import SR.Lab3.entity.Product;
import SR.Lab3.entity.Supplier;
import SR.Lab3.repository.ProductRepository;
import SR.Lab3.repository.SupplierRepository;
import SR.Lab3.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.NoSuchElementException;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    @Autowired
    private SupplierRepository repository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Supplier read(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Supplier not found with id: " + id));
    }

    @Override
    public List<Supplier> read() {
        return repository.findAll();
    }

    @Override
    public Supplier save(Supplier supplier) {
        return repository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Supplier> readByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));
        return repository.findByProduct(product);
    }

    @Override
    public List<Supplier> readBySurname(String surname) {
        return repository.findBySurname(surname);
    }

    @Override
    public Supplier update(Supplier supplier) {
        if (supplier.getId() == null) {
            throw new IllegalArgumentException("Supplier id cannot be null for update operation");
        }

        Supplier existingSupplier = repository.findById(supplier.getId())
                .orElseThrow(() -> new NoSuchElementException("Supplier not found with id: " + supplier.getId()));

        existingSupplier.setName(supplier.getName());
        existingSupplier.setSurname(supplier.getSurname());
        existingSupplier.setPhoneNumber(supplier.getPhoneNumber());

        if (supplier.getProducts() != null && !supplier.getProducts().isEmpty()) {
            Set<Product> products = new HashSet<>();
            for (Product p : supplier.getProducts()) {
                Product product = productRepository.findById(p.getId())
                        .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + p.getId()));
                products.add(product);
            }
            existingSupplier.setProducts(products);
        } else {
            existingSupplier.setProducts(new HashSet<>());
        }

        return repository.save(existingSupplier);
    }

    @Override
    public void edit(Supplier entity) {
        update(entity);
    }

    @Override
    public List<Supplier> searchSuppliers(String name, String surname, String phone) {
        return repository.searchSuppliers(name, surname, phone);
    }

    @Override
    public List<Map<String, Object>> getSuppliersWithProductCount() {
        List<Object[]> results = repository.findSuppliersWithProductCount();
        List<Map<String, Object>> suppliersWithCount = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> supplierData = new HashMap<>();
            supplierData.put("id", result[0]);
            supplierData.put("name", result[1]);
            supplierData.put("surname", result[2]);
            supplierData.put("phoneNumber", result[3]);
            supplierData.put("productCount", result[4]);
            suppliersWithCount.add(supplierData);
        }

        return suppliersWithCount;
    }

    @Override
    @Transactional
    public boolean assignProductToSupplier(Long supplierId, Long productId) {
        try {
            Supplier supplier = repository.findById(supplierId)
                    .orElseThrow(() -> new NoSuchElementException("Supplier not found with id: " + supplierId));

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NoSuchElementException("Product not found with id: " + productId));

            if (supplier.getProducts() == null) {
                supplier.setProducts(new HashSet<>());
            }

            supplier.getProducts().add(product);
            repository.save(supplier);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getTopSuppliers(int limit) {
        List<Object[]> results = repository.findTopSuppliersByProductCount();
        List<Map<String, Object>> topSuppliers = new ArrayList<>();

        int count = 0;
        for (Object[] result : results) {
            if (count >= limit) break;

            Map<String, Object> supplierData = new HashMap<>();
            supplierData.put("id", result[0]);
            supplierData.put("name", result[1]);
            supplierData.put("surname", result[2]);
            supplierData.put("phoneNumber", result[3]);
            supplierData.put("productCount", result[4]);
            topSuppliers.add(supplierData);

            count++;
        }

        return topSuppliers;
    }
}
