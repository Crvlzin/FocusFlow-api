package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record RevisaoRequest(
    @NotNull(message = "O ID do assunto é obrigatório")
    UUID idAssunto,

    @NotNull(message = "A data da revisão é obrigatória")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2026-09-27")
    LocalDate dtRevisao,

    @Min(value = 1, message = "O ciclo deve ser entre 1 e 4")
    @Max(value = 4, message = "O ciclo deve ser entre 1 e 4")
    int nivelCiclo,

    Boolean flConcluida,

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dtConclusao
) {}
