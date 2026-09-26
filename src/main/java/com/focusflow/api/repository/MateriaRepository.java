package com.focusflow.api.repository;

import com.focusflow.api.entity.Materia;
import com.focusflow.api.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, UUID> {

    List<Materia> findAllByUsuarioOrderByNmMateriaAsc(Usuario usuario);

    Optional<Materia> findByIdMateriaAndUsuario(UUID idMateria, Usuario usuario);

    boolean existsByNmMateriaIgnoreCaseAndUsuario(String nmMateria, Usuario usuario);
}
