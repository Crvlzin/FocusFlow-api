package com.focusflow.api.dto;

import java.util.UUID;

public record AssuntoResponse(
    UUID idAssunto,
    UUID idMateria,
    String nome
) {}
