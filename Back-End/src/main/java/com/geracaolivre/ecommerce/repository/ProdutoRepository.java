package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);

    List<Produto> findByDestaqueTrueAndAtivoTrue();

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Produto> buscarPorTermo(@Param("termo") String termo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.categoria.id = :categoriaId AND " +
           "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Produto> buscarPorCategoriaETermo(@Param("categoriaId") Long categoriaId, @Param("termo") String termo);
}
