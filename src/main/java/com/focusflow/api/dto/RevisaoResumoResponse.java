package com.focusflow.api.dto;

public record RevisaoResumoResponse(
    long totalParaHoje,
    long totalAtrasadas,
    long totalPendentes,
    long totalConcluidas
) {}
