package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.service.AuthService;
import com.geracaolivre.ecommerce.service.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;
    private final AuthService authService;

    public FavoritoController(FavoritoService favoritoService, AuthService authService) {
        this.favoritoService = favoritoService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarFavoritos() {
        Usuario usuario = authService.getAuthenticatedUser();
        return ResponseEntity.ok(favoritoService.listarFavoritos(usuario));
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> listarIdsFavoritos() {
        Usuario usuario = authService.getAuthenticatedUser();
        return ResponseEntity.ok(favoritoService.listarIdsFavoritos(usuario));
    }

    @PostMapping("/toggle/{produtoId}")
    public ResponseEntity<?> toggleFavorito(@PathVariable Long produtoId) {
        Usuario usuario = authService.getAuthenticatedUser();
        boolean adicionado = favoritoService.toggleFavorito(usuario, produtoId);
        return ResponseEntity.ok(Map.of(
                "favoritado", adicionado,
                "message", adicionado ? "Produto adicionado aos favoritos" : "Produto removido dos favoritos"
        ));
    }
}
