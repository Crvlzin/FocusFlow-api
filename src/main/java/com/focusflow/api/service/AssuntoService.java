package com.focusflow.api.service;

import com.focusflow.api.dto.AssuntoRequest;
import com.focusflow.api.dto.AssuntoResponse;
import com.focusflow.api.entity.Assunto;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.AssuntoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssuntoService {

    private final AssuntoRepository assuntoRepository;
    private final MateriaService materiaService;

    @Transactional(readOnly = true)
    public List<AssuntoResponse> listarAssuntosPorMateria(UUID idMateria, Usuario usuario) {
        var materia = materiaService.buscarMateriaDoUsuario(idMateria, usuario);
        return assuntoRepository.findAllByMateriaOrderByNmAssuntoAsc(materia)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AssuntoResponse criarAssunto(UUID idMateria, AssuntoRequest request, Usuario usuario) {
        var materia = materiaService.buscarMateriaDoUsuario(idMateria, usuario);
        var nomeAssunto = request.nome().trim();

        if (assuntoRepository.existsByNmAssuntoIgnoreCaseAndMateria(nomeAssunto, materia)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta matéria já possui um assunto com este nome.");
        }

        var assunto = Assunto.builder()
                .materia(materia)
                .nmAssunto(nomeAssunto)
                .build();

        assunto = assuntoRepository.save(assunto);
        return toResponse(assunto);
    }

    @Transactional
    public AssuntoResponse atualizarAssunto(UUID idAssunto, AssuntoRequest request, Usuario usuario) {
        var assunto = buscarAssuntoDoUsuario(idAssunto, usuario);
        var novoNome = request.nome().trim();

        if (!assunto.getNmAssunto().equalsIgnoreCase(novoNome) &&
                assuntoRepository.existsByNmAssuntoIgnoreCaseAndMateria(novoNome, assunto.getMateria())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta matéria já possui um assunto com este nome.");
        }

        assunto.setNmAssunto(novoNome);
        assunto = assuntoRepository.save(assunto);
        return toResponse(assunto);
    }

    @Transactional
    public void deletarAssunto(UUID idAssunto, Usuario usuario) {
        // Busca garantindo que o assunto pertence a uma matéria do usuário logado
        var assunto = buscarAssuntoDoUsuario(idAssunto, usuario);
        // Exclui SOMENTE este assunto. A matéria pai e outros assuntos permanecem intocados!
        assuntoRepository.delete(assunto);
    }

    public Assunto buscarAssuntoDoUsuario(UUID idAssunto, Usuario usuario) {
        return assuntoRepository.findByIdAssuntoAndMateriaUsuario(idAssunto, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assunto não encontrado."));
    }

    private AssuntoResponse toResponse(Assunto assunto) {
        return new AssuntoResponse(
                assunto.getIdAssunto(),
                assunto.getMateria().getIdMateria(),
                assunto.getNmAssunto()
        );
    }
}
