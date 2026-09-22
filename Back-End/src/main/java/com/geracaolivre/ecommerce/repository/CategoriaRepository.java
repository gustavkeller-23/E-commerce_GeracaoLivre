package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNomeIgnoreCase(String nome);
}
