package com.focusflow.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.UUID;

public record RevisaoResponse(
    UUID idRevisao,
    UUID idMateria,
    String nomeMateria,
    UUID idAssunto,
    String nomeAssunto,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dtRevisao,
    int nivelCiclo,
    boolean flConcluida,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dtConclusao,
    boolean atrasada
) {}
