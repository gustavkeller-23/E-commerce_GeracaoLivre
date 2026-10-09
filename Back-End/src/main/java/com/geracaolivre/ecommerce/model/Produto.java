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
    @DecimalMin(value = "0", message = "O estoque não pode ser negativo")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal estoque = BigDecimal.ZERO;

    @Column(length = 1000)
    private String imagemUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    private Boolean destaque = false;
    private Boolean ativo = true;
    private LocalDateTime dataCadastro = LocalDateTime.now();


    @jakarta.validation.constraints.Pattern(regexp = "GORGURAO")
    private String material = "GORGURAO";
    @Column(unique = true)
    private String sku;
    private Integer larguraMm;
    private String cor;
    private String estampa;
    @Column(precision = 12, scale = 2)
    private BigDecimal quantidadeMinima;
    @Column(precision = 12, scale = 2)
    private BigDecimal incremento;

    public Produto() {}

    public Produto(String nome, String descricao, BigDecimal preco, BigDecimal precoPromocional, BigDecimal estoque, String imagemUrl, Categoria categoria, Boolean destaque) {
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

    public String getMaterial() { return material; }
    public void setMaterial(String value) { this.material = value; }
    public String getSku() { return sku; }
    public void setSku(String value) { this.sku = value; }
    public Integer getLarguraMm() { return larguraMm; }
    public void setLarguraMm(Integer value) { this.larguraMm = value; }
    public String getCor() { return cor; }
    public void setCor(String value) { this.cor = value; }
    public String getEstampa() { return estampa; }
    public void setEstampa(String value) { this.estampa = value; }
    public BigDecimal getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(BigDecimal value) { this.quantidadeMinima = value; }
    public BigDecimal getIncremento() { return incremento; }
    public void setIncremento(BigDecimal value) { this.incremento = value; }

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

    public BigDecimal getEstoque() { return estoque; }
    public void setEstoque(BigDecimal estoque) { this.estoque = estoque; }

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

    public boolean isCatalogavel() { return Boolean.TRUE.equals(ativo) && "GORGURAO".equals(material); }

    public void validarQuantidade(BigDecimal quantidade) {
        if (!isDisponivel()) throw new IllegalStateException("Fita indisponível no momento.");
        if (quantidade == null || quantidade.scale() > 2 || quantidadeMinima == null || incremento == null
                || incremento.signum() <= 0 || quantidade.compareTo(quantidadeMinima) < 0
                || quantidade.subtract(quantidadeMinima).remainder(incremento).signum() != 0) {
            throw new IllegalArgumentException("Respeite o corte mínimo de " + quantidadeMinima + " m e o incremento de " + incremento + " m.");
        }
        if (quantidade.compareTo(estoque) > 0) throw new IllegalStateException("Estoque insuficiente. Disponível: " + estoque + " m.");
    }

    public boolean isDisponivel() {
        return isCatalogavel() && estoque != null && quantidadeMinima != null && estoque.compareTo(quantidadeMinima) >= 0;
    }
}
