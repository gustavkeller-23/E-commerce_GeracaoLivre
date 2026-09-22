package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
    List<Mensagem> findAllByOrderByDataEnvioDesc();
}
