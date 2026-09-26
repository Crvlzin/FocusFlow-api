package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "revisoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idRevisao")
public class Revisao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_revisao", updatable = false, nullable = false)
    private UUID idRevisao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assunto", nullable = false)
    private Assunto assunto;

    @Column(name = "dt_revisao", nullable = false)
    private LocalDate dtRevisao;

    @Column(name = "nivel_ciclo", nullable = false)
    private int nivelCiclo; // 1 a 4

    @Column(name = "fl_concluida", nullable = false)
    @Builder.Default
    private boolean flConcluida = false;

    @Column(name = "dt_conclusao")
    private LocalDate dtConclusao;

    public boolean isAtrasada() {
        return !this.flConcluida && this.dtRevisao.isBefore(LocalDate.now());
    }
}
