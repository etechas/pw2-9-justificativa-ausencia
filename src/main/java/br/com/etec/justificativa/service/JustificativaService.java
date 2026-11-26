package br.com.etec.justificativa.service;

import br.com.etec.justificativa.dto.JustificativaRequest;
import br.com.etec.justificativa.entity.JustificativaAusencia;
import br.com.etec.justificativa.enums.StatusJustificativaEnum;
import br.com.etec.justificativa.exception.ConflitoException;
import br.com.etec.justificativa.exception.RecursoNaoEncontradoException;
import br.com.etec.justificativa.exception.RegraNegocioException;
import br.com.etec.justificativa.repository.JustificativaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JustificativaService {

    private final JustificativaRepository repository;

    public JustificativaService(JustificativaRepository repository) {
        this.repository = repository;
    }

    public List<JustificativaAusencia> listar() {
        return repository.findAll();
    }

    public JustificativaAusencia buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Justificativa " + id + " não encontrada."));
    }

    // regras do cadastro
    public JustificativaAusencia registrar(JustificativaRequest dados) {
        if (dados.diasAfastamento() == null || dados.diasAfastamento() <= 0) {
            throw new RegraNegocioException("O período de afastamento deve ser de no mínimo 1 dia.");
        }
        JustificativaAusencia j = new JustificativaAusencia();
        preencher(j, dados);
        j.setStatus(StatusJustificativaEnum.EM_ANALISE);
        return repository.save(j);
    }

    public JustificativaAusencia atualizar(Long id, JustificativaRequest dados) {
        JustificativaAusencia j = buscar(id);
        if (j.getStatus() != StatusJustificativaEnum.EM_ANALISE) {
            throw new ConflitoException("Justificativa já analisada não pode ser alterada.");
        }
        preencher(j, dados);
        return repository.save(j);
    }

    public void deletar(Long id) {
        JustificativaAusencia j = buscar(id);
        repository.delete(j);
    }

    // verificaçao de status em analise

    public JustificativaAusencia emitirParecer(Long id, StatusJustificativaEnum novoStatus) {
        if (novoStatus != StatusJustificativaEnum.ACEITO && novoStatus != StatusJustificativaEnum.RECUSADO) {
            throw new RegraNegocioException("O parecer deve ser aceito ou recusado.");
        }
        JustificativaAusencia j = buscar(id);
        if (j.getStatus() != StatusJustificativaEnum.EM_ANALISE) {
            throw new ConflitoException("Esta justificativa já foi analisada (" + j.getStatus() + ").");
        }
        j.setStatus(novoStatus);
        return repository.save(j);
    }

    private void preencher(JustificativaAusencia j, JustificativaRequest d) {
        j.setMatriculaAluno(d.matriculaAluno());
        j.setMotivo(d.motivo());
        j.setDataInicio(d.dataInicio());
        j.setDiasAfastamento(d.diasAfastamento());
        j.setDataFim(d.dataInicio().plusDays(d.diasAfastamento() - 1));
    }
}
