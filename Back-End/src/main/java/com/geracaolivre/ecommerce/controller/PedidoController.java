package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.CheckoutRequest;
import com.geracaolivre.ecommerce.model.Pedido;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.service.AuthService;
import com.geracaolivre.ecommerce.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final AuthService authService;

    public PedidoController(PedidoService pedidoService, AuthService authService) {
        this.pedidoService = pedidoService;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Pedido> checkout(@Valid @RequestBody CheckoutRequest request) {
        Usuario usuario = authService.getAuthenticatedUser();
        Pedido pedido = pedidoService.criarPedido(usuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

    @GetMapping("/meus-pedidos")
    public ResponseEntity<List<Pedido>> listarMeusPedidos() {
        Usuario usuario = authService.getAuthenticatedUser();
        return ResponseEntity.ok(pedidoService.listarPedidosDoUsuario(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        Usuario usuario = authService.getAuthenticatedUser();
        return ResponseEntity.ok(pedidoService.buscarPorIdEUsuario(id, usuario));
    }
}
