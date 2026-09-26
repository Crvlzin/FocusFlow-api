package com.focusflow.api.controller;

import com.focusflow.api.dto.AssuntoRequest;
import com.focusflow.api.dto.AssuntoResponse;
import com.focusflow.api.dto.MateriaRequest;
import com.focusflow.api.dto.MateriaResponse;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.service.AssuntoService;
import com.focusflow.api.service.MateriaService;
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
@RequestMapping("/materias")
@RequiredArgsConstructor
@Tag(name = "Matérias", description = "Endpoints para gerenciamento de matérias e seus assuntos")
public class MateriaController {

    private final MateriaService materiaService;
    private final AssuntoService assuntoService;

    @GetMapping
    @Operation(summary = "Listar matérias", description = "Retorna todas as matérias do usuário autenticado")
    public ResponseEntity<List<MateriaResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(materiaService.listarMaterias(usuario));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar matéria por ID", description = "Retorna uma matéria específica do usuário com seus assuntos")
    public ResponseEntity<MateriaResponse> buscarPorId(@PathVariable("id") UUID id,
                                                       @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(materiaService.buscarPorId(id, usuario));
    }

    @PostMapping
    @Operation(summary = "Criar matéria", description = "Cadastra uma nova matéria para o usuário")
    public ResponseEntity<MateriaResponse> criar(@Valid @RequestBody MateriaRequest request,
                                                 @AuthenticationPrincipal Usuario usuario) {
        var response = materiaService.criarMateria(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar matéria", description = "Atualiza o nome de uma matéria do usuário")
    public ResponseEntity<MateriaResponse> atualizar(@PathVariable("id") UUID id,
                                                     @Valid @RequestBody MateriaRequest request,
                                                     @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(materiaService.atualizarMateria(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar matéria", description = "Remove uma matéria e seus assuntos associados")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id,
                                        @AuthenticationPrincipal Usuario usuario) {
        materiaService.deletarMateria(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/assuntos")
    @Operation(summary = "Listar assuntos da matéria", description = "Retorna os assuntos de uma matéria do usuário")
    public ResponseEntity<List<AssuntoResponse>> listarAssuntos(@PathVariable("id") UUID id,
                                                                @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(assuntoService.listarAssuntosPorMateria(id, usuario));
    }

    @PostMapping("/{id}/assuntos")
    @Operation(summary = "Adicionar assunto à matéria", description = "Cadastra um novo assunto vinculado à matéria")
    public ResponseEntity<AssuntoResponse> criarAssunto(@PathVariable("id") UUID id,
                                                        @Valid @RequestBody AssuntoRequest request,
                                                        @AuthenticationPrincipal Usuario usuario) {
        var response = assuntoService.criarAssunto(id, request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
