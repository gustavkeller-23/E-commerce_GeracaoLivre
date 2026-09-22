package com.geracaolivre.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrinhos")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true, nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinho> itens = new ArrayList<>();

    public Carrinho() {}

    public Carrinho(Usuario usuario) {
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<ItemCarrinho> getItens() { return itens; }
    public void setItens(List<ItemCarrinho> itens) { this.itens = itens; }

    public BigDecimal getValorTotal() {
        if (itens == null || itens.isEmpty()) return BigDecimal.ZERO;
        return itens.stream()
                .map(ItemCarrinho::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
