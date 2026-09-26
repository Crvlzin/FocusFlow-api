package com.focusflow.api.controller;

import com.focusflow.api.dto.EstatisticaRequest;
import com.focusflow.api.dto.EstatisticaResponse;
import com.focusflow.api.dto.EstatisticaResumoResponse;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.service.EstatisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/estatisticas")
@RequiredArgsConstructor
@Tag(name = "Estatísticas", description = "Endpoints para registro e análise de métricas de estudo")
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    @GetMapping
    @Operation(summary = "Listar histórico de estudos", description = "Retorna sessões de estudo com filtros opcionais por assunto e período")
    public ResponseEntity<List<EstatisticaResponse>> listar(
            @RequestParam(name = "idAssunto", required = false) UUID idAssunto,
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(estatisticaService.listar(usuario, idAssunto, dataInicio, dataFim));
    }

    @GetMapping("/resumo")
    @Operation(summary = "Resumo analítico para dashboard", description = "Calcula totais de minutos, acertos, erros e taxa de precisão geral")
    public ResponseEntity<EstatisticaResumoResponse> obterResumo(
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(estatisticaService.obterResumo(usuario, dataInicio, dataFim));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sessão por ID", description = "Retorna os detalhes de uma sessão de estudo específica")
    public ResponseEntity<EstatisticaResponse> buscarPorId(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(estatisticaService.buscarPorId(id, usuario));
    }

    @PostMapping
    @Operation(summary = "Registrar sessão de estudo", description = "Salva tempo, certas e erradas de um assunto (suporta data retroativa)")
    public ResponseEntity<EstatisticaResponse> criar(
            @Valid @RequestBody EstatisticaRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var response = estatisticaService.criar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar sessão de estudo", description = "Permite corrigir valores de uma sessão já registrada")
    public ResponseEntity<EstatisticaResponse> atualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody EstatisticaRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(estatisticaService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar registro específico", description = "Remove exclusivamente este registro diário sem apagar o assunto ou outros dias")
    public ResponseEntity<Void> deletar(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        estatisticaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
