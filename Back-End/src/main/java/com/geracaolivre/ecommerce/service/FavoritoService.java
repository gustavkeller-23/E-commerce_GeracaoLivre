package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.dto.ProdutoDTO;
import com.geracaolivre.ecommerce.model.Favorito;
import com.geracaolivre.ecommerce.model.Produto;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.repository.FavoritoRepository;
import com.geracaolivre.ecommerce.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final ProdutoRepository produtoRepository;

    public FavoritoService(FavoritoRepository favoritoRepository, ProdutoRepository produtoRepository) {
        this.favoritoRepository = favoritoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoDTO> listarFavoritos(Usuario usuario) {
        return favoritoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(Favorito::getProduto)
                .map(ProdutoDTO::new)
                .collect(Collectors.toList());
    }

    public List<Long> listarIdsFavoritos(Usuario usuario) {
        return favoritoRepository.findByUsuarioId(usuario.getId()).stream()
                .map(f -> f.getProduto().getId())
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean toggleFavorito(Usuario usuario, Long produtoId) {
        Optional<Favorito> existente = favoritoRepository.findByUsuarioIdAndProdutoId(usuario.getId(), produtoId);

        if (existente.isPresent()) {
            favoritoRepository.delete(existente.get());
            return false; // removido
        } else {
            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
            Favorito novo = new Favorito(usuario, produto);
            favoritoRepository.save(novo);
            return true; // adicionado
        }
    }
}
