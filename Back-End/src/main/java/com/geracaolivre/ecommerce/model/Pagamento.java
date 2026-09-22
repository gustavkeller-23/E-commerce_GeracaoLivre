package com.geracaolivre.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    private LocalDateTime dataPagamento;

    @Column(length = 2000)
    private String codigoPix;

    @Column(length = 100)
    private String codigoBarrasBoleto;

    private String cartaoUltimosDigitos;
    private Integer parcelas = 1;

    public Pagamento() {}

    public Pagamento(Pedido pedido, FormaPagamento formaPagamento, BigDecimal valor) {
        this.pedido = pedido;
        this.formaPagamento = formaPagamento;
        this.valor = valor;
        this.status = (formaPagamento == FormaPagamento.CARTAO_CREDITO) ? StatusPagamento.CONFIRMADO : StatusPagamento.PENDENTE;
        if (this.status == StatusPagamento.CONFIRMADO) {
            this.dataPagamento = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public String getCodigoPix() { return codigoPix; }
    public void setCodigoPix(String codigoPix) { this.codigoPix = codigoPix; }

    public String getCodigoBarrasBoleto() { return codigoBarrasBoleto; }
    public void setCodigoBarrasBoleto(String codigoBarrasBoleto) { this.codigoBarrasBoleto = codigoBarrasBoleto; }

    public String getCartaoUltimosDigitos() { return cartaoUltimosDigitos; }
    public void setCartaoUltimosDigitos(String cartaoUltimosDigitos) { this.cartaoUltimosDigitos = cartaoUltimosDigitos; }

    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }
}
