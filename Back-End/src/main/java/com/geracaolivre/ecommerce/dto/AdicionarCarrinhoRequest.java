package com.geracaolivre.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AdicionarCarrinhoRequest {
    @NotNull(message = "O ID do produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade mínima é 1")
    private Integer quantidade = 1;

    public AdicionarCarrinhoRequest() {}

    public AdicionarCarrinhoRequest(Long produtoId, Integer quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
