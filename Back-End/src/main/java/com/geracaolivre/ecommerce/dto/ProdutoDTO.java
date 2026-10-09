package com.geracaolivre.ecommerce.dto;

import com.geracaolivre.ecommerce.model.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ProdutoDTO {
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @jakarta.validation.constraints.Digits(integer = 8, fraction = 2)
    private BigDecimal preco;

    @DecimalMin("0.01")
    @jakarta.validation.constraints.Digits(integer = 8, fraction = 2)
    private BigDecimal precoPromocional;

    @NotNull(message = "Estoque é obrigatório")
    @DecimalMin(value = "0", message = "Estoque não pode ser negativo")
    @jakarta.validation.constraints.Digits(integer = 10, fraction = 2)
    private BigDecimal estoque;

    private String imagemUrl;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoriaId;

    private String categoriaNome;
    private Boolean destaque = false;
    private Boolean ativo = true;


    @NotBlank
    @jakarta.validation.constraints.Pattern(regexp = "GORGURAO", message = "O catálogo aceita apenas fitas de gorgurão")
    private String material = "GORGURAO";
    @NotBlank
    private String sku;
    @NotNull
    @Min(1)
    private Integer larguraMm;
    @NotBlank
    private String cor;
    private String estampa;
    @NotNull
    @DecimalMin("0.01")
    @jakarta.validation.constraints.Digits(integer = 10, fraction = 2)
    private BigDecimal quantidadeMinima;
    @NotNull
    @DecimalMin("0.01")
    @jakarta.validation.constraints.Digits(integer = 10, fraction = 2)
    private BigDecimal incremento;

    public ProdutoDTO() {}

    public ProdutoDTO(Produto produto) {
        this.material = produto.getMaterial();
        this.sku = produto.getSku();
        this.larguraMm = produto.getLarguraMm();
        this.cor = produto.getCor();
        this.estampa = produto.getEstampa();
        this.quantidadeMinima = produto.getQuantidadeMinima();
        this.incremento = produto.getIncremento();
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.precoPromocional = produto.getPrecoPromocional();
        this.estoque = produto.getEstoque();
        this.imagemUrl = produto.getImagemUrl();
        if (produto.getCategoria() != null) {
            this.categoriaId = produto.getCategoria().getId();
            this.categoriaNome = produto.getCategoria().getNome();
        }
        this.destaque = produto.getDestaque();
        this.ativo = produto.getAtivo();
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

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }

    public Boolean getDestaque() { return destaque; }
    public void setDestaque(Boolean destaque) { this.destaque = destaque; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
