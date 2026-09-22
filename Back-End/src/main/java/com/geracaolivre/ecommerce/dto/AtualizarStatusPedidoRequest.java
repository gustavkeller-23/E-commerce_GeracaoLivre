package com.geracaolivre.ecommerce.dto;

import com.geracaolivre.ecommerce.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public class AtualizarStatusPedidoRequest {
    @NotNull(message = "O novo status é obrigatório")
    private StatusPedido status;

    public AtualizarStatusPedidoRequest() {}
    public AtualizarStatusPedidoRequest(StatusPedido status) { this.status = status; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
}
