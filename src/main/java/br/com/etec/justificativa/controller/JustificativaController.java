package br.com.etec.justificativa.controller;

import br.com.etec.justificativa.dto.JustificativaRequest;
import br.com.etec.justificativa.dto.ParecerRequest;
import br.com.etec.justificativa.entity.JustificativaAusencia;
import br.com.etec.justificativa.service.JustificativaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/justificativas")
public class JustificativaController {

    private final JustificativaService service;

    public JustificativaController(JustificativaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<JustificativaAusencia>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JustificativaAusencia> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody JustificativaRequest dados) {
        String erro = validar(dados);
        if (erro != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id,
                                       @RequestBody JustificativaRequest dados) {
        String erro = validar(dados);
        if (erro != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
        }
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/parecer")
    public ResponseEntity<?> parecer(@PathVariable Long id,
                                     @RequestBody ParecerRequest dados) {
        if (dados.status() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O parecer é obrigatório.");
        }
        return ResponseEntity.ok(service.emitirParecer(id, dados.status()));
    }

    private String validar(JustificativaRequest dados) {
        if (dados.matriculaAluno() == null || dados.matriculaAluno().isBlank()) {
            return "A matrícula do aluno é obrigatória.";
        }
        if (dados.matriculaAluno().length() > 20) {
            return "A matrícula pode ter no máximo 20 caracteres.";
        }
        if (dados.motivo() == null) {
            return "O motivo é obrigatório (SAUDE, TRABALHO, LUTO ou OUTRO).";
        }
        if (dados.dataInicio() == null) {
            return "A data de início é obrigatória.";
        }
        if (dados.diasAfastamento() == null) {
            return "Os dias de afastamento são obrigatórios.";
        }
        if (dados.diasAfastamento() < 1) {
            return "O período de afastamento deve ser de no mínimo 1 dia.";
        }
        if (dados.diasAfastamento() > 365) {
            return "O período de afastamento não pode passar de 365 dias.";
        }
        return null;
    }
}
