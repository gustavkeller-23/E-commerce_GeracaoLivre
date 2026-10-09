package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.service.ProdutoService;
import com.geracaolivre.ecommerce.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/produtos")
public class AdminProdutoController {
    private final ProdutoService service;
    private final ProdutoRepository repository;
    public AdminProdutoController(ProdutoService service, ProdutoRepository repository) { this.service = service; this.repository = repository; }
    @GetMapping
    public List<ProdutoDTO> listar() { return repository.findAll().stream().map(ProdutoDTO::new).toList(); }
    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarProduto(dto));
    }
    @PutMapping("/{id}")
    public ProdutoDTO editar(@PathVariable Long id, @Valid @RequestBody ProdutoDTO dto) { return service.atualizarProduto(id, dto); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) { service.excluirProduto(id); return ResponseEntity.noContent().build(); }
}
