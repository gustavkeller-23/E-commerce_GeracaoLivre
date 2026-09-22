package com.geracaolivre.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MensagemRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private String telefone;

    @NotBlank(message = "Assunto é obrigatório")
    private String assunto;

    @NotBlank(message = "A mensagem não pode estar vazia")
    private String conteudo;

    public MensagemRequest() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getAssunto() { return assunto; }
    public void setAssunto(String assunto) { this.assunto = assunto; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
}
