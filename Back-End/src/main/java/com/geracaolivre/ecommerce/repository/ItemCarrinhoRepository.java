package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Long> {
    Optional<ItemCarrinho> findByCarrinhoIdAndProdutoId(Long carrinhoId, Long produtoId);
}
