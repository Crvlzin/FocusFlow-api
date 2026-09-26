package com.focusflow.api.dto;

import java.util.List;
import java.util.UUID;

public record MateriaResponse(
    UUID idMateria,
    String nome,
    int totalAssuntos,
    List<AssuntoResponse> assuntos
) {}
