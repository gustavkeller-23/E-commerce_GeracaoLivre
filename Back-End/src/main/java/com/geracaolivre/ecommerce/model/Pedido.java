package com.geracaolivre.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroPedido; // NP (ex: GL-2026-0001)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha", "enderecos"})
    private Usuario usuario;

    private LocalDateTime dataPedido = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorSubtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    private String cupomAplicado;

    // Dados de entrega congelados no momento do pedido
    private String cepEntrega;
    private String enderecoEntrega;
    private String numeroEntrega;
    private String complementoEntrega;
    private String bairroEntrega;
    private String cidadeEntrega;
    private String estadoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pagamento pagamento;

    public Pedido() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public BigDecimal getValorSubtotal() { return valorSubtotal; }
    public void setValorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getCupomAplicado() { return cupomAplicado; }
    public void setCupomAplicado(String cupomAplicado) { this.cupomAplicado = cupomAplicado; }

    public String getCepEntrega() { return cepEntrega; }
    public void setCepEntrega(String cepEntrega) { this.cepEntrega = cepEntrega; }

    public String getEnderecoEntrega() { return enderecoEntrega; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }

    public String getNumeroEntrega() { return numeroEntrega; }
    public void setNumeroEntrega(String numeroEntrega) { this.numeroEntrega = numeroEntrega; }

    public String getComplementoEntrega() { return complementoEntrega; }
    public void setComplementoEntrega(String complementoEntrega) { this.complementoEntrega = complementoEntrega; }

    public String getBairroEntrega() { return bairroEntrega; }
    public void setBairroEntrega(String bairroEntrega) { this.bairroEntrega = bairroEntrega; }

    public String getCidadeEntrega() { return cidadeEntrega; }
    public void setCidadeEntrega(String cidadeEntrega) { this.cidadeEntrega = cidadeEntrega; }

    public String getEstadoEntrega() { return estadoEntrega; }
    public void setEstadoEntrega(String estadoEntrega) { this.estadoEntrega = estadoEntrega; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public Pagamento getPagamento() { return pagamento; }
    public void setPagamento(Pagamento pagamento) { this.pagamento = pagamento; }
}
