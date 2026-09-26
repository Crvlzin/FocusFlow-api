package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CronogramaResponse(
    UUID idCronograma,
    Integer diaSemana,
    UUID idMateria,
    String nomeMateria,
    String tituloEstudo,
    @JsonFormat(pattern = "HH:mm")
    LocalTime horarioInicio,
    @JsonFormat(pattern = "HH:mm")
    LocalTime horarioFim,
    String observacao,
    boolean flConcluido,
    int ordem,
    OffsetDateTime dtCriacao
) {}
