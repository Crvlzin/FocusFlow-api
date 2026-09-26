package com.focusflow.api.service;

import com.focusflow.api.dto.AuthResponse;
import com.focusflow.api.dto.LoginRequest;
import com.focusflow.api.dto.RegisterRequest;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.UsuarioRepository;
import com.focusflow.api.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este e-mail já está em uso.");
        }

        var usuario = Usuario.builder()
                .nmUsuario(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .role("ROLE_USER")
                .build();

        usuario = usuarioRepository.save(usuario);
        var token = tokenService.generateToken(usuario);

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getNmUsuario(), usuario.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        var usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos."));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
        }

        var token = tokenService.generateToken(usuario);

        return new AuthResponse(token, usuario.getIdUsuario(), usuario.getNmUsuario(), usuario.getEmail());
    }

    public Usuario getUsuarioAutenticado() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado.");
        }
        return usuario;
    }
}
