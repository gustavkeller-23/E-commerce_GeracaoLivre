package com.geracaolivre.ecommerce.dto;

import com.geracaolivre.ecommerce.model.Role;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String nome;
    private String email;
    private Role role;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String nome, String email, Role role) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
