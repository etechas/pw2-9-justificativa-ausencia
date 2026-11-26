package br.com.etec.justificativa.dto;

import br.com.etec.justificativa.enums.StatusJustificativaEnum;

public record ParecerRequest(
        StatusJustificativaEnum status
) {}
