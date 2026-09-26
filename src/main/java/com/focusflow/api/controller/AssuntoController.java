package com.focusflow.api.controller;

import com.focusflow.api.dto.AssuntoRequest;
import com.focusflow.api.dto.AssuntoResponse;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.service.AssuntoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/assuntos")
@RequiredArgsConstructor
@Tag(name = "Assuntos", description = "Endpoints para gerenciamento pontual de assuntos")
public class AssuntoController {

    private final AssuntoService assuntoService;

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar assunto", description = "Atualiza o nome de um assunto existente")
    public ResponseEntity<AssuntoResponse> atualizar(@PathVariable("id") UUID id,
                                                     @Valid @RequestBody AssuntoRequest request,
                                                     @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(assuntoService.atualizarAssunto(id, request, usuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar assunto", description = "Remove exclusivamente este assunto (a matéria pai é mantida)")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id,
                                        @AuthenticationPrincipal Usuario usuario) {
        assuntoService.deletarAssunto(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
