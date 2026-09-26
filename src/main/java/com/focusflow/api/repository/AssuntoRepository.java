package com.focusflow.api.repository;

import com.focusflow.api.entity.Assunto;
import com.focusflow.api.entity.Materia;
import com.focusflow.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssuntoRepository extends JpaRepository<Assunto, UUID> {

    List<Assunto> findAllByMateriaOrderByNmAssuntoAsc(Materia materia);

    Optional<Assunto> findByIdAssuntoAndMateriaUsuario(UUID idAssunto, Usuario usuario);

    boolean existsByNmAssuntoIgnoreCaseAndMateria(String nmAssunto, Materia materia);
}
