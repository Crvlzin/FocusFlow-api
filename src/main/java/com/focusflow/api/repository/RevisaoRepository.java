package com.focusflow.api.repository;

import com.focusflow.api.entity.Revisao;
import com.focusflow.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RevisaoRepository extends JpaRepository<Revisao, UUID> {

    List<Revisao> findAllByUsuarioOrderByDtRevisaoAsc(Usuario usuario);

    List<Revisao> findAllByUsuarioAndFlConcluidaOrderByDtRevisaoAsc(Usuario usuario, boolean flConcluida);

    List<Revisao> findAllByUsuarioAndAssuntoIdAssuntoOrderByDtRevisaoAsc(Usuario usuario, UUID idAssunto);

    List<Revisao> findAllByUsuarioAndAssuntoIdAssuntoAndFlConcluidaOrderByDtRevisaoAsc(
            Usuario usuario, UUID idAssunto, boolean flConcluida
    );

    Optional<Revisao> findByIdRevisaoAndUsuario(UUID idRevisao, Usuario usuario);
}
