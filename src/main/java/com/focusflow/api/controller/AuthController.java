package com.focusflow.api.controller;

import com.focusflow.api.dto.AuthResponse;
import com.focusflow.api.dto.LoginRequest;
import com.focusflow.api.dto.RegisterRequest;
import com.focusflow.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo usuário e retorna o token JWT")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Valida credenciais e retorna o token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Dados do usuário autenticado", description = "Retorna os dados do usuário atualmente logado")
    public ResponseEntity<Object> me() {
        var usuario = authService.getUsuarioAutenticado();
        return ResponseEntity.ok(java.util.Map.of(
                "idUsuario", usuario.getIdUsuario(),
                "nome", usuario.getNmUsuario(),
                "email", usuario.getEmail(),
                "role", usuario.getRole()
        ));
    }
}
