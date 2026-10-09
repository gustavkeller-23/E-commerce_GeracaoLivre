package com.geracaolivre.ecommerce.service;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.transaction.support.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

@Service
public class CatalogoEventos {
    private final Set<SseEmitter> emitters = ConcurrentHashMap.newKeySet();
    public SseEmitter conectar() {
        SseEmitter emitter = new SseEmitter(300_000L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> { emitters.remove(emitter); emitter.complete(); });
        emitter.onError(error -> emitters.remove(emitter));
        try { emitter.send(SseEmitter.event().name("conectado").data("ok")); }
        catch (IOException e) { emitters.remove(emitter); }
        return emitter;
    }
    public void aposCommit() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { publicar(); }
            });
        } else publicar();
    }
    private void publicar() {
        for (SseEmitter emitter : emitters) {
            try { emitter.send(SseEmitter.event().name("catalogo").data("atualizado")); }
            catch (IOException | IllegalStateException e) { emitters.remove(emitter); emitter.complete(); }
        }
    }
}
