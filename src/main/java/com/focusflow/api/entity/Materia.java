package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "materias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idMateria")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_materia", updatable = false, nullable = false)
    private UUID idMateria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nm_materia", nullable = false, length = 150)
    private String nmMateria;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Assunto> assuntos = new ArrayList<>();
}
