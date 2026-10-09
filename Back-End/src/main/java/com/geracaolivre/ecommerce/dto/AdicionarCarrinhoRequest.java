package com.geracaolivre.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AdicionarCarrinhoRequest {
    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "A quantidade é obrigatória")
    @DecimalMin(value = "0.01", message = "Informe uma metragem maior que zero")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal quantidade = BigDecimal.ONE;

    public AdicionarCarrinhoRequest() {}

    public AdicionarCarrinhoRequest(Long produtoId, BigDecimal quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public BigDecimal getQuantidade() { return quantidade; }
    public void setQuantidade(BigDecimal quantidade) { this.quantidade = quantidade; }
}
