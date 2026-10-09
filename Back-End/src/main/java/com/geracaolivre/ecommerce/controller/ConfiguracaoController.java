package com.geracaolivre.ecommerce.controller;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
@RestController
public class ConfiguracaoController {
    @Value("${geracaolivre.demo:false}") private boolean demo;
    @GetMapping("/api/produtos/configuracao")
    public Map<String, Object> configuracao() { return Map.of("demonstracao", demo, "checkoutDisponivel", false, "unidade", "METRO"); }
}
