package com.geracaolivre.ecommerce;
import com.geracaolivre.ecommerce.config.AdminBootstrap;
import com.geracaolivre.ecommerce.model.*;
import com.geracaolivre.ecommerce.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class AdminBootstrapTest {
    @Test void naoPromoveClienteExistenteParaAdministrador() {
        var repository = mock(UsuarioRepository.class);
        var encoder = mock(PasswordEncoder.class);
        when(repository.findByEmail("admin@loja.local")).thenReturn(Optional.of(new Usuario("Cliente", "admin@loja.local", "hash", null, Role.ROLE_CLIENTE)));
        var bootstrap = new AdminBootstrap(repository, encoder, "admin@loja.local", "SenhaInicial123!");
        assertThrows(IllegalStateException.class, () -> bootstrap.run());
        verify(repository, never()).save(any());
    }
    @Test void nuncaSalvaSenhaInicialSemHash() {
        var repository = mock(UsuarioRepository.class);
        var encoder = mock(PasswordEncoder.class);
        when(repository.findByEmail("admin@loja.local")).thenReturn(Optional.empty());
        when(encoder.encode("SenhaInicial123!")).thenReturn("hash-seguro");
        new AdminBootstrap(repository, encoder, "ADMIN@loja.local", "SenhaInicial123!").run();
        verify(repository).save(argThat(user -> user.getSenha().equals("hash-seguro") && user.getRole() == Role.ROLE_ADMIN));
    }
}
