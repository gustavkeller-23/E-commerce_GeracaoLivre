package com.geracaolivre.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Column(length = 2000)
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(precision = 10, scale = 2)
    private BigDecimal precoPromocional;

    @NotNull(message = "O estoque é obrigatório")
    @Min(value = 0, message = "O estoque não pode ser negativo")
    @Column(nullable = false)
    private Integer estoque = 0;

    @Column(length = 1000)
    private String imagemUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    private Boolean destaque = false;
    private Boolean ativo = true;
    private LocalDateTime dataCadastro = LocalDateTime.now();

    public Produto() {}

    public Produto(String nome, String descricao, BigDecimal preco, BigDecimal precoPromocional, Integer estoque, String imagemUrl, Categoria categoria, Boolean destaque) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.precoPromocional = precoPromocional;
        this.estoque = estoque;
        this.imagemUrl = imagemUrl;
        this.categoria = categoria;
        this.destaque = destaque;
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public BigDecimal getPrecoPromocional() { return precoPromocional; }
    public void setPrecoPromocional(BigDecimal precoPromocional) { this.precoPromocional = precoPromocional; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Boolean getDestaque() { return destaque; }
    public void setDestaque(Boolean destaque) { this.destaque = destaque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public boolean isDisponivel() {
        return ativo != null && ativo && estoque != null && estoque > 0;
    }
}
