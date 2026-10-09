package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.service.ProdutoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoDTO>> listarTodos(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Long categoriaId) {
        List<ProdutoDTO> produtos = produtoService.listarTodos(busca, categoriaId);
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscarPorId(@PathVariable Long id) {
        ProdutoDTO produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<ProdutoDTO>> listarDestaques() {
        List<ProdutoDTO> destaques = produtoService.listarDestaques();
        return ResponseEntity.ok(destaques);
    }

    @GetMapping("/paginado")
    public java.util.Map<String, Object> pagina(@RequestParam(required = false) String busca,
            @RequestParam(required = false) Long categoriaId, @RequestParam(required = false) Integer largura,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "12") int tamanho) {
        var resultado = produtoService.pagina(busca, categoriaId, largura, pagina, tamanho);
        return java.util.Map.of("itens", resultado.getContent(), "pagina", resultado.getNumber(), "totalPaginas", resultado.getTotalPages(), "total", resultado.getTotalElements());
    }
    @org.springframework.beans.factory.annotation.Autowired
    private com.geracaolivre.ecommerce.service.CatalogoEventos eventos;
    @GetMapping(value = "/eventos", produces = "text/event-stream")
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter eventos() { return eventos.conectar(); }
}
