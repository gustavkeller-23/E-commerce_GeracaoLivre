package com.geracaolivre.ecommerce.repository;

import com.geracaolivre.ecommerce.model.Pedido;
import com.geracaolivre.ecommerce.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByDataPedidoDesc(Long usuarioId);
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findByStatusOrderByDataPedidoDesc(StatusPedido status);
    List<Pedido> findAllByOrderByDataPedidoDesc();
}
