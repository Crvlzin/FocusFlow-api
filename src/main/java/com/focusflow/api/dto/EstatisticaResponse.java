package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EstatisticaResponse(
    UUID idEstatistica,
    UUID idMateria,
    String nomeMateria,
    UUID idAssunto,
    String nomeAssunto,
    int qtdCertas,
    int qtdErradas,
    int qtdTotal,
    int qtdMinutos,
    double taxaAcerto,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dataEstudo,
    OffsetDateTime dtRegistro
) {}
