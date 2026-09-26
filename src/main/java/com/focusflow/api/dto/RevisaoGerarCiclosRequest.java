package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RevisaoGerarCiclosRequest(
    @NotNull(message = "O ID do assunto é obrigatório")
    UUID idAssunto,

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2026-09-26", description = "Data base do estudo inicial (opcional, padrão: data atual)")
    LocalDate dataBase
) {}
