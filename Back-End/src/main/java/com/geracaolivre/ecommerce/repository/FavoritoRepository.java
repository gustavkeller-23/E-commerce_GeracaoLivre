package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuarioId(Long usuarioId);
    Optional<Favorito> findByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
    void deleteByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
}
