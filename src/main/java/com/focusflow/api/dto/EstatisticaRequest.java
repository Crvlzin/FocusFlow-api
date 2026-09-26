package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EstatisticaRequest(
    @NotNull(message = "O ID do assunto é obrigatório")
    UUID idAssunto,

    @Min(value = 0, message = "A quantidade de certas não pode ser negativa")
    int qtdCertas,

    @Min(value = 0, message = "A quantidade de erradas não pode ser negativa")
    int qtdErradas,

    @Min(value = 0, message = "A quantidade de minutos não pode ser negativa")
    int qtdMinutos,

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2026-09-25", description = "Data retroativa do estudo (opcional, formato YYYY-MM-DD)")
    LocalDate dataEstudo,

    @Schema(description = "Data e hora completas com timezone (opcional)")
    OffsetDateTime dtRegistro
) {}
