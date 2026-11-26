package br.com.etec.justificativa;

import br.com.etec.justificativa.controller.JustificativaController;
import br.com.etec.justificativa.entity.JustificativaAusencia;
import br.com.etec.justificativa.enums.MotivoEnum;
import br.com.etec.justificativa.enums.StatusJustificativaEnum;
import br.com.etec.justificativa.exception.ConflitoException;
import br.com.etec.justificativa.exception.RecursoNaoEncontradoException;
import br.com.etec.justificativa.service.JustificativaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)

public class JustificativaControllerTest {

    @Mock
    JustificativaService service;

    MockMvc mvc;

    static final String JSON_OK = """
            {"matriculaAluno":"2024001","motivo":"SAUDE",
             "dataInicio":"2026-11-30","diasAfastamento":3}""";

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new JustificativaController(service)).build();
    }

    private JustificativaAusencia exemplo() {
        return JustificativaAusencia.builder()
                .id(1L).matriculaAluno("2024001").motivo(MotivoEnum.SAUDE)
                .dataInicio(LocalDate.of(2026, 11, 30)).diasAfastamento(3)
                .dataFim(LocalDate.of(2026, 12, 2))
                .status(StatusJustificativaEnum.EM_ANALISE).build();
    }

    @Test
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(exemplo()));
        mvc.perform(get("/api/justificativas")).andExpect(status().isOk());
    }

    @Test
    void buscar() throws Exception {
        when(service.buscar(1L)).thenReturn(exemplo());
        mvc.perform(get("/api/justificativas/1")).andExpect(status().isOk());
    }

    @Test
    void criar() throws Exception {
        when(service.registrar(any())).thenReturn(exemplo());
        mvc.perform(post("/api/justificativas")
                        .contentType(MediaType.APPLICATION_JSON).content(JSON_OK)).andExpect(status().isCreated());
    }

    @Test
    void mostrar() throws Exception {
        when(service.emitirParecer(eq(1L), eq(StatusJustificativaEnum.ACEITO))).thenReturn(exemplo());
        mvc.perform(patch("/api/justificativas/1/mostrar")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"ACEITO\"}")).andExpect(status().isOk());
    }

    @Test
    void deletar() throws Exception {
        mvc.perform(delete("/api/justificativas/1")).andExpect(status().isNoContent());
    }


    @Test
    void criar_diasZero() throws Exception {
        String json = """
                {"matriculaAluno":"2024001","motivo":"SAUDE",
                 "dataInicio":"2026-11-30","diasAfastamento":0}""";
        mvc.perform(post("/api/justificativas").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void criar_semMatricul() throws Exception {
        String json = """
                {"matriculaAluno":"","motivo":"SAUDE",
                 "dataInicio":"2026-11-30","diasAfastamento":2}""";
        mvc.perform(post("/api/justificativas").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void criar_motivoInvalido() throws Exception {
        String json = """
                {"matriculaAluno":"2024001","motivo":"FERIAS",
                 "dataInicio":"2026-11-30","diasAfastamento":2}""";
        mvc.perform(post("/api/justificativas").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isBadRequest());
    }

    @Test
    void buscar_inexistente() throws Exception {
        when(service.buscar(99L)).thenThrow(new RecursoNaoEncontradoException("não encontrada"));
        mvc.perform(get("/api/justificativas/99")).andExpect(status().isNotFound());
    }

    @Test
    void parecer_Analisada() throws Exception {
        when(service.emitirParecer(eq(1L), eq(StatusJustificativaEnum.RECUSADO)))
                .thenThrow(new ConflitoException("já analisada"));
        mvc.perform(patch("/api/justificativas/1/mostrar").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"RECUSADO\"}")).andExpect(status().isConflict());
    }

    @Test
    void parecer_valorInvalido() throws Exception {
        mvc.perform(patch("/api/justificativas/1/mostrar").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"TALVEZ\"}")).andExpect(status().isBadRequest());
    }
}
