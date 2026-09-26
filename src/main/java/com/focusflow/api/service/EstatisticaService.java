package com.focusflow.api.service;

import com.focusflow.api.dto.EstatisticaRequest;
import com.focusflow.api.dto.EstatisticaResponse;
import com.focusflow.api.dto.EstatisticaResumoResponse;
import com.focusflow.api.entity.Estatistica;
import com.focusflow.api.entity.Usuario;
import com.focusflow.api.repository.EstatisticaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EstatisticaService {

    private final EstatisticaRepository estatisticaRepository;
    private final AssuntoService assuntoService;

    @Transactional(readOnly = true)
    public List<EstatisticaResponse> listar(Usuario usuario, UUID idAssunto, LocalDate dataInicio, LocalDate dataFim) {
        List<Estatistica> lista;

        if (dataInicio != null || dataFim != null) {
            var inicio = (dataInicio != null ? dataInicio : LocalDate.of(2000, 1, 1))
                    .atStartOfDay().atOffset(ZoneOffset.ofHours(-3));
            var fim = (dataFim != null ? dataFim : LocalDate.now().plusDays(1))
                    .atTime(23, 59, 59).atOffset(ZoneOffset.ofHours(-3));

            if (idAssunto != null) {
                lista = estatisticaRepository.findAllByUsuarioAndAssuntoIdAssuntoAndDtRegistroBetweenOrderByDtRegistroDesc(
                        usuario, idAssunto, inicio, fim
                );
            } else {
                lista = estatisticaRepository.findAllByUsuarioAndDtRegistroBetweenOrderByDtRegistroDesc(
                        usuario, inicio, fim
                );
            }
        } else if (idAssunto != null) {
            lista = estatisticaRepository.findAllByUsuarioAndAssuntoIdAssuntoOrderByDtRegistroDesc(usuario, idAssunto);
        } else {
            lista = estatisticaRepository.findAllByUsuarioOrderByDtRegistroDesc(usuario);
        }

        return lista.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EstatisticaResumoResponse obterResumo(Usuario usuario, LocalDate dataInicio, LocalDate dataFim) {
        var registros = listar(usuario, null, dataInicio, dataFim);

        long totalMinutos = 0;
        long totalCertas = 0;
        long totalErradas = 0;

        for (var r : registros) {
            totalMinutos += r.qtdMinutos();
            totalCertas += r.qtdCertas();
            totalErradas += r.qtdErradas();
        }

        long totalQuestoes = totalCertas + totalErradas;
        double taxaGeral = (totalQuestoes > 0)
                ? Math.round(((double) totalCertas / totalQuestoes) * 1000.0) / 10.0
                : 0.0;

        return new EstatisticaResumoResponse(
                totalMinutos,
                totalCertas,
                totalErradas,
                totalQuestoes,
                taxaGeral,
                registros.size()
        );
    }

    @Transactional(readOnly = true)
    public EstatisticaResponse buscarPorId(UUID id, Usuario usuario) {
        var estatistica = buscarEstatisticaDoUsuario(id, usuario);
        return toResponse(estatistica);
    }

    @Transactional
    public EstatisticaResponse criar(EstatisticaRequest request, Usuario usuario) {
        var assunto = assuntoService.buscarAssuntoDoUsuario(request.idAssunto(), usuario);

        // Se o usuário informou data retroativa (ex: ontem), respeita a data informada!
        OffsetDateTime dtRegistro = resolverDataRegistro(request);

        var estatistica = Estatistica.builder()
                .usuario(usuario)
                .assunto(assunto)
                .qtdCertas(request.qtdCertas())
                .qtdErradas(request.qtdErradas())
                .qtdMinutos(request.qtdMinutos())
                .dtRegistro(dtRegistro)
                .build();

        estatistica = estatisticaRepository.save(estatistica);
        return toResponse(estatistica);
    }

    @Transactional
    public EstatisticaResponse atualizar(UUID id, EstatisticaRequest request, Usuario usuario) {
        var estatistica = buscarEstatisticaDoUsuario(id, usuario);
        var assunto = assuntoService.buscarAssuntoDoUsuario(request.idAssunto(), usuario);

        estatistica.setAssunto(assunto);
        estatistica.setQtdCertas(request.qtdCertas());
        estatistica.setQtdErradas(request.qtdErradas());
        estatistica.setQtdMinutos(request.qtdMinutos());

        if (request.dtRegistro() != null || request.dataEstudo() != null) {
            estatistica.setDtRegistro(resolverDataRegistro(request));
        }

        estatistica = estatisticaRepository.save(estatistica);
        return toResponse(estatistica);
    }

    @Transactional
    public void deletar(UUID id, Usuario usuario) {
        // Busca garantindo que o registro de estatística pertence ao usuário
        var estatistica = buscarEstatisticaDoUsuario(id, usuario);
        // Exclui SOMENTE este registro diário. O assunto, a matéria e outros dias continuam salvos!
        estatisticaRepository.delete(estatistica);
    }

    private Estatistica buscarEstatisticaDoUsuario(UUID id, Usuario usuario) {
        return estatisticaRepository.findByIdEstatisticaAndUsuario(id, usuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de estatística não encontrado."));
    }

    private OffsetDateTime resolverDataRegistro(EstatisticaRequest request) {
        if (request.dtRegistro() != null) {
            return request.dtRegistro();
        }
        if (request.dataEstudo() != null) {
            // Define o estudo no dia informado às 12:00 no fuso de Brasília (UTC-3)
            return request.dataEstudo().atTime(12, 0).atOffset(ZoneOffset.ofHours(-3));
        }
        return OffsetDateTime.now();
    }

    private EstatisticaResponse toResponse(Estatistica e) {
        var assunto = e.getAssunto();
        var materia = assunto != null ? assunto.getMateria() : null;

        LocalDate dataEstudo = e.getDtRegistro() != null ? e.getDtRegistro().toLocalDate() : null;

        return new EstatisticaResponse(
                e.getIdEstatistica(),
                materia != null ? materia.getIdMateria() : null,
                materia != null ? materia.getNmMateria() : null,
                assunto != null ? assunto.getIdAssunto() : null,
                assunto != null ? assunto.getNmAssunto() : null,
                e.getQtdCertas(),
                e.getQtdErradas(),
                e.getQtdTotalCalculada(),
                e.getQtdMinutos(),
                e.getTaxaAcerto(),
                dataEstudo,
                e.getDtRegistro()
        );
    }
}
