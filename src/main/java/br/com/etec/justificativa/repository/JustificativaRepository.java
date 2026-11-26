package br.com.etec.justificativa.repository;

import br.com.etec.justificativa.entity.JustificativaAusencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JustificativaRepository extends JpaRepository<JustificativaAusencia, Long> {
}
