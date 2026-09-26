package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.UUID;

public record CronogramaRequest(
    @NotNull(message = "O dia da semana é obrigatório")
    @Min(value = 0, message = "O dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    @Max(value = 6, message = "O dia da semana deve ser entre 0 (Domingo) e 6 (Sábado)")
    Integer diaSemana,

    UUID idMateria,

    @NotBlank(message = "O título do estudo é obrigatório")
    @Size(min = 2, max = 255, message = "O título do estudo deve ter entre 2 e 255 caracteres")
    String tituloEstudo,

    @JsonFormat(pattern = "HH:mm")
    @Schema(type = "string", example = "14:00")
    LocalTime horarioInicio,

    @JsonFormat(pattern = "HH:mm")
    @Schema(type = "string", example = "16:00")
    LocalTime horarioFim,

    String observacao,

    Boolean flConcluido,

    Integer ordem
) {}
