package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.category) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Product> search(String q);

    @Query("SELECT p FROM Product p WHERE p.quantity < p.reorderLevel")
    List<Product> findLowStock();
}
