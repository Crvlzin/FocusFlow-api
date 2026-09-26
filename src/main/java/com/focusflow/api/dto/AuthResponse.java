package com.focusflow.api.dto;

import java.util.UUID;

public record AuthResponse(
    String token,
    String type,
    UUID idUsuario,
    String nome,
    String email
) {
    public AuthResponse(String token, UUID idUsuario, String nome, String email) {
        this(token, "Bearer", idUsuario, nome, email);
    }
}
