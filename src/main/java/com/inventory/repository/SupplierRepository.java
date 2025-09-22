package com.inventory.repository;

import com.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    @Query("SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.email) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.address) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Supplier> search(String q);
}
