package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.dto.MensagemRequest;
import com.geracaolivre.ecommerce.model.Mensagem;
import com.geracaolivre.ecommerce.repository.MensagemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MensagemService {

    private final MensagemRepository mensagemRepository;

    public MensagemService(MensagemRepository mensagemRepository) {
        this.mensagemRepository = mensagemRepository;
    }

    @Transactional
    public Mensagem salvarMensagem(MensagemRequest request) {
        Mensagem mensagem = new Mensagem(
                request.getNome(),
                request.getEmail(),
                request.getTelefone(),
                request.getAssunto(),
                request.getConteudo()
        );
        return mensagemRepository.save(mensagem);
    }

    public List<Mensagem> listarTodas() {
        return mensagemRepository.findAllByOrderByDataEnvioDesc();
    }

    @Transactional
    public Mensagem responderMensagem(Long id, String resposta) {
        Mensagem mensagem = mensagemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada com id: " + id));

        mensagem.setResposta(resposta);
        mensagem.setRespondida(true);
        return mensagemRepository.save(mensagem);
    }
}
