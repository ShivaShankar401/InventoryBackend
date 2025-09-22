package com.inventory.controller;

import com.inventory.model.InventoryLog;
import com.inventory.service.InventoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("USER") != null;
    }

    @GetMapping("/logs")
    public ResponseEntity<?> logs(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        List<InventoryLog> logs = inventoryService.findAllLogs();
        return ResponseEntity.ok(logs);
    }

    @PostMapping("/update-stock")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Object> payload, HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        Long productId = Long.valueOf(String.valueOf(payload.get("productId")));
        int quantity = Integer.parseInt(String.valueOf(payload.get("quantity")));
        String type = String.valueOf(payload.get("type"));
        String notes = payload.get("notes") != null ? String.valueOf(payload.get("notes")) : null;
        InventoryLog log = inventoryService.updateStock(productId, quantity, type, notes);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        // Placeholder simple stats can reuse reports for now
        Map<String, Object> rpt = inventoryService.reports();
        return ResponseEntity.ok(rpt);
    }

    @GetMapping("/reports")
    public ResponseEntity<?> reports(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(inventoryService.reports());
    }

    @GetMapping("/reports/export/csv")
    public ResponseEntity<?> exportCsv(HttpSession session) {
        if (!isAuthenticated(session)) return ResponseEntity.status(401).build();
        List<InventoryLog> logs = inventoryService.findAllLogs();
        StringBuilder sb = new StringBuilder();
        sb.append("id,product,type,quantity,notes,createdAt\n");
        logs.forEach(l -> sb.append(l.getId()).append(',')
                .append(l.getProduct() != null ? l.getProduct().getName() : "").append(',')
                .append(l.getType()).append(',')
                .append(l.getQuantity()).append(',')
                .append(l.getNotes() != null ? l.getNotes().replace(",", " ") : "").append(',')
                .append(l.getCreatedAt()).append('\n'));

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory_logs.csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(bytes);
    }
}
