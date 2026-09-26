package com.focusflow.api.service;

import com.focusflow.api.dto.RevisaoGerarCiclosRequest;
import com.focusflow.api.dto.RevisaoRequest;
import com.focusflow.api.dto.RevisaoResponse;
import com.focusflow.api.dto.RevisaoResumoResponse;
import com.focusflow.api.entity.Revisao;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.RevisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RevisaoService {

    private final RevisaoRepository revisaoRepository;
    private final AssuntoService assuntoService;

    @Transactional(readOnly = true)
    public List<RevisaoResponse> listar(Usuario usuario, String filtro, UUID idAssunto) {
        List<Revisao> base;
        if (idAssunto != null) {
            base = revisaoRepository.findAllByUsuarioAndAssuntoIdAssuntoOrderByDtRevisaoAsc(usuario, idAssunto);
        } else {
            base = revisaoRepository.findAllByUsuarioOrderByDtRevisaoAsc(usuario);
        }

        var hoje = LocalDate.now();

        if (filtro != null && !filtro.isBlank()) {
            base = switch (filtro.toLowerCase().trim()) {
                case "hoje" -> base.stream()
                        .filter(r -> !r.isFlConcluida() && r.getDtRevisao().isEqual(hoje))
                        .toList();
                case "atrasadas" -> base.stream()
                        .filter(Revisao::isAtrasada)
                        .toList();
                case "pendentes" -> base.stream()
                        .filter(r -> !r.isFlConcluida())
                        .toList();
                case "concluidas" -> base.stream()
                        .filter(Revisao::isFlConcluida)
                        .toList();
                default -> base;
            };
        }

        return base.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RevisaoResumoResponse obterResumo(Usuario usuario) {
        var todas = revisaoRepository.findAllByUsuarioOrderByDtRevisaoAsc(usuario);
        var hoje = LocalDate.now();

        long totalParaHoje = todas.stream()
                .filter(r -> !r.isFlConcluida() && r.getDtRevisao().isEqual(hoje))
                .count();

        long totalAtrasadas = todas.stream()
                .filter(Revisao::isAtrasada)
                .count();

        long totalPendentes = todas.stream()
                .filter(r -> !r.isFlConcluida())
                .count();

        long totalConcluidas = todas.stream()
                .filter(Revisao::isFlConcluida)
                .count();

        return new RevisaoResumoResponse(totalParaHoje, totalAtrasadas, totalPendentes, totalConcluidas);
    }

    @Transactional(readOnly = true)
    public RevisaoResponse buscarPorId(UUID id, Usuario usuario) {
        var revisao = buscarRevisaoDoUsuario(id, usuario);
        return toResponse(revisao);
    }

    @Transactional
    public List<RevisaoResponse> gerarCiclos(RevisaoGerarCiclosRequest request, Usuario usuario) {
        var assunto = assuntoService.buscarAssuntoDoUsuario(request.idAssunto(), usuario);
        var dataBase = (request.dataBase() != null) ? request.dataBase() : LocalDate.now();

        // Ciclos clássicos da Curva de Esquecimento / Repetição Espaçada:
        // Ciclo 1: D+1 (24 horas)
        // Ciclo 2: D+7 (1 semana)
        // Ciclo 3: D+30 (1 mês)
        // Ciclo 4: D+60 (2 meses)
        int[] dias = {1, 7, 30, 60};
        List<Revisao> novasRevisoes = new ArrayList<>();

        for (int ciclo = 1; ciclo <= 4; ciclo++) {
            var rev = Revisao.builder()
                    .usuario(usuario)
                    .assunto(assunto)
                    .dtRevisao(dataBase.plusDays(dias[ciclo - 1]))
                    .nivelCiclo(ciclo)
                    .flConcluida(false)
                    .build();
            novasRevisoes.add(rev);
        }

        novasRevisoes = revisaoRepository.saveAll(novasRevisoes);
        return novasRevisoes.stream().map(this::toResponse).toList();
    }

    @Transactional
    public RevisaoResponse criar(RevisaoRequest request, Usuario usuario) {
        var assunto = assuntoService.buscarAssuntoDoUsuario(request.idAssunto(), usuario);

        var revisao = Revisao.builder()
                .usuario(usuario)
                .assunto(assunto)
                .dtRevisao(request.dtRevisao())
                .nivelCiclo(request.nivelCiclo())
                .flConcluida(Boolean.TRUE.equals(request.flConcluida()))
                .dtConclusao(request.dtConclusao())
                .build();

        revisao = revisaoRepository.save(revisao);
        return toResponse(revisao);
    }

    @Transactional
    public RevisaoResponse concluir(UUID id, Boolean concluida, LocalDate dtConclusao, Usuario usuario) {
        var revisao = buscarRevisaoDoUsuario(id, usuario);

        boolean novoStatus = (concluida != null) ? concluida : !revisao.isFlConcluida();
        revisao.setFlConcluida(novoStatus);

        if (novoStatus) {
            revisao.setDtConclusao(dtConclusao != null ? dtConclusao : LocalDate.now());
        } else {
            revisao.setDtConclusao(null);
        }

        revisao = revisaoRepository.save(revisao);
        return toResponse(revisao);
    }

    @Transactional
    public RevisaoResponse atualizar(UUID id, RevisaoRequest request, Usuario usuario) {
        var revisao = buscarRevisaoDoUsuario(id, usuario);
        var assunto = assuntoService.buscarAssuntoDoUsuario(request.idAssunto(), usuario);

        revisao.setAssunto(assunto);
        revisao.setDtRevisao(request.dtRevisao());
        revisao.setNivelCiclo(request.nivelCiclo());
        if (request.flConcluida() != null) {
            revisao.setFlConcluida(request.flConcluida());
        }
        revisao.setDtConclusao(request.dtConclusao());

        revisao = revisaoRepository.save(revisao);
        return toResponse(revisao);
    }

    @Transactional
    public void deletar(UUID id, Usuario usuario) {
        var revisao = buscarRevisaoDoUsuario(id, usuario);
        // Exclui exclusivamente este agendamento de revisão pontual
        revisaoRepository.delete(revisao);
    }

    private Revisao buscarRevisaoDoUsuario(UUID id, Usuario usuario) {
        return revisaoRepository.findByIdRevisaoAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Revisão não encontrada."));
    }

    private RevisaoResponse toResponse(Revisao r) {
        var assunto = r.getAssunto();
        var materia = assunto != null ? assunto.getMateria() : null;

        return new RevisaoResponse(
                r.getIdRevisao(),
                materia != null ? materia.getIdMateria() : null,
                materia != null ? materia.getNmMateria() : null,
                assunto != null ? assunto.getIdAssunto() : null,
                assunto != null ? assunto.getNmAssunto() : null,
                r.getDtRevisao(),
                r.getNivelCiclo(),
                r.isFlConcluida(),
                r.getDtConclusao(),
                r.isAtrasada()
        );
    }
}
