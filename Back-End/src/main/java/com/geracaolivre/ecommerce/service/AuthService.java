package com.geracaolivre.ecommerce.service;

import com.geracaolivre.ecommerce.dto.AuthRequest;
import com.geracaolivre.ecommerce.dto.AuthResponse;
import com.geracaolivre.ecommerce.dto.RegisterRequest;
import com.geracaolivre.ecommerce.model.Carrinho;
import com.geracaolivre.ecommerce.model.Role;
import com.geracaolivre.ecommerce.model.Usuario;
import com.geracaolivre.ecommerce.repository.CarrinhoRepository;
import com.geracaolivre.ecommerce.repository.UsuarioRepository;
import com.geracaolivre.ecommerce.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UsuarioRepository usuarioRepository,
                       CarrinhoRepository carrinhoRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.usuarioRepository = usuarioRepository;
        this.carrinhoRepository = carrinhoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("O e-mail informado já está cadastrado.");
        }

        Usuario usuario = new Usuario(
                request.getNome(),
                request.getEmail(),
                passwordEncoder.encode(request.getSenha()),
                request.getTelefone(),
                Role.ROLE_CLIENTE
        );

        Usuario salvo = usuarioRepository.save(usuario);

        // Cria o carrinho inicial do usuário
        Carrinho carrinho = new Carrinho(salvo);
        carrinhoRepository.save(carrinho);

        String token = tokenProvider.generateToken(salvo.getEmail(), salvo.getRole().name());
        return new AuthResponse(token, salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getRole());
    }

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            String token = tokenProvider.generateToken(usuario.getEmail(), usuario.getRole().name());
            return new AuthResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("E-mail ou senha incorretos.");
        }
    }

    public Usuario getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Usuário não autenticado.");
        }
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com e-mail: " + email));
    }
}
