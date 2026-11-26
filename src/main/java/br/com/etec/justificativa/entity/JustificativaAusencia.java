package br.com.etec.justificativa.entity;

import br.com.etec.justificativa.enums.MotivoEnum;
import br.com.etec.justificativa.enums.StatusJustificativaEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "justificativa_ausencia")
public class JustificativaAusencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "m_aluno", nullable = false)
    private String matriculaAluno;

    @Column(name = "motivo", nullable = false)
    @Enumerated(EnumType.STRING)
    private MotivoEnum motivo;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "dias_afastamento", nullable = false)
    private Integer diasAfastamento;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Column(name = "status_atual", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusJustificativaEnum status;
}
