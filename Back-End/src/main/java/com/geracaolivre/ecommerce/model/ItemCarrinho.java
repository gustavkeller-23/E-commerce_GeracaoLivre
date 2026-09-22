package com.geracaolivre.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_carrinho")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrinho_id", nullable = false)
    @JsonIgnore
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade = 1;

    public ItemCarrinho() {}

    public ItemCarrinho(Carrinho carrinho, Produto produto, Integer quantidade) {
        this.carrinho = carrinho;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Carrinho getCarrinho() { return carrinho; }
    public void setCarrinho(Carrinho carrinho) { this.carrinho = carrinho; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getSubtotal() {
        if (produto == null) return BigDecimal.ZERO;
        BigDecimal precoEfetivo = produto.getPrecoPromocional() != null ? produto.getPrecoPromocional() : produto.getPreco();
        return precoEfetivo.multiply(BigDecimal.valueOf(quantidade));
    }
}
