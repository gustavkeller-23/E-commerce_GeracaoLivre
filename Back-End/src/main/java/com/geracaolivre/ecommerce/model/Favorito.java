package com.geracaolivre.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "produto_id"})
})
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    private LocalDateTime dataAdicionado = LocalDateTime.now();

    public Favorito() {}

    public Favorito(Usuario usuario, Produto produto) {
        this.usuario = usuario;
        this.produto = produto;
        this.dataAdicionado = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public LocalDateTime getDataAdicionado() { return dataAdicionado; }
    public void setDataAdicionado(LocalDateTime dataAdicionado) { this.dataAdicionado = dataAdicionado; }
}
