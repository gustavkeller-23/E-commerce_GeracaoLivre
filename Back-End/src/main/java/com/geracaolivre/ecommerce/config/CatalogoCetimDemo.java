package com.geracaolivre.ecommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.repository.CategoriaRepository;
import com.geracaolivre.ecommerce.repository.ProdutoRepository;
import com.geracaolivre.ecommerce.service.ProdutoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component @Profile("dev") @Order(10)
public class CatalogoCetimDemo implements CommandLineRunner {
    private final ObjectMapper mapper;
    private final ProdutoRepository produtos;
    private final CategoriaRepository categorias;
    private final ProdutoService service;
    public CatalogoCetimDemo(ObjectMapper mapper, ProdutoRepository produtos, CategoriaRepository categorias, ProdutoService service) {
        this.mapper = mapper; this.produtos = produtos; this.categorias = categorias; this.service = service;
    }
    @Override @Transactional
    public void run(String... args) throws Exception {
        // Cadastro idempotente: não sobrescrever preços/estoques editados pela loja.
        try (var input = new ClassPathResource("catalogo/fitas-cetim.json").getInputStream()) {
            for (var node : mapper.readTree(input)) {
                if (produtos.existsBySku(node.get("sku").asText())) continue;
                var dto = mapper.treeToValue(node, ProdutoDTO.class);
                dto.setCategoriaId(categorias.findByNomeIgnoreCase(dto.getCategoriaNome()).orElseThrow().getId());
                service.criarProduto(dto);
            }
        }
    }
}
