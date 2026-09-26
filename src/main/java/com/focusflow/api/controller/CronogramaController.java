package com.focusflow.api.controller;

import com.focusflow.api.dto.CronogramaRequest;
import com.focusflow.api.dto.CronogramaResponse;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.service.CronogramaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cronograma")
@RequiredArgsConstructor
@Tag(name = "Cronograma", description = "Endpoints para gerenciamento do cronograma semanal de estudos")
public class CronogramaController {

    private final CronogramaService cronogramaService;

    @GetMapping
    @Operation(summary = "Listar cronograma", description = "Retorna todos os blocos de estudo da semana ou filtrados por dia")
    public ResponseEntity<List<CronogramaResponse>> listar(
            @RequestParam(name = "diaSemana", required = false) Integer diaSemana,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(cronogramaService.listar(usuario, diaSemana));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item do cronograma por ID", description = "Retorna os detalhes de um bloco de estudo específico")
    public ResponseEntity<CronogramaResponse> buscarPorId(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(cronogramaService.buscarPorId(id, usuario));
    }

    @PostMapping
    @Operation(summary = "Criar item no cronograma", description = "Adiciona um novo bloco de estudo à grade semanal")
    public ResponseEntity<CronogramaResponse> criar(
            @Valid @RequestBody CronogramaRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var response = cronogramaService.criar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item no cronograma", description = "Altera título, horários, matéria ou observações do bloco")
    public ResponseEntity<CronogramaResponse> atualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody CronogramaRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(cronogramaService.atualizar(id, request, usuario));
    }

    @PatchMapping("/{id}/concluido")
    @Operation(summary = "Marcar/Desmarcar como concluído", description = "Alterna ou define o status de conclusão do bloco de estudo")
    public ResponseEntity<CronogramaResponse> toggleConcluido(
            @PathVariable("id") UUID id,
            @RequestParam(name = "concluido", required = false) Boolean concluido,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(cronogramaService.toggleConcluido(id, concluido, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar item do cronograma", description = "Remove um bloco de estudo da grade semanal")
    public ResponseEntity<Void> deletar(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        cronogramaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
