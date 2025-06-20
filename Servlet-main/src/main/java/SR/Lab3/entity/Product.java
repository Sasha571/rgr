package SR.Lab3.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "products")
@AttributeOverride(name = "id", column = @Column(name = "pr_id"))
public class Product extends AbstractEntity {

    @Column(name = "pr_name", unique = true, nullable = false)
    @JsonProperty("pr_name")
    private String prName;

    @ManyToMany
    @JoinTable(
            name = "product_supplier",
            joinColumns = @JoinColumn(name = "pr_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    private Set<Supplier> suppliers = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonBackReference("category-products")
    private Category category;

    public Product() {}

    public Product(String prName) {
        this.prName = prName;
    }

    public String getPrName() {
        return prName;
    }

    public void setPrName(String prName) {
        this.prName = prName;
    }

    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Set<Supplier> suppliers) {
        this.suppliers = suppliers;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", prName='" + prName + '\'' +
                ", suppliersCount=" + (suppliers != null ? suppliers.size() : 0) +
                ", categoryId=" + (category != null && category.getId() != null ? category.getId() : "null") +
                '}';
    }
}
