package com.geracaolivre.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    private String telefone;

    @NotBlank(message = "O assunto é obrigatório")
    private String assunto;

    @NotBlank(message = "A mensagem é obrigatória")
    @Column(length = 3000, nullable = false)
    private String conteudo;

    private LocalDateTime dataEnvio = LocalDateTime.now();

    private Boolean respondida = false;
    private String resposta;

    public Mensagem() {}

    public Mensagem(String nome, String email, String telefone, String assunto, String conteudo) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.assunto = assunto;
        this.conteudo = conteudo;
        this.dataEnvio = LocalDateTime.now();
        this.respondida = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public Boolean getRespondida() { return respondida; }
    public void setRespondida(Boolean respondida) { this.respondida = respondida; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }
}
