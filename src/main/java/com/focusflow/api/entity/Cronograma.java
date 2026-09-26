package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cronograma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idCronograma")
public class Cronograma {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cronograma", updatable = false, nullable = false)
    private UUID idCronograma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 0=Domingo, 1=Segunda, 2=Terça, 3=Quarta, 4=Quinta, 5=Sexta, 6=Sábado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia")
    private Materia materia; // Opcional (pode ser null se a matéria for apagada ou não atribuída)

    @Column(name = "titulo_estudo", nullable = false, length = 255)
    private String tituloEstudo;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "fl_concluido", nullable = false)
    @Builder.Default
    private boolean flConcluido = false;

    @Column(name = "ordem", nullable = false)
    @Builder.Default
    private int ordem = 0;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private OffsetDateTime dtCriacao;

    @PrePersist
    public void prePersist() {
        if (this.dtCriacao == null) {
            this.dtCriacao = OffsetDateTime.now();
        }
    }
}
