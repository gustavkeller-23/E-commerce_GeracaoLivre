package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
    Optional<Carrinho> findByUsuarioId(Long usuarioId);
}
