package com.focusflow.api.dto;

public record EstatisticaResumoResponse(
    long totalMinutos,
    long totalCertas,
    long totalErradas,
    long totalQuestoes,
    double taxaAcertoGeral,
    long totalSessoes
) {}
