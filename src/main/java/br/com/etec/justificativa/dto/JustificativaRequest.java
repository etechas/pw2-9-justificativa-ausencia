package br.com.etec.justificativa.dto;

import br.com.etec.justificativa.enums.MotivoEnum;

import java.time.LocalDate;

public record JustificativaRequest(
        String matriculaAluno,

        MotivoEnum motivo,

        LocalDate dataInicio,

        Integer diasAfastamento
) {}
