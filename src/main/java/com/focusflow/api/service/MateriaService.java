package com.focusflow.api.service;

import com.focusflow.api.dto.AssuntoResponse;
import com.focusflow.api.dto.MateriaRequest;
import com.focusflow.api.dto.MateriaResponse;
import com.focusflow.api.entity.Materia;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Transactional(readOnly = true)
    public List<MateriaResponse> listarMaterias(Usuario usuario) {
        return materiaRepository.findAllByUsuarioOrderByNmMateriaAsc(usuario)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MateriaResponse buscarPorId(UUID idMateria, Usuario usuario) {
        var materia = buscarMateriaDoUsuario(idMateria, usuario);
        return toResponse(materia);
    }

    @Transactional
    public MateriaResponse criarMateria(MateriaRequest request, Usuario usuario) {
        if (materiaRepository.existsByNmMateriaIgnoreCaseAndUsuario(request.nome().trim(), usuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já possui uma matéria cadastrada com este nome.");
        }

        var materia = Materia.builder()
                .usuario(usuario)
                .nmMateria(request.nome().trim())
                .build();

        materia = materiaRepository.save(materia);
        return toResponse(materia);
    }

    @Transactional
    public MateriaResponse atualizarMateria(UUID idMateria, MateriaRequest request, Usuario usuario) {
        var materia = buscarMateriaDoUsuario(idMateria, usuario);
        var novoNome = request.nome().trim();

        if (!materia.getNmMateria().equalsIgnoreCase(novoNome) &&
                materiaRepository.existsByNmMateriaIgnoreCaseAndUsuario(novoNome, usuario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Você já possui uma matéria cadastrada com este nome.");
        }

        materia.setNmMateria(novoNome);
        materia = materiaRepository.save(materia);
        return toResponse(materia);
    }

    @Transactional
    public void deletarMateria(UUID idMateria, Usuario usuario) {
        var materia = buscarMateriaDoUsuario(idMateria, usuario);
        materiaRepository.delete(materia);
    }

    public Materia buscarMateriaDoUsuario(UUID idMateria, Usuario usuario) {
        return materiaRepository.findByIdMateriaAndUsuario(idMateria, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matéria não encontrada."));
    }

    private MateriaResponse toResponse(Materia materia) {
        var assuntosResponse = materia.getAssuntos() != null
                ? materia.getAssuntos().stream()
                    .map(a -> new AssuntoResponse(a.getIdAssunto(), materia.getIdMateria(), a.getNmAssunto()))
                    .toList()
                : List.<AssuntoResponse>of();

        return new MateriaResponse(
                materia.getIdMateria(),
                materia.getNmMateria(),
                assuntosResponse.size(),
                assuntosResponse
        );
    }
}
