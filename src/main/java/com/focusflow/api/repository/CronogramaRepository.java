package com.focusflow.api.repository;

import com.focusflow.api.entity.Cronograma;
import com.focusflow.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CronogramaRepository extends JpaRepository<Cronograma, UUID> {

    List<Cronograma> findAllByUsuarioOrderByDiaSemanaAscOrdemAsc(Usuario usuario);

    List<Cronograma> findAllByUsuarioAndDiaSemanaOrderByOrdemAsc(Usuario usuario, Integer diaSemana);

    Optional<Cronograma> findByIdCronogramaAndUsuario(UUID idCronograma, Usuario usuario);

    int countByUsuarioAndDiaSemana(Usuario usuario, Integer diaSemana);
}
