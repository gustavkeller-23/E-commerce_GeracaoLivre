package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.AdicionarCarrinhoRequest;
import com.geracaolivre.ecommerce.dto.AtualizarQuantidadeRequest;
import com.geracaolivre.ecommerce.model.Carrinho;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.service.AuthService;
import com.geracaolivre.ecommerce.service.CarrinhoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final AuthService authService;

    public CarrinhoController(CarrinhoService carrinhoService, AuthService authService) {
        this.carrinhoService = carrinhoService;
        this.authService = authService;
    }

    private Map<String, Object> formatarCarrinhoResponse(Carrinho carrinho) {
        Map<String, Object> res = new HashMap<>();
        res.put("id", carrinho.getId());
        res.put("total", carrinho.getValorTotal());
        res.put("itens", carrinho.getItens().stream().map(item -> {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("produtoId", item.getProduto().getId());
            itemMap.put("nome", item.getProduto().getNome());
            itemMap.put("imagemUrl", item.getProduto().getImagemUrl());
            itemMap.put("precoUnitario", item.getProduto().getPrecoPromocional() != null ? item.getProduto().getPrecoPromocional() : item.getProduto().getPreco());
            itemMap.put("quantidade", item.getQuantidade());
            itemMap.put("subtotal", item.getSubtotal());
            itemMap.put("estoqueDisponivel", item.getProduto().getEstoque());
            return itemMap;
        }).collect(Collectors.toList()));
        return res;
    }

    @GetMapping
    public ResponseEntity<?> obterCarrinho() {
        Usuario usuario = authService.getAuthenticatedUser();
        Carrinho carrinho = carrinhoService.obterOuCriarCarrinho(usuario);
        return ResponseEntity.ok(formatarCarrinhoResponse(carrinho));
    }

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarItem(@Valid @RequestBody AdicionarCarrinhoRequest request) {
        Usuario usuario = authService.getAuthenticatedUser();
        Carrinho carrinho = carrinhoService.adicionarItem(usuario, request.getProdutoId(), request.getQuantidade());
        return ResponseEntity.ok(formatarCarrinhoResponse(carrinho));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<?> atualizarQuantidade(
            @PathVariable Long itemId,
            @Valid @RequestBody AtualizarQuantidadeRequest request) {
        Usuario usuario = authService.getAuthenticatedUser();
        Carrinho carrinho = carrinhoService.atualizarQuantidade(usuario, itemId, request.getQuantidade());
        return ResponseEntity.ok(formatarCarrinhoResponse(carrinho));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> removerItem(@PathVariable Long itemId) {
        Usuario usuario = authService.getAuthenticatedUser();
        Carrinho carrinho = carrinhoService.removerItem(usuario, itemId);
        return ResponseEntity.ok(formatarCarrinhoResponse(carrinho));
    }

    @DeleteMapping("/limpar")
    public ResponseEntity<?> limparCarrinho() {
        Usuario usuario = authService.getAuthenticatedUser();
        carrinhoService.limparCarrinho(usuario);
        return ResponseEntity.ok(Map.of("message", "Carrinho limpo com sucesso"));
    }
}
