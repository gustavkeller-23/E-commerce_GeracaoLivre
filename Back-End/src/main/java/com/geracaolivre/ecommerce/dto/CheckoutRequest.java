package com.geracaolivre.ecommerce.dto;

import com.geracaolivre.ecommerce.model.FormaPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotBlank(message = "O CEP é obrigatório")
    private String cep;

    @NotBlank(message = "A rua é obrigatória")
    private String rua;

    @NotBlank(message = "O número é obrigatório")
    private String numero;

    private String complemento;

    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório")
    private String estado;

    @NotNull(message = "Selecione a forma de pagamento")
    private FormaPagamento formaPagamento;

    private String cupom;

    // Dados do cartão de crédito (se aplicável)
    private String numeroCartao;
    private String nomeCartao;
    private String validadeCartao;
    private String cvvCartao;
    private Integer parcelas = 1;

    public CheckoutRequest() {}

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getCupom() { return cupom; }
    public void setCupom(String cupom) { this.cupom = cupom; }

    public String getNumeroCartao() { return numeroCartao; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }

    public String getNomeCartao() { return nomeCartao; }
    public void setNomeCartao(String nomeCartao) { this.nomeCartao = nomeCartao; }

    public String getValidadeCartao() { return validadeCartao; }
    public void setValidadeCartao(String validadeCartao) { this.validadeCartao = validadeCartao; }

    public String getCvvCartao() { return cvvCartao; }
    public void setCvvCartao(String cvvCartao) { this.cvvCartao = cvvCartao; }

    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }
}
