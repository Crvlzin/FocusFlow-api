package com.focusflow.api.service;

import com.focusflow.api.dto.CronogramaRequest;
import com.focusflow.api.dto.CronogramaResponse;
import com.focusflow.api.entity.Cronograma;
import com.focusflow.api.entity.Materia;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.CronogramaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CronogramaService {

    private final CronogramaRepository cronogramaRepository;
    private final MateriaService materiaService;

    @Transactional(readOnly = true)
    public List<CronogramaResponse> listar(Usuario usuario, Integer diaSemana) {
        List<Cronograma> itens;
        if (diaSemana != null) {
            if (diaSemana < 0 || diaSemana > 6) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O dia da semana deve ser entre 0 e 6.");
            }
            itens = cronogramaRepository.findAllByUsuarioAndDiaSemanaOrderByOrdemAsc(usuario, diaSemana);
        } else {
            itens = cronogramaRepository.findAllByUsuarioOrderByDiaSemanaAscOrdemAsc(usuario);
        }

        return itens.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CronogramaResponse buscarPorId(UUID id, Usuario usuario) {
        var cronograma = buscarCronogramaDoUsuario(id, usuario);
        return toResponse(cronograma);
    }

    @Transactional
    public CronogramaResponse criar(CronogramaRequest request, Usuario usuario) {
        validarHorarios(request);

        Materia materia = null;
        if (request.idMateria() != null) {
            materia = materiaService.buscarMateriaDoUsuario(request.idMateria(), usuario);
        }

        int ordem = (request.ordem() != null)
                ? request.ordem()
                : cronogramaRepository.countByUsuarioAndDiaSemana(usuario, request.diaSemana());

        var cronograma = Cronograma.builder()
                .usuario(usuario)
                .diaSemana(request.diaSemana())
                .materia(materia)
                .tituloEstudo(request.tituloEstudo().trim())
                .horarioInicio(request.horarioInicio())
                .horarioFim(request.horarioFim())
                .observacao(request.observacao())
                .flConcluido(Boolean.TRUE.equals(request.flConcluido()))
                .ordem(ordem)
                .build();

        cronograma = cronogramaRepository.save(cronograma);
        return toResponse(cronograma);
    }

    @Transactional
    public CronogramaResponse atualizar(UUID id, CronogramaRequest request, Usuario usuario) {
        var cronograma = buscarCronogramaDoUsuario(id, usuario);
        validarHorarios(request);

        Materia materia = null;
        if (request.idMateria() != null) {
            materia = materiaService.buscarMateriaDoUsuario(request.idMateria(), usuario);
        }

        cronograma.setDiaSemana(request.diaSemana());
        cronograma.setMateria(materia);
        cronograma.setTituloEstudo(request.tituloEstudo().trim());
        cronograma.setHorarioInicio(request.horarioInicio());
        cronograma.setHorarioFim(request.horarioFim());
        cronograma.setObservacao(request.observacao());
        if (request.flConcluido() != null) {
            cronograma.setFlConcluido(request.flConcluido());
        }
        if (request.ordem() != null) {
            cronograma.setOrdem(request.ordem());
        }

        cronograma = cronogramaRepository.save(cronograma);
        return toResponse(cronograma);
    }

    @Transactional
    public CronogramaResponse toggleConcluido(UUID id, Boolean concluido, Usuario usuario) {
        var cronograma = buscarCronogramaDoUsuario(id, usuario);

        if (concluido != null) {
            cronograma.setFlConcluido(concluido);
        } else {
            cronograma.setFlConcluido(!cronograma.isFlConcluido());
        }

        cronograma = cronogramaRepository.save(cronograma);
        return toResponse(cronograma);
    }

    @Transactional
    public void deletar(UUID id, Usuario usuario) {
        var cronograma = buscarCronogramaDoUsuario(id, usuario);
        cronogramaRepository.delete(cronograma);
    }

    private Cronograma buscarCronogramaDoUsuario(UUID id, Usuario usuario) {
        return cronogramaRepository.findByIdCronogramaAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item do cronograma não encontrado."));
    }

    private void validarHorarios(CronogramaRequest request) {
        if (request.horarioInicio() != null && request.horarioFim() != null) {
            if (request.horarioFim().isBefore(request.horarioInicio())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O horário de término não pode ser anterior ao horário de início.");
            }
        }
    }

    private CronogramaResponse toResponse(Cronograma c) {
        UUID idMateria = c.getMateria() != null ? c.getMateria().getIdMateria() : null;
        String nomeMateria = c.getMateria() != null ? c.getMateria().getNmMateria() : null;

        return new CronogramaResponse(
                c.getIdCronograma(),
                c.getDiaSemana(),
                idMateria,
                nomeMateria,
                c.getTituloEstudo(),
                c.getHorarioInicio(),
                c.getHorarioFim(),
                c.getObservacao(),
                c.isFlConcluido(),
                c.getOrdem(),
                c.getDtCriacao()
        );
    }
}
