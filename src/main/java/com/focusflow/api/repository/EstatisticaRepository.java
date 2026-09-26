package com.focusflow.api.repository;

import com.focusflow.api.entity.Estatistica;
import com.focusflow.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstatisticaRepository extends JpaRepository<Estatistica, UUID> {

    List<Estatistica> findAllByUsuarioOrderByDtRegistroDesc(Usuario usuario);

    List<Estatistica> findAllByUsuarioAndAssuntoIdAssuntoOrderByDtRegistroDesc(Usuario usuario, UUID idAssunto);

    List<Estatistica> findAllByUsuarioAndDtRegistroBetweenOrderByDtRegistroDesc(Usuario usuario, OffsetDateTime inicio, OffsetDateTime fim);

    List<Estatistica> findAllByUsuarioAndAssuntoIdAssuntoAndDtRegistroBetweenOrderByDtRegistroDesc(
            Usuario usuario, UUID idAssunto, OffsetDateTime inicio, OffsetDateTime fim
    );

    Optional<Estatistica> findByIdEstatisticaAndUsuario(UUID idEstatistica, Usuario usuario);
}
