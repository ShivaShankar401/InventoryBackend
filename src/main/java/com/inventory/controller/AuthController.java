package com.inventory.controller;

import com.inventory.model.Role;
import com.inventory.model.User;
import com.inventory.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String password = payload.get("password");
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        }
        User user = userOpt.get();
        session.setAttribute("USER", user);
        return ResponseEntity.ok(toDto(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, String> res = new HashMap<>();
        res.put("message", "Logged out");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        User user = (User) session.getAttribute("USER");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(toDto(user));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String email = payload.get("email");
        String password = payload.get("password");
        String roleStr = payload.getOrDefault("role", "STAFF");
        Role role = Role.valueOf(roleStr.toUpperCase());
        User existing = userService.findByEmail(email).orElse(null);
        if (existing != null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Email already registered");
            return ResponseEntity.badRequest().body(error);
        }
        User user = userService.register(name, email, password, role);
        return ResponseEntity.ok(toDto(user));
    }

    private Map<String, Object> toDto(User u) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", u.getId());
        m.put("name", u.getName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole().name());
        return m;
    }
}
