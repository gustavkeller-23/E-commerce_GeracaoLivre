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

    public ProdutoService(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com id: " + id));
    }

    public ProdutoDTO buscarPorId(Long id) {
        return new ProdutoDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public ProdutoDTO criarProduto(ProdutoDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada com id: " + dto.getCategoriaId()));

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

        Produto salvo = produtoRepository.save(produto);
        return new ProdutoDTO(salvo);
    }

    @Transactional
    public ProdutoDTO atualizarProduto(Long id, ProdutoDTO dto) {
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

        Produto atualizado = produtoRepository.save(produto);
        return new ProdutoDTO(atualizado);
    }

    @Transactional
    public void excluirProduto(Long id) {
        Produto produto = buscarEntidadePorId(id);
        // Exclusão lógica para preservar histórico de pedidos
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }
}
