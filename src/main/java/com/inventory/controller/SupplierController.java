package com.inventory.controller;

import com.inventory.model.Supplier;
import com.inventory.service.SupplierService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) { this.supplierService = supplierService; }

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("USER") != null;
    }

    @GetMapping
    public ResponseEntity<?> all(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        List<Supplier> list = supplierService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam("q") String q, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(supplierService.search(q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return supplierService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Supplier s, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(supplierService.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Supplier s, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        s.setId(id);
        return ResponseEntity.ok(supplierService.save(s));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        supplierService.delete(id);
        return ResponseEntity.ok().build();
    }
}
