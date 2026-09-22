package com.geracaolivre.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AtualizarQuantidadeRequest {
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima deve ser 1")
    private Integer quantidade;

    public AtualizarQuantidadeRequest() {}
    public AtualizarQuantidadeRequest(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
