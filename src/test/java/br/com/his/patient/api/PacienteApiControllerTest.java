package br.com.his.patient.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.AccessContextService;
import br.com.his.access.service.AdminAuthorizationService;
import br.com.his.paciente.model.Paciente;
import br.com.his.paciente.service.PacienteService;
import br.com.his.patient.api.error.ApiExceptionHandler;

@WebMvcTest(
        controllers = PacienteApiController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ClientWebSecurityAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class
        })
@AutoConfigureMockMvc(addFilters = false)
@Import({PacienteApiMapper.class, ApiExceptionHandler.class})
class PacienteApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private UnidadeContext unidadeContext;

    @MockBean
    private UnidadeRepository unidadeRepository;

    @MockBean
    private AccessContextService accessContextService;

    @MockBean
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void deveRetornarPacientePorId() throws Exception {
        Paciente paciente = new Paciente();
        paciente.setId(10L);
        paciente.setNome("MARIA TESTE");
        paciente.setCpf("12345678901");
        paciente.setDataNascimento(LocalDate.of(2000, 1, 1));
        paciente.setAtivo(true);

        when(pacienteService.buscarPorId(10L)).thenReturn(paciente);

        mockMvc.perform(get("/api/pacientes/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nome").value("MARIA TESTE"));
    }

    @Test
    void deveRetornar404QuandoPacienteNaoExiste() throws Exception {
        when(pacienteService.buscarPorId(anyLong()))
                .thenThrow(new IllegalArgumentException("Paciente nao encontrado: 999"));

        mockMvc.perform(get("/api/pacientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar409QuandoMergeInvalido() throws Exception {
        doThrow(new IllegalArgumentException("Merge invalido: paciente de origem inativo ou ja mergeado"))
                .when(pacienteService).mergePaciente(eq(1L), eq(2L), any());

        mockMvc.perform(post("/api/pacientes/merge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromId":1,"toId":2,"motivo":"duplicidade"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void deveRetornar400QuandoPayloadInvalidoNoCreate() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"123"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void deveRetornar409QuandoCpfDuplicadoNoUpdate() throws Exception {
        when(pacienteService.atualizarPaciente(eq(10L), any()))
                .thenThrow(new IllegalArgumentException("CPF ja cadastrado em outro paciente"));

        mockMvc.perform(put("/api/pacientes/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"JOAO","cpf":"52998224725"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
