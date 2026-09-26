package com.focusflow.api.controller;

import com.focusflow.api.dto.RevisaoGerarCiclosRequest;
import com.focusflow.api.dto.RevisaoRequest;
import com.focusflow.api.dto.RevisaoResponse;
import com.focusflow.api.dto.RevisaoResumoResponse;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.service.RevisaoService;
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
@RequestMapping("/revisoes")
@RequiredArgsConstructor
@Tag(name = "Revisões", description = "Endpoints para gerenciamento do sistema de repetição espaçada")
public class RevisaoController {

    private final RevisaoService revisaoService;

    @GetMapping
    @Operation(summary = "Listar revisões", description = "Lista revisões com filtros (hoje, atrasadas, pendentes, concluidas ou por assunto)")
    public ResponseEntity<List<RevisaoResponse>> listar(
            @RequestParam(name = "filtro", required = false) String filtro,
            @RequestParam(name = "idAssunto", required = false) UUID idAssunto,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(revisaoService.listar(usuario, filtro, idAssunto));
    }

    @GetMapping("/resumo")
    @Operation(summary = "Resumo do painel de revisões", description = "Retorna contadores de revisões para hoje, atrasadas, pendentes e concluídas")
    public ResponseEntity<RevisaoResumoResponse> obterResumo(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(revisaoService.obterResumo(usuario));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar revisão por ID", description = "Retorna os detalhes de um agendamento de revisão específico")
    public ResponseEntity<RevisaoResponse> buscarPorId(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(revisaoService.buscarPorId(id, usuario));
    }

    @PostMapping
    @Operation(summary = "Criar revisão individual", description = "Agenda manualmente uma revisão para uma data específica")
    public ResponseEntity<RevisaoResponse> criar(
            @Valid @RequestBody RevisaoRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var response = revisaoService.criar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/gerar-ciclos")
    @Operation(summary = "Gerar os 4 ciclos de repetição espaçada", description = "Gera automaticamente revisões para D+1, D+7, D+30 e D+60")
    public ResponseEntity<List<RevisaoResponse>> gerarCiclos(
            @Valid @RequestBody RevisaoGerarCiclosRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        var response = revisaoService.gerarCiclos(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Marcar revisão como concluída", description = "Atualiza o status para concluído e salva a data de conclusão")
    public ResponseEntity<RevisaoResponse> concluir(
            @PathVariable("id") UUID id,
            @RequestParam(name = "concluida", required = false) Boolean concluida,
            @RequestParam(name = "dtConclusao", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dtConclusao,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(revisaoService.concluir(id, concluida, dtConclusao, usuario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar revisão", description = "Modifica data ou ciclo de uma revisão agendada")
    public ResponseEntity<RevisaoResponse> atualizar(
            @PathVariable("id") UUID id,
            @Valid @RequestBody RevisaoRequest request,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(revisaoService.atualizar(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar revisão", description = "Exclui apenas o agendamento desta revisão pontual")
    public ResponseEntity<Void> deletar(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        revisaoService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
