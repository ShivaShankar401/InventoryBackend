package com.inventory.controller;

import com.inventory.model.Product;
import com.inventory.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) { this.productService = productService; }

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("USER") != null;
    }

    @GetMapping
    public ResponseEntity<?> all(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        List<Product> list = productService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<?> lowStock(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(productService.lowStock());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("q") String q, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(productService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product p, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        Product saved = productService.save(p);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product p, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        p.setId(id);
        Product saved = productService.save(p);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
