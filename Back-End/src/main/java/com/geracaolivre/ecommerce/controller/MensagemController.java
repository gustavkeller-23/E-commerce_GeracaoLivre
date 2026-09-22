package com.geracaolivre.ecommerce.controller;

import com.geracaolivre.ecommerce.dto.MensagemRequest;
import com.geracaolivre.ecommerce.model.Mensagem;
import com.geracaolivre.ecommerce.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private final MensagemService mensagemService;

    public MensagemController(MensagemService mensagemService) {
        this.mensagemService = mensagemService;
    }

    @PostMapping
    public ResponseEntity<?> enviarMensagem(@Valid @RequestBody MensagemRequest request) {
        Mensagem mensagem = mensagemService.salvarMensagem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", mensagem.getId(),
                "message", "Mensagem enviada com sucesso! Nossa equipe entrará em contato em breve."
        ));
    }
}
