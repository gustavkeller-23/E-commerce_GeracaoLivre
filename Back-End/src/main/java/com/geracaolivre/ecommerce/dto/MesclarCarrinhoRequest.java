package com.geracaolivre.ecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record MesclarCarrinhoRequest(
    @NotBlank @Pattern(regexp = "[a-zA-Z0-9-]{10,100}") String chave,
    @NotNull @Size(max = 100) List<@Valid AdicionarCarrinhoRequest> itens
) {}
