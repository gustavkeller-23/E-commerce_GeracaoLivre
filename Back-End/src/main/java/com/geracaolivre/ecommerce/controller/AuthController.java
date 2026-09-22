package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.AuthRequest;
import com.geracaolivre.ecommerce.dto.AuthResponse;
import com.geracaolivre.ecommerce.dto.RegisterRequest;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe() {
        try {
            Usuario usuario = authService.getAuthenticatedUser();
            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "nome", usuario.getNome(),
                    "email", usuario.getEmail(),
                    "telefone", usuario.getTelefone() != null ? usuario.getTelefone() : "",
                    "role", usuario.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }
}
