package com.inventory.service;

import com.inventory.model.InventoryLog;
import com.inventory.model.Product;
import com.inventory.repository.InventoryLogRepository;
import com.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {
    private final InventoryLogRepository logRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryLogRepository logRepository, ProductRepository productRepository) {
        this.logRepository = logRepository;
        this.productRepository = productRepository;
    }

    public List<InventoryLog> findAllLogs() { return logRepository.findAll(); }

    public InventoryLog updateStock(Long productId, int quantity, String type, String notes) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        if ("IN".equalsIgnoreCase(type)) {
            product.setQuantity(product.getQuantity() + quantity);
        } else if ("OUT".equalsIgnoreCase(type)) {
            if (product.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock");
            }
            product.setQuantity(product.getQuantity() - quantity);
        } else {
            throw new RuntimeException("Invalid type, must be IN or OUT");
        }
        productRepository.save(product);

        InventoryLog log = new InventoryLog();
        log.setProduct(product);
        log.setType(type.toUpperCase());
        log.setQuantity(quantity);
        log.setNotes(notes);
        log.setCreatedAt(LocalDateTime.now());
        return logRepository.save(log);
    }

    public Map<String, Object> reports() {
        List<Product> products = productRepository.findAll();
        Map<String, Object> result = new HashMap<>();

        double stockValuation = products.stream().mapToDouble(p -> p.getPrice() * p.getQuantity()).sum();
        result.put("stockValuation", stockValuation);

        // Simple heuristic for fast/slow moving: below/above reorder level
        result.put("fastMoving", products.stream().filter(p -> p.getQuantity() > p.getReorderLevel() * 2).limit(10).toList());
        result.put("slowMoving", products.stream().filter(p -> p.getQuantity() <= p.getReorderLevel()).limit(10).toList());

        // Placeholder turnover rate
        result.put("turnoverRate", 3.2);
        return result;
    }
}
