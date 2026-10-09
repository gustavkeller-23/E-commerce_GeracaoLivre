package com.geracaolivre.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AtualizarQuantidadeRequest {
    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.01", message = "Informe uma metragem maior que zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal quantidade;

    public AtualizarQuantidadeRequest() {}
    public AtualizarQuantidadeRequest(BigDecimal quantidade) { this.quantidade = quantidade; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
}
