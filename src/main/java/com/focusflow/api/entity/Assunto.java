package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "assuntos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idAssunto")
public class Assunto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_assunto", updatable = false, nullable = false)
    private UUID idAssunto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Column(name = "nm_assunto", nullable = false, length = 150)
    private String nmAssunto;
}
