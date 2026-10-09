package com.geracaolivre.ecommerce.config;

import com.geracaolivre.ecommerce.model.*;
import com.geracaolivre.ecommerce.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

@Component @Profile("!dev")
@ConditionalOnProperty(name = "geracaolivre.bootstrap.enabled", havingValue = "true")
public class AdminBootstrap implements CommandLineRunner {
    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String email;
    private final String senha;
    public AdminBootstrap(UsuarioRepository usuarios, PasswordEncoder encoder,
            @Value("${geracaolivre.bootstrap.email:}") String email,
            @Value("${geracaolivre.bootstrap.password:}") String senha) {
        this.usuarios = usuarios; this.encoder = encoder; this.email = email.trim().toLowerCase(Locale.ROOT); this.senha = senha;
    }
    @Override @Transactional
    public void run(String... args) {
        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+") || senha.length() < 12) {
            throw new IllegalArgumentException("Configure e-mail e senha de pelo menos 12 caracteres para o administrador inicial.");
        }
        var existente = usuarios.findByEmail(email);
        if (existente.isPresent()) {
            if (existente.get().getRole() != Role.ROLE_ADMIN) throw new IllegalStateException("O e-mail do administrador inicial pertence a uma conta de cliente.");
            return;
        }
        usuarios.save(new Usuario("Administração Geração Livre", email, encoder.encode(senha), null, Role.ROLE_ADMIN));
    }
}
