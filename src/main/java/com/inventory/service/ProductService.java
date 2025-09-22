package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.model.Supplier;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Product> findAll() { return productRepository.findAll(); }

    public Optional<Product> findById(Long id) { return productRepository.findById(id); }

    public List<Product> search(String q) { return productRepository.search(q); }

    public List<Product> lowStock() { return productRepository.findLowStock(); }

    public Product save(Product p) {
        if (p.getSupplier() != null && p.getSupplier().getId() != null) {
            Supplier s = supplierRepository.findById(p.getSupplier().getId()).orElse(null);
            p.setSupplier(s);
        }
        return productRepository.save(p);
    }

    public void delete(Long id) { productRepository.deleteById(id); }
}
