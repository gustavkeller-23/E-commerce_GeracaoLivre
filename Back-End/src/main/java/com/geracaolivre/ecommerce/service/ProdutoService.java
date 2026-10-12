package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.model.Categoria;
import com.geracaolivre.ecommerce.model.Produto;
import com.geracaolivre.ecommerce.repository.CategoriaRepository;
import com.geracaolivre.ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final CatalogoEventos eventos;

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository, CatalogoEventos eventos) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.eventos = eventos;
    }

    public List<ProdutoDTO> listarTodos(String termo, Long categoriaId) {
        List<Produto> produtos;

        if (termo != null && !termo.trim().isEmpty() && categoriaId != null) {
            produtos = produtoRepository.buscarPorCategoriaETermo(categoriaId, termo.trim());
        } else if (termo != null && !termo.trim().isEmpty()) {
            produtos = produtoRepository.buscarPorTermo(termo.trim());
        } else if (categoriaId != null) {
            produtos = produtoRepository.findByCategoriaIdAndAtivoTrue(categoriaId);
        } else {
            produtos = produtoRepository.findByAtivoTrue();
        }

        return produtos.stream().map(ProdutoDTO::new).collect(Collectors.toList());
    }

    public List<ProdutoDTO> listarDestaques() {
        return produtoRepository.findByDestaqueTrueAndAtivoTrue().stream()
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    public Produto buscarEntidadePorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Fita não encontrada."));
    }

    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = buscarEntidadePorId(id);
        if (!produto.isCatalogavel()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Fita não encontrada.");
        return new ProdutoDTO(produto);
    }

    @Transactional
    public ProdutoDTO criarProduto(ProdutoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com id: " + dto.getCategoriaId()));

        validarDados(dto);
        Produto produto = new Produto(
                dto.getNome(),
                dto.getDescricao(),
                dto.getPreco(),
                dto.getPrecoPromocional(),
                dto.getEstoque(),
                dto.getImagemUrl(),
                categoria,
                dto.getDestaque() != null ? dto.getDestaque() : false
        );

        if (dto.getAtivo() != null) {
            produto.setAtivo(dto.getAtivo());
        }

        aplicarCaracteristicas(produto, dto);
        Produto salvo = produtoRepository.save(produto);
        eventos.aposCommit();
        return new ProdutoDTO(salvo);
    }

    @Transactional
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO dto) {
        validarDados(dto);
        Produto produto = buscarEntidadePorId(id);

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com id: " + dto.getCategoriaId()));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setPrecoPromocional(dto.getPrecoPromocional());
        produto.setEstoque(dto.getEstoque());
        produto.setImagemUrl(dto.getImagemUrl());
        produto.setCategoria(categoria);
        if (dto.getDestaque() != null) produto.setDestaque(dto.getDestaque());
        if (dto.getAtivo() != null) produto.setAtivo(dto.getAtivo());

        aplicarCaracteristicas(produto, dto);
        Produto atualizado = produtoRepository.save(produto);
        eventos.aposCommit();
        return new ProdutoDTO(atualizado);
    }

    @Transactional
    public void excluirProduto(Long id) {
        Produto produto = buscarEntidadePorId(id);
        // Exclusão lógica para preservar histórico de pedidos
        produto.setAtivo(false);
        produtoRepository.save(produto);
        eventos.aposCommit();
    }

    private void validarDados(ProdutoDTO dto) {
        if (!"GORGURAO".equals(dto.getMaterial()) && !"CETIM".equals(dto.getMaterial())) throw new IllegalArgumentException("Informe gorgurão ou cetim como material da fita.");
        if (dto.getPrecoPromocional() != null && dto.getPrecoPromocional().compareTo(dto.getPreco()) >= 0)
            throw new IllegalArgumentException("Preço promocional deve ser menor que o preço regular.");
    }
    private void aplicarCaracteristicas(Produto produto, ProdutoDTO dto) {
        produto.setMaterial(dto.getMaterial());
        produto.setSku(dto.getSku().trim());
        produto.setLarguraMm(dto.getLarguraMm());
        produto.setCor(dto.getCor().trim());
        produto.setEstampa(dto.getEstampa());
        produto.setQuantidadeMinima(dto.getQuantidadeMinima());
        produto.setIncremento(dto.getIncremento());
    }

    public org.springframework.data.domain.Page<ProdutoDTO> pagina(String busca, Long categoriaId, Integer largura, int pagina, int tamanho) {
        if (pagina < 0 || tamanho < 1 || tamanho > 48) throw new IllegalArgumentException("Paginação inválida.");
        org.springframework.data.jpa.domain.Specification<Produto> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            predicates.add(cb.isTrue(root.get("ativo")));
            predicates.add(root.get("material").in("GORGURAO", "CETIM"));
            if (categoriaId != null) predicates.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            if (largura != null) predicates.add(cb.equal(root.get("larguraMm"), largura));
            if (busca != null && !busca.isBlank()) {
                String termo = "%" + busca.trim().toLowerCase(java.util.Locale.ROOT) + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("nome")), termo), cb.like(cb.lower(root.get("cor")), termo), cb.like(cb.lower(root.get("descricao")), termo)));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return produtoRepository.findAll(spec, org.springframework.data.domain.PageRequest.of(pagina, tamanho, org.springframework.data.domain.Sort.by("id"))).map(ProdutoDTO::new);
    }
}
