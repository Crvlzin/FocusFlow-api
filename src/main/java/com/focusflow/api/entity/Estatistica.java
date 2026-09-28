package com.focusflow.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "estatisticas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "idEstatistica")
public class Estatistica {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_estatistica", updatable = false, nullable = false)
    private UUID idEstatistica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_assunto", nullable = false)
    private Assunto assunto;

    @Column(name = "qtd_certas", nullable = false)
    @Builder.Default
    private int qtdCertas = 0;

    @Column(name = "qtd_erradas", nullable = false)
    @Builder.Default
    private int qtdErradas = 0;

    @Column(name = "qtd_minutos", nullable = false)
    @Builder.Default
    private int qtdMinutos = 0;

    @Column(name = "qtd_total", nullable = false)
    @Builder.Default
    private int qtdTotal = 0;

    @Column(name = "dt_registro", nullable = false)
    private OffsetDateTime dtRegistro;

    @PrePersist
    public void prePersist() {
        if (this.dtRegistro == null) {
            this.dtRegistro = OffsetDateTime.now();
        }
    }

    public int getQtdTotalCalculada() {
        return Math.max(this.qtdTotal, this.qtdCertas + this.qtdErradas);
    }

    public double getTaxaAcerto() {
        int total = getQtdTotalCalculada();
        if (total == 0) return 0.0;
        return Math.round(((double) this.qtdCertas / total) * 1000.0) / 10.0; // 1 casa decimal (ex: 85.5)
    }
}
