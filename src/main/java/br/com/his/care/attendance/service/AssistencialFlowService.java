package br.com.his.care.attendance.service;

import br.com.his.care.attendance.api.dto.*;
import br.com.his.care.attendance.dto.*;
import br.com.his.care.attendance.model.*;
import br.com.his.care.attendance.repository.*;
import br.com.his.care.attendance.service.*;
import br.com.his.care.admission.dto.*;
import br.com.his.care.admission.model.*;
import br.com.his.care.admission.repository.*;
import br.com.his.care.triage.dto.*;
import br.com.his.care.triage.model.*;
import br.com.his.care.triage.repository.*;
import br.com.his.care.inpatient.dto.*;
import br.com.his.care.inpatient.model.*;
import br.com.his.care.inpatient.repository.*;
import br.com.his.care.inpatient.service.*;
import br.com.his.care.episode.model.*;
import br.com.his.care.episode.repository.*;
import br.com.his.care.timeline.dto.*;
import br.com.his.care.timeline.model.*;
import br.com.his.care.timeline.repository.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Usuario;
import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.care.admission.dto.EntradaForm;
import br.com.his.care.timeline.dto.TimelinePeriodoItem;
import br.com.his.care.triage.dto.TriagemForm;
import br.com.his.care.attendance.model.Atendimento;
import br.com.his.care.admission.support.ProcedenciaEntradaRules;
import br.com.his.care.timeline.model.AtendimentoEvento;
import br.com.his.care.timeline.model.AtendimentoEventoTipo;
import br.com.his.care.timeline.model.AtendimentoPeriodo;
import br.com.his.care.timeline.model.AtendimentoPeriodoTipo;
import br.com.his.care.inpatient.model.Area;
import br.com.his.care.triage.model.AvcSinalAlerta;
import br.com.his.care.triage.model.AlergiaSeveridade;
import br.com.his.care.triage.model.AlergiaSubstancia;
import br.com.his.care.triage.model.ClassificacaoRisco;
import br.com.his.care.triage.model.ClassificacaoRiscoAlergia;
import br.com.his.care.triage.model.ClassificacaoRiscoAvcSinalAlerta;
import br.com.his.care.triage.model.ClassificacaoReavaliacao;
import br.com.his.care.triage.model.ClassificacaoCor;
import br.com.his.care.triage.model.ClassificacaoGlicemia;
import br.com.his.care.triage.model.ClassificacaoOxigenacao;
import br.com.his.care.triage.model.ClassificacaoPerfusao;
import br.com.his.care.triage.model.ClassificacaoRiscoComorbidade;
import br.com.his.care.triage.model.ClassificacaoGlasgow;
import br.com.his.care.triage.model.ClassificacaoSinaisVitais;
import br.com.his.care.triage.model.ClassificacaoAntropometria;
import br.com.his.care.triage.model.Comorbidade;
import br.com.his.care.episode.model.Episodio;
import br.com.his.care.episode.model.EpisodioStatus;
import br.com.his.care.admission.model.Entrada;
import br.com.his.care.admission.model.FormaChegada;
import br.com.his.care.triage.model.GlasgowAberturaOcular;
import br.com.his.care.triage.model.GlasgowRespostaMotora;
import br.com.his.care.triage.model.GlasgowRespostaPupilar;
import br.com.his.care.triage.model.GlasgowRespostaVerbal;
import br.com.his.care.admission.model.GrauParentesco;
import br.com.his.care.inpatient.model.Internacao;
import br.com.his.care.admission.model.MotivoEntrada;
import br.com.his.care.inpatient.model.Observacao;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import br.com.his.care.triage.model.ReguaDor;
import br.com.his.care.admission.model.SituacaoOcupacional;
import br.com.his.care.attendance.model.StatusAtendimento;
import br.com.his.care.attendance.model.TipoAtendimento;
import br.com.his.care.attendance.model.UnidadeConfigFluxo;
import br.com.his.care.inpatient.repository.AreaRepository;
import br.com.his.care.triage.repository.AvcSinalAlertaRepository;
import br.com.his.care.timeline.repository.AtendimentoEventoRepository;
import br.com.his.care.timeline.repository.AtendimentoPeriodoRepository;
import br.com.his.care.attendance.repository.AtendimentoRepository;
import br.com.his.care.triage.repository.AlergiaSeveridadeRepository;
import br.com.his.care.triage.repository.AlergiaSubstanciaRepository;
import br.com.his.care.triage.repository.ClassificacaoRiscoRepository;
import br.com.his.care.triage.repository.ClassificacaoRiscoAlergiaRepository;
import br.com.his.care.triage.repository.ClassificacaoRiscoAvcSinalAlertaRepository;
import br.com.his.care.triage.repository.ClassificacaoCorRepository;
import br.com.his.care.triage.repository.ClassificacaoRiscoComorbidadeRepository;
import br.com.his.care.triage.repository.ClassificacaoGlasgowRepository;
import br.com.his.care.triage.repository.ClassificacaoReavaliacaoRepository;
import br.com.his.care.triage.repository.ComorbidadeRepository;
import br.com.his.care.admission.repository.EntradaRepository;
import br.com.his.care.episode.repository.EpisodioRepository;
import br.com.his.care.admission.repository.FormaChegadaRepository;
import br.com.his.care.triage.repository.GlasgowAberturaOcularRepository;
import br.com.his.care.triage.repository.GlasgowRespostaMotoraRepository;
import br.com.his.care.triage.repository.GlasgowRespostaPupilarRepository;
import br.com.his.care.triage.repository.GlasgowRespostaVerbalRepository;
import br.com.his.care.admission.repository.GrauParentescoRepository;
import br.com.his.care.inpatient.repository.InternacaoRepository;
import br.com.his.care.admission.repository.MotivoEntradaRepository;
import br.com.his.care.inpatient.repository.ObservacaoRepository;
import br.com.his.care.triage.repository.ReguaDorRepository;
import br.com.his.care.admission.repository.SituacaoOcupacionalRepository;
import br.com.his.care.attendance.repository.StatusAtendimentoRepository;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.triage.repository.UnidadeRegraTriagemRepository;
import br.com.his.reference.location.model.Bairro;
import br.com.his.reference.location.model.Municipio;
import br.com.his.reference.location.repository.BairroRepository;
import br.com.his.reference.location.repository.MunicipioRepository;
import br.com.his.patient.model.lookup.Procedencia;
import br.com.his.patient.model.lookup.Profissao;
import br.com.his.patient.model.lookup.TipoProcedencia;
import br.com.his.patient.repository.ProfissaoRepository;
import br.com.his.patient.repository.ProcedenciaRepository;
import br.com.his.patient.repository.TipoProcedenciaRepository;
import br.com.his.patient.model.Paciente;
import br.com.his.patient.service.PacienteService;

@Service
public class AssistencialFlowService {

    private final AtendimentoRepository atendimentoRepository;
    private final AreaRepository areaRepository;
    private final AvcSinalAlertaRepository avcSinalAlertaRepository;
    private final AlergiaSubstanciaRepository alergiaSubstanciaRepository;
    private final AlergiaSeveridadeRepository alergiaSeveridadeRepository;
    private final ComorbidadeRepository comorbidadeRepository;
    private final ClassificacaoRiscoRepository classificacaoRiscoRepository;
    private final ClassificacaoRiscoAlergiaRepository classificacaoRiscoAlergiaRepository;
    private final ClassificacaoRiscoAvcSinalAlertaRepository classificacaoRiscoAvcSinalAlertaRepository;
    private final ClassificacaoCorRepository classificacaoCorRepository;
    private final ClassificacaoRiscoComorbidadeRepository classificacaoRiscoComorbidadeRepository;
    private final ClassificacaoGlasgowRepository classificacaoGlasgowRepository;
    private final ClassificacaoReavaliacaoRepository classificacaoReavaliacaoRepository;
    private final ReguaDorRepository reguaDorRepository;
    private final GlasgowAberturaOcularRepository glasgowAberturaOcularRepository;
    private final GlasgowRespostaVerbalRepository glasgowRespostaVerbalRepository;
    private final GlasgowRespostaMotoraRepository glasgowRespostaMotoraRepository;
    private final GlasgowRespostaPupilarRepository glasgowRespostaPupilarRepository;
    private final EpisodioRepository episodioRepository;
    private final EntradaRepository entradaRepository;
    private final ObservacaoRepository observacaoRepository;
    private final InternacaoRepository internacaoRepository;
    private final FormaChegadaRepository formaChegadaRepository;
    private final GrauParentescoRepository grauParentescoRepository;
    private final MotivoEntradaRepository motivoEntradaRepository;
    private final SituacaoOcupacionalRepository situacaoOcupacionalRepository;
    private final StatusAtendimentoRepository statusAtendimentoRepository;
    private final ProcedenciaRepository procedenciaRepository;
    private final ProfissaoRepository profissaoRepository;
    private final TipoProcedenciaRepository tipoProcedenciaRepository;
    private final BairroRepository bairroRepository;
    private final MunicipioRepository MunicipioRepository;
    private final AtendimentoPeriodoRepository atendimentoPeriodoRepository;
    private final AtendimentoEventoRepository atendimentoEventoRepository;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final UnidadeRegraTriagemRepository unidadeRegraTriagemRepository;
    private final PacienteService pacienteService;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public AssistencialFlowService(AtendimentoRepository atendimentoRepository,
                                   AreaRepository areaRepository,
                                   AvcSinalAlertaRepository avcSinalAlertaRepository,
                                   AlergiaSubstanciaRepository alergiaSubstanciaRepository,
                                   AlergiaSeveridadeRepository alergiaSeveridadeRepository,
                                   ComorbidadeRepository comorbidadeRepository,
                                   ClassificacaoRiscoRepository classificacaoRiscoRepository,
                                   ClassificacaoRiscoAlergiaRepository classificacaoRiscoAlergiaRepository,
                                   ClassificacaoRiscoAvcSinalAlertaRepository classificacaoRiscoAvcSinalAlertaRepository,
                                   ClassificacaoCorRepository classificacaoCorRepository,
                                   ClassificacaoRiscoComorbidadeRepository classificacaoRiscoComorbidadeRepository,
                                   ClassificacaoGlasgowRepository classificacaoGlasgowRepository,
                                   ClassificacaoReavaliacaoRepository classificacaoReavaliacaoRepository,
                                   ReguaDorRepository reguaDorRepository,
                                   GlasgowAberturaOcularRepository glasgowAberturaOcularRepository,
                                   GlasgowRespostaVerbalRepository glasgowRespostaVerbalRepository,
                                   GlasgowRespostaMotoraRepository glasgowRespostaMotoraRepository,
                                   GlasgowRespostaPupilarRepository glasgowRespostaPupilarRepository,
                                   EpisodioRepository episodioRepository,
                                   EntradaRepository entradaRepository,
                                   ObservacaoRepository observacaoRepository,
                                   InternacaoRepository internacaoRepository,
                                   FormaChegadaRepository formaChegadaRepository,
                                   MotivoEntradaRepository motivoEntradaRepository,
                                   GrauParentescoRepository grauParentescoRepository,
                                   SituacaoOcupacionalRepository situacaoOcupacionalRepository,
                                   StatusAtendimentoRepository statusAtendimentoRepository,
                                   ProcedenciaRepository procedenciaRepository,
                                   ProfissaoRepository profissaoRepository,
                                   TipoProcedenciaRepository tipoProcedenciaRepository,
                                   BairroRepository bairroRepository,
                                   MunicipioRepository MunicipioRepository,
                                   AtendimentoPeriodoRepository atendimentoPeriodoRepository,
                                   AtendimentoEventoRepository atendimentoEventoRepository,
                                   UnidadeRepository unidadeRepository,
                                   UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                   UnidadeRegraTriagemRepository unidadeRegraTriagemRepository,
                                   PacienteService pacienteService,
                                   UsuarioAuditoriaService usuarioAuditoriaService) {
        this.atendimentoRepository = atendimentoRepository;
        this.areaRepository = areaRepository;
        this.avcSinalAlertaRepository = avcSinalAlertaRepository;
        this.alergiaSubstanciaRepository = alergiaSubstanciaRepository;
        this.alergiaSeveridadeRepository = alergiaSeveridadeRepository;
        this.comorbidadeRepository = comorbidadeRepository;
        this.classificacaoRiscoRepository = classificacaoRiscoRepository;
        this.classificacaoRiscoAlergiaRepository = classificacaoRiscoAlergiaRepository;
        this.classificacaoRiscoAvcSinalAlertaRepository = classificacaoRiscoAvcSinalAlertaRepository;
        this.classificacaoCorRepository = classificacaoCorRepository;
        this.classificacaoRiscoComorbidadeRepository = classificacaoRiscoComorbidadeRepository;
        this.classificacaoGlasgowRepository = classificacaoGlasgowRepository;
        this.classificacaoReavaliacaoRepository = classificacaoReavaliacaoRepository;
        this.reguaDorRepository = reguaDorRepository;
        this.glasgowAberturaOcularRepository = glasgowAberturaOcularRepository;
        this.glasgowRespostaVerbalRepository = glasgowRespostaVerbalRepository;
        this.glasgowRespostaMotoraRepository = glasgowRespostaMotoraRepository;
        this.glasgowRespostaPupilarRepository = glasgowRespostaPupilarRepository;
        this.episodioRepository = episodioRepository;
        this.entradaRepository = entradaRepository;
        this.observacaoRepository = observacaoRepository;
        this.internacaoRepository = internacaoRepository;
        this.formaChegadaRepository = formaChegadaRepository;
        this.motivoEntradaRepository = motivoEntradaRepository;
        this.grauParentescoRepository = grauParentescoRepository;
        this.situacaoOcupacionalRepository = situacaoOcupacionalRepository;
        this.statusAtendimentoRepository = statusAtendimentoRepository;
        this.procedenciaRepository = procedenciaRepository;
        this.profissaoRepository = profissaoRepository;
        this.tipoProcedenciaRepository = tipoProcedenciaRepository;
        this.bairroRepository = bairroRepository;
        this.MunicipioRepository = MunicipioRepository;
        this.atendimentoPeriodoRepository = atendimentoPeriodoRepository;
        this.atendimentoEventoRepository = atendimentoEventoRepository;
        this.unidadeRepository = unidadeRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.unidadeRegraTriagemRepository = unidadeRegraTriagemRepository;
        this.pacienteService = pacienteService;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional
    public Atendimento criarAtendimento(Long pacienteId, Long unidadeId, TipoAtendimento tipoAtendimento) {
        return criarAtendimento(pacienteId, unidadeId, tipoAtendimento, null);
    }

    @Transactional
    public Atendimento criarAtendimento(Long pacienteId,
                                        Long unidadeId,
                                        TipoAtendimento tipoAtendimento,
                                        LocalDateTime dataHoraChegada) {
        return criarAtendimento(pacienteId, unidadeId, tipoAtendimento, dataHoraChegada, null);
    }

    @Transactional
    public Atendimento criarAtendimento(Long pacienteId,
                                        Long unidadeId,
                                        TipoAtendimento tipoAtendimento,
                                        LocalDateTime dataHoraChegada,
                                        Episodio episodioExistente) {
        Paciente paciente = resolvePacienteDefinitivo(pacienteService.buscarPorId(pacienteId));
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada: " + unidadeId));
        validarSemAtendimentoAberto(paciente.getId(), unidadeId, episodioExistente);

        UnidadeConfigFluxo config = getConfig(unidadeId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime chegada = dataHoraChegada == null ? now : dataHoraChegada;
        String usuario = currentUsername();
        Usuario usuarioAtual = currentUsuario();
        if (chegada.isAfter(now)) {
            chegada = now;
        }

        Episodio episodio = episodioExistente;
        if (episodio == null) {
            episodio = new Episodio();
            episodio.setPaciente(paciente);
            episodio.setStatus(EpisodioStatus.ABERTO);
            episodio.setDataAbertura(now);
            episodio = episodioRepository.save(episodio);
        }

        Atendimento atendimento = new Atendimento();
        atendimento.setPaciente(paciente);
        atendimento.setUnidade(unidade);
        atendimento.setEpisodio(episodio);
        atendimento.setTipoAtendimento(tipoAtendimento);
        atendimento.setStatus(resolveInitialStatus(config));
        atendimento.setDataHoraChegada(chegada);
        atendimento.setDataCriacao(now);
        atendimento.setUsuarioCriacao(usuario);
        atendimento.setUsuarioCriacaoUsuario(usuarioAtual);
        atendimento = atendimentoRepository.save(atendimento);

        abrirPeriodo(atendimento, AtendimentoPeriodoTipo.CHEGADA, chegada, usuario, false);
        if (config.getPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM) {
            abrirPeriodo(atendimento, AtendimentoPeriodoTipo.AGUARDANDO_TRIAGEM, now, usuario, true);
        } else {
            abrirPeriodo(atendimento, AtendimentoPeriodoTipo.RECEPCAO, now, usuario, true);
        }
        registrarEvento(atendimento, AtendimentoEventoTipo.ATENDIMENTO_CRIADO, null);
        if (episodioExistente == null) {
            registrarEvento(atendimento, AtendimentoEventoTipo.EPISODIO_ABERTO, null);
        }

        return atendimento;
    }

    private void validarSemAtendimentoAberto(Long pacienteId, Long unidadeId, Episodio episodioExistente) {
        if (episodioExistente != null) {
            return;
        }
        atendimentoRepository.findFirstByPacienteIdAndUnidadeIdAndStatusCodigoInOrderByDataHoraChegadaAsc(
                        pacienteId,
                        unidadeId,
                        statusesAbertos())
                .ifPresent(atendimento -> {
                    throw new IllegalArgumentException(
                            "Ja existe atendimento aberto para este paciente na unidade. Atendimento #" + atendimento.getId());
                });
    }

    private Paciente resolvePacienteDefinitivo(Paciente paciente) {
        Paciente atual = paciente;
        while (atual.getMergedInto() != null) {
            atual = atual.getMergedInto();
        }
        return atual;
    }

    @Transactional
    public Atendimento criarAtendimentoComPaciente(Long unidadeId,
                                                   TipoAtendimento tipoAtendimento,
                                                   Long pacienteId,
                                                   boolean criarTemporario,
                                                   String sexoTemporario,
                                                   Integer idadeAparenteTemporario) {
        return criarAtendimentoComPaciente(unidadeId,
                tipoAtendimento,
                pacienteId,
                criarTemporario,
                sexoTemporario,
                idadeAparenteTemporario,
                null);
    }

    @Transactional
    public Atendimento criarAtendimentoComPaciente(Long unidadeId,
                                                   TipoAtendimento tipoAtendimento,
                                                   Long pacienteId,
                                                   boolean criarTemporario,
                                                   String sexoTemporario,
                                                   Integer idadeAparenteTemporario,
                                                   LocalDateTime dataHoraChegada) {
        Long pacienteSelecionadoId = pacienteId;
        if (criarTemporario) {
            Paciente temporario = pacienteService.criarPacienteTemporario(sexoTemporario, idadeAparenteTemporario);
            pacienteSelecionadoId = temporario.getId();
        }
        if (pacienteSelecionadoId == null) {
            throw new IllegalArgumentException("Informe um paciente existente ou marque criar temporario");
        }
        return criarAtendimento(pacienteSelecionadoId, unidadeId, tipoAtendimento, dataHoraChegada);
    }

    @Transactional
    public void iniciarTriagem(Long atendimentoId) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        abrirPeriodo(atendimento, AtendimentoPeriodoTipo.TRIAGEM, LocalDateTime.now(), currentUsername(), true);
        ensureTriagemAberta(atendimento);
        atendimento.setStatus(status("EM_TRIAGEM"));
        atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void finalizarTriagem(Long atendimentoId, TriagemForm form) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        LocalDateTime agora = LocalDateTime.now();
        garantirPeriodoTriagemAberto(atendimento, agora);
        ClassificacaoRisco classificacao = ensureTriagemAberta(atendimento);
        ClassificacaoCor classificacaoCor = classificacaoCorRepository.findById(form.getClassificacaoCorId())
                .orElseThrow(() -> new IllegalArgumentException("Classificacao de risco nao encontrada"));
        classificacao.setClassificacaoCor(classificacaoCor);
        ClassificacaoSinaisVitais sinaisVitais = classificacao.getSinaisVitais();
        if (sinaisVitais == null) {
            sinaisVitais = new ClassificacaoSinaisVitais();
            classificacao.setSinaisVitais(sinaisVitais);
        }
        sinaisVitais.setPressaoArterial(normalize(form.getPressaoArterial()));
        sinaisVitais.setTemperatura(form.getTemperatura());
        sinaisVitais.setFrequenciaCardiaca(form.getFrequenciaCardiaca());
        sinaisVitais.setSaturacaoO2(form.getSaturacaoO2());
        sinaisVitais.setFrequenciaRespiratoria(form.getFrequenciaRespiratoria());

        ClassificacaoOxigenacao oxigenacao = classificacao.getOxigenacao();
        if (oxigenacao == null) {
            oxigenacao = new ClassificacaoOxigenacao();
            classificacao.setOxigenacao(oxigenacao);
        }
        oxigenacao.setSaturacaoO2ComTerapiaO2(form.getSaturacaoO2ComTerapiaO2());
        oxigenacao.setSaturacaoO2Aa(form.getSaturacaoO2Aa());

        ClassificacaoGlicemia glicemia = classificacao.getGlicemia();
        if (glicemia == null) {
            glicemia = new ClassificacaoGlicemia();
            classificacao.setGlicemia(glicemia);
        }
        glicemia.setGlicemiaCapilar(form.getGlicemiaCapilar());
        glicemia.setHgt(form.getHgt());

        ClassificacaoAntropometria antropometria = classificacao.getAntropometria();
        if (antropometria == null) {
            antropometria = new ClassificacaoAntropometria();
            classificacao.setAntropometria(antropometria);
        }
        antropometria.setPesoKg(form.getPesoKg());
        antropometria.setAlturaCm(form.getAlturaCm());

        ClassificacaoPerfusao perfusao = classificacao.getPerfusao();
        if (perfusao == null) {
            perfusao = new ClassificacaoPerfusao();
            classificacao.setPerfusao(perfusao);
        }
        perfusao.setPerfusaoCapilarPerifericaSeg(form.getPerfusaoCapilarPerifericaSeg());
        perfusao.setPreenchimentoCapilarCentralSeg(form.getPreenchimentoCapilarCentralSeg());
        classificacao.setReguaDor(resolveReguaDor(form.getReguaDorId()));
        classificacao.setMedicacoesUsoContinuo(normalize(form.getMedicacoesUsoContinuo()));
        classificacao.setDiscriminador(normalize(form.getDiscriminador()));
        classificacao.setObservacao(normalize(form.getObservacao()));
        classificacao.setQueixaPrincipal(normalize(form.getQueixaPrincipal()));
        classificacao.setDataFim(agora);
        classificacaoRiscoRepository.save(classificacao);
        salvarAlergiaEComorbidadesDaClassificacao(classificacao, form);
        salvarAvcSinaisAlertaDaClassificacao(classificacao, form);
        salvarGlasgowDaClassificacao(classificacao, form);

        fecharPeriodoAberto(atendimentoId, AtendimentoPeriodoTipo.TRIAGEM, currentUsername(), agora);
        UnidadeConfigFluxo config = getConfig(atendimento.getUnidade().getId());
        AtendimentoPeriodoTipo proximoPeriodo = config.getPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM
                ? AtendimentoPeriodoTipo.AGUARDANDO_RECEPCAO
                : AtendimentoPeriodoTipo.AGUARDANDO_MEDICO;
        StatusAtendimento proximoStatus = proximoPeriodo == AtendimentoPeriodoTipo.AGUARDANDO_RECEPCAO
                ? status("AGUARDANDO_RECEPCAO")
                : status("AGUARDANDO_MEDICO");
        abrirPeriodo(atendimento, proximoPeriodo, agora, currentUsername(), true);
        atendimento.setStatus(proximoStatus);
        atendimentoRepository.save(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.TRIAGEM_FINALIZADA, null);
    }

    private void garantirPeriodoTriagemAberto(Atendimento atendimento, LocalDateTime inicioTriagem) {
        boolean triagemJaAberta = atendimentoPeriodoRepository
                .findFirstByAtendimentoIdAndTipoAndFimEmIsNull(atendimento.getId(), AtendimentoPeriodoTipo.TRIAGEM)
                .isPresent();
        if (triagemJaAberta) {
            return;
        }
        String usuario = currentUsername();
        fecharPeriodoAberto(atendimento.getId(), AtendimentoPeriodoTipo.AGUARDANDO_TRIAGEM, usuario, inicioTriagem);
        abrirPeriodo(atendimento, AtendimentoPeriodoTipo.TRIAGEM, inicioTriagem, usuario, false);
        atendimento.setStatus(status("EM_TRIAGEM"));
        atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void registrarReclassificacao(Long atendimentoId, TriagemForm form) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        ClassificacaoRisco classificacaoBase = classificacaoRiscoRepository
                .findTopByAtendimentoIdAndDataFimIsNotNullOrderByDataFimDesc(atendimentoId)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento sem classificacao inicial finalizada"));
        ClassificacaoCor classificacaoCor = classificacaoCorRepository.findById(form.getClassificacaoCorId())
                .orElseThrow(() -> new IllegalArgumentException("Classificacao de risco nao encontrada"));

        ClassificacaoReavaliacao reavaliacao = new ClassificacaoReavaliacao();
        reavaliacao.setClassificacaoRisco(classificacaoBase);
        reavaliacao.setClassificacaoCor(classificacaoCor);
        reavaliacao.setSinaisVitais(buildSinaisVitaisSeInformado(form));
        reavaliacao.setOxigenacao(buildOxigenacaoSeInformado(form));
        reavaliacao.setGlicemia(buildGlicemiaSeInformado(form));
        reavaliacao.setAntropometria(buildAntropometriaSeInformado(form));
        reavaliacao.setPerfusao(buildPerfusaoSeInformado(form));
        reavaliacao.setReguaDor(resolveReguaDor(form.getReguaDorId()));
        reavaliacao.setDiscriminador(normalize(form.getDiscriminador()));
        reavaliacao.setObservacao(normalize(form.getObservacao()));
        reavaliacao.setDataHora(LocalDateTime.now());
        reavaliacao.setUsuario(currentUsername());
        reavaliacao.setUsuarioRegistro(currentUsuario());
        preencherGlasgowDaReavaliacao(reavaliacao, form);
        classificacaoReavaliacaoRepository.save(reavaliacao);
        registrarEvento(atendimento, AtendimentoEventoTipo.TRIAGEM_FINALIZADA, "{\"tipo\":\"RECLASSIFICACAO\"}");
    }

    @Transactional
    public Episodio abrirEpisodio(Long atendimentoId) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        if (atendimento.getEpisodio() != null) {
            return atendimento.getEpisodio();
        }

        Episodio episodio = new Episodio();
        episodio.setPaciente(atendimento.getPaciente());
        episodio.setStatus(EpisodioStatus.ABERTO);
        episodio.setDataAbertura(LocalDateTime.now());
        episodio = episodioRepository.save(episodio);
        atendimento.setEpisodio(episodio);
        atendimentoRepository.save(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.EPISODIO_ABERTO, null);

        return episodio;
    }

    @Transactional
    public Entrada registrarEntradaPorAtendimento(Long atendimentoId, EntradaForm form) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (atendimento.getEpisodio() == null) {
            throw new IllegalArgumentException("Atendimento sem episodio associado");
        }
        return registrarEntrada(atendimento, form);
    }

    @Transactional
    public Entrada registrarEntrada(Long atendimentoId, EntradaForm form) {
        return registrarEntrada(buscarAtendimento(atendimentoId), form);
    }

    private Entrada registrarEntrada(Atendimento atendimento, EntradaForm form) {
        Entrada entrada = entradaRepository.findByAtendimentoId(atendimento.getId()).orElseGet(Entrada::new);
        Area area = areaRepository.findById(form.getAreaId())
                .orElseThrow(() -> new IllegalArgumentException("Area da entrada nao encontrada"));
        FormaChegada formaChegada = formaChegadaRepository.findById(form.getFormaChegadaId())
                .orElseThrow(() -> new IllegalArgumentException("Forma de chegada nao encontrada"));
        MotivoEntrada motivoEntrada = form.getMotivoEntradaId() == null
                ? null
                : motivoEntradaRepository.findById(form.getMotivoEntradaId())
                        .orElseThrow(() -> new IllegalArgumentException("Motivo da entrada nao encontrado"));
        GrauParentesco grauParentesco = form.getGrauParentescoId() == null
                ? null
                : grauParentescoRepository.findById(form.getGrauParentescoId())
                        .orElseThrow(() -> new IllegalArgumentException("Grau de parentesco nao encontrado"));
        TipoProcedencia tipoProcedencia = tipoProcedenciaRepository.findById(form.getTipoProcedenciaId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de procedencia nao encontrado"));
        ProcedenciaEntradaRules.TipoCampo tipoCampoProcedencia = ProcedenciaEntradaRules.resolve(tipoProcedencia);
        ProcedenciaEntradaRules.clearIrrelevantFields(form, tipoCampoProcedencia);
        Procedencia procedencia = null;
        Bairro bairro = null;
        Municipio Municipio = null;
        SituacaoOcupacional situacaoOcupacional = form.getSituacaoOcupacionalId() == null
                ? null
                : situacaoOcupacionalRepository.findById(form.getSituacaoOcupacionalId())
                        .orElseThrow(() -> new IllegalArgumentException("Situacao ocupacional nao encontrada"));
        Profissao profissao = form.getProfissaoId() == null
                ? null
                : profissaoRepository.findById(form.getProfissaoId())
                        .orElseThrow(() -> new IllegalArgumentException("Profissao nao encontrada"));
        if (!area.isAtivo() || !area.getUnidade().getId().equals(atendimento.getUnidade().getId())) {
            throw new IllegalArgumentException("Area da entrada invalida para a unidade atual");
        }
        if (!formaChegada.isAtivo()) {
            throw new IllegalArgumentException("Forma de chegada inativa");
        }
        if (motivoEntrada != null && !motivoEntrada.isAtivo()) {
            throw new IllegalArgumentException("Motivo da entrada inativo");
        }
        if (grauParentesco != null && !grauParentesco.isAtivo()) {
            throw new IllegalArgumentException("Grau de parentesco inativo");
        }
        if (situacaoOcupacional != null && !situacaoOcupacional.isAtivo()) {
            throw new IllegalArgumentException("Situacao ocupacional inativa");
        }
        if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.BAIRRO) {
            if (form.getProcedenciaBairroId() == null) {
                throw new IllegalArgumentException("Bairro obrigatorio para o tipo de procedencia BAIRRO");
            }
            bairro = bairroRepository.findById(form.getProcedenciaBairroId())
                    .orElseThrow(() -> new IllegalArgumentException("Bairro nao encontrado"));
            if (atendimento.getUnidade().getMunicipio() == null || bairro.getMunicipio() == null
                    || !bairro.getMunicipio().getId().equals(atendimento.getUnidade().getMunicipio().getId())) {
                throw new IllegalArgumentException("Bairro invalido para a Municipio da unidade atual");
            }
            procedencia = resolverOuCriarProcedenciaBairro(tipoProcedencia, bairro, atendimento.getUnidade());
        } else if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.MUNICIPIO) {
            if (form.getProcedenciaMunicipioId() == null) {
                throw new IllegalArgumentException("Municipio obrigatoria para o tipo de procedencia Municipio");
            }
            Municipio = MunicipioRepository.findById(form.getProcedenciaMunicipioId())
                    .orElseThrow(() -> new IllegalArgumentException("Municipio nao encontrada"));
            procedencia = resolverOuCriarProcedenciaMunicipio(tipoProcedencia, Municipio, atendimento.getUnidade());
        } else if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.OUTROS) {
            String descricaoOutros = normalize(form.getProcedenciaObservacao());
            if (descricaoOutros == null) {
                throw new IllegalArgumentException("Descricao da procedencia e obrigatoria para o tipo OUTROS");
            }
            procedencia = resolverOuCriarProcedenciaDescricao(tipoProcedencia, descricaoOutros, atendimento.getUnidade());
        } else if (tipoCampoProcedencia == ProcedenciaEntradaRules.TipoCampo.CATALOGO) {
            if (form.getProcedenciaId() == null) {
                throw new IllegalArgumentException("Procedencia obrigatoria para o tipo selecionado");
            }
            procedencia = procedenciaRepository.findById(form.getProcedenciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Procedencia nao encontrada"));
            if (procedencia.getTipoProcedencia() == null
                    || !procedencia.getTipoProcedencia().getId().equals(tipoProcedencia.getId())) {
                throw new IllegalArgumentException("Procedencia nao corresponde ao tipo selecionado");
            }
            if (procedencia.getUnidade() != null
                    && !procedencia.getUnidade().getId().equals(atendimento.getUnidade().getId())) {
                throw new IllegalArgumentException("Procedencia invalida para a unidade atual");
            }
        }
        entrada.setAtendimento(atendimento);
        entrada.setArea(area);
        entrada.setFormaChegada(formaChegada);
        entrada.setProcedencia(procedencia);
        entrada.setMotivoEntrada(motivoEntrada);
        entrada.setGrauParentesco(grauParentesco);
        entrada.setSituacaoOcupacional(situacaoOcupacional);
        entrada.setProfissao(profissao);
        entrada.setDataHoraEntrada(
                entrada.getDataHoraEntrada() == null ? LocalDateTime.now() : entrada.getDataHoraEntrada());
        entrada.setTelefoneComunicante(normalize(form.getTelefoneComunicante()));
        entrada.setComunicante(normalize(form.getComunicante()));
        entrada.setInformacaoAdChegada(normalize(form.getInformacaoAdChegada()));
        entrada.setProcedenciaObservacao(normalize(form.getProcedenciaObservacao()));
        entrada.setProfissaoObservacao(normalize(form.getProfissaoObservacao()));
        entrada.setTempoServico(normalize(form.getTempoServico()));
        entrada.setObservacoes(normalize(form.getObservacoes()));
        entrada.setConvenio(normalize(form.getConvenio()));
        entrada.setGuia(normalize(form.getGuia()));
        entrada.setAtualizadoEm(LocalDateTime.now());
        entrada.setAtualizadoPor(currentUsername());
        entrada.setAtualizadoPorUsuario(currentUsuario());
        entrada = entradaRepository.save(entrada);

        avancarFluxoAposEntrada(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.ENTRADA_REGISTRADA, null);
        return entrada;
    }

    @Transactional
    public void finalizarAtendimento(Long atendimentoId, boolean evadiu) {
        finalizarAtendimentoComStatus(
                atendimentoId,
                evadiu ? "EVADIU" : "FINALIZADO",
                evadiu ? EpisodioStatus.CANCELADO : EpisodioStatus.ALTA,
                evadiu ? "{\"motivo\":\"evadiu\"}" : null);
    }

    @Transactional
    private void finalizarAtendimentoComStatus(Long atendimentoId,
                                               String statusCodigo,
                                               EpisodioStatus episodioStatus,
                                               String metadataEvento) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        fecharTodosPeriodosAbertos(atendimentoId, currentUsername());

        buscarEpisodioPorAtendimento(atendimentoId).ifPresent(episodio -> {
            if (episodio.getDataFechamento() == null) {
                episodio.setDataFechamento(LocalDateTime.now());
            }
            if (episodio.getStatus() == EpisodioStatus.ABERTO || episodio.getStatus() == EpisodioStatus.OBSERVACAO) {
                episodio.setStatus(episodioStatus);
            }
            episodioRepository.save(episodio);
        });

        atendimento.setStatus(status(statusCodigo));
        atendimentoRepository.save(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.FINALIZADO, metadataEvento);
    }

    @Transactional
    public void aplicarDesfecho(Long atendimentoId, String motivoDesfechoDescricao) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }

        String motivo = normalize(motivoDesfechoDescricao);
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo de desfecho invalido");
        }

        if ("TRANSFERENCIA".equalsIgnoreCase(motivo)) {
            marcarAtendimentoTransferido(atendimentoId, "{\"origem\":\"desfecho\"}");
            return;
        }

        if ("EVASAO".equalsIgnoreCase(motivo)) {
            finalizarAtendimentoComStatus(
                    atendimentoId,
                    "EVADIU",
                    EpisodioStatus.CANCELADO,
                    "{\"motivo\":\"EVASAO\",\"origem\":\"desfecho\"}");
            return;
        }

        if ("ABANDONO".equalsIgnoreCase(motivo)) {
            finalizarAtendimentoComStatus(
                    atendimentoId,
                    "ABANDONO",
                    EpisodioStatus.CANCELADO,
                    "{\"motivo\":\"ABANDONO\",\"origem\":\"desfecho\"}");
            return;
        }

        fecharTodosPeriodosAbertos(atendimentoId, currentUsername());

        buscarEpisodioPorAtendimento(atendimentoId).ifPresent(episodio -> {
            if (episodio.getDataFechamento() == null) {
                episodio.setDataFechamento(LocalDateTime.now());
            }
            episodio.setStatus("OBITO".equalsIgnoreCase(motivo) ? EpisodioStatus.OBITO : EpisodioStatus.ALTA);
            episodioRepository.save(episodio);
        });

        atendimento.setStatus(status("FINALIZADO"));
        atendimentoRepository.save(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.FINALIZADO,
                "{\"motivo\":\"" + motivo + "\",\"origem\":\"desfecho\"}");
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarFilaHoje(Long unidadeId) {
        LocalDate hoje = LocalDate.now();
        return atendimentoRepository.findByUnidadeIdAndDataHoraChegadaBetweenOrderByDataHoraChegadaDesc(
                unidadeId, hoje.atStartOfDay(), hoje.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarPorPeriodo(Long unidadeId, LocalDate dataInicio, LocalDate dataFim) {
        LocalDate inicio = dataInicio == null ? LocalDate.now() : dataInicio;
        LocalDate fim = dataFim == null ? inicio : dataFim;
        if (fim.isBefore(inicio)) {
            LocalDate tmp = inicio;
            inicio = fim;
            fim = tmp;
        }
        return atendimentoRepository.findByUnidadeIdAndDataHoraChegadaBetweenOrderByDataHoraChegadaDesc(
                unidadeId,
                inicio.atStartOfDay(),
                fim.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarNaoIdentificadosEmAberto(Long unidadeId) {
        return atendimentoRepository.findNaoIdentificadosEmAberto(unidadeId, statusesAbertos());
    }

    @Transactional(readOnly = true)
    public List<Atendimento> listarFilaClassificacao(Long unidadeId) {
        return atendimentoRepository.findFilaClassificacao(unidadeId, statusesAbertos());
    }

    @Transactional(readOnly = true)
    public Map<Long, ClassificacaoRisco> mapaUltimaClassificacao(List<Long> atendimentoIds) {
        if (atendimentoIds == null || atendimentoIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, ClassificacaoRisco> result = new LinkedHashMap<>();
        for (ClassificacaoRisco classificacao : classificacaoRiscoRepository.findByAtendimentoIdInOrderByDataInicioDesc(atendimentoIds)) {
            result.putIfAbsent(classificacao.getAtendimento().getId(), classificacao);
        }
        Map<Long, ClassificacaoReavaliacao> reavaliacoes = mapaUltimaReavaliacao(atendimentoIds);
        for (Map.Entry<Long, ClassificacaoReavaliacao> entry : reavaliacoes.entrySet()) {
            ClassificacaoRisco base = result.get(entry.getKey());
            if (base == null) {
                continue;
            }
            ClassificacaoRisco display = new ClassificacaoRisco();
            display.setId(base.getId());
            display.setAtendimento(base.getAtendimento());
            display.setClassificacaoCor(entry.getValue().getClassificacaoCor() != null
                    ? entry.getValue().getClassificacaoCor()
                    : base.getClassificacaoCor());
            display.setQueixaPrincipal(base.getQueixaPrincipal());
            display.setDiscriminador(entry.getValue().getDiscriminador() != null
                    ? entry.getValue().getDiscriminador()
                    : base.getDiscriminador());
            display.setObservacao(entry.getValue().getObservacao() != null
                    ? entry.getValue().getObservacao()
                    : base.getObservacao());
            display.setDataInicio(base.getDataInicio());
            display.setDataFim(base.getDataFim());
            result.put(entry.getKey(), display);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Optional<ClassificacaoRisco> buscarUltimaClassificacaoFinalizada(Long atendimentoId) {
        return classificacaoRiscoRepository.findTopByAtendimentoIdAndDataFimIsNotNullOrderByDataFimDesc(atendimentoId);
    }

    @Transactional(readOnly = true)
    public Atendimento buscarAtendimento(Long atendimentoId) {
        return atendimentoRepository.findById(atendimentoId)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento nao encontrado: " + atendimentoId));
    }

    @Transactional(readOnly = true)
    public Optional<ClassificacaoRisco> buscarTriagemAberta(Long atendimentoId) {
        return classificacaoRiscoRepository.findFirstByAtendimentoIdAndDataFimIsNull(atendimentoId);
    }

    @Transactional(readOnly = true)
    public Optional<Episodio> buscarEpisodioPorAtendimento(Long atendimentoId) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        return Optional.ofNullable(atendimento.getEpisodio());
    }

    @Transactional
    public Atendimento criarAtendimentoTransferencia(Episodio episodio,
                                                     Long unidadeDestinoId,
                                                     TipoAtendimento tipoAtendimento,
                                                     LocalDateTime dataHoraChegada) {
        if (episodio == null) {
            throw new IllegalArgumentException("Episodio obrigatorio para transferencia");
        }
        Atendimento atendimento = criarAtendimento(
                episodio.getPaciente().getId(),
                unidadeDestinoId,
                tipoAtendimento,
                dataHoraChegada,
                episodio);
        registrarEvento(atendimento, AtendimentoEventoTipo.TRANSFERENCIA_RECEBIDA, null);
        return atendimento;
    }

    @Transactional
    public void marcarAtendimentoTransferido(Long atendimentoId, String metadata) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        if (isEncerrado(atendimento)) {
            throw new IllegalArgumentException("Atendimento ja encerrado");
        }
        fecharTodosPeriodosAbertos(atendimentoId, currentUsername());
        atendimento.setStatus(status("TRANSFERIDO"));
        atendimentoRepository.save(atendimento);
        registrarEvento(atendimento, AtendimentoEventoTipo.TRANSFERENCIA_SAIDA, metadata);
    }

    @Transactional
    public void registrarEventoTransferenciaSolicitada(Long atendimentoId, String metadata) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        registrarEvento(atendimento, AtendimentoEventoTipo.TRANSFERENCIA_SOLICITADA, metadata);
    }

    @Transactional(readOnly = true)
    public Optional<Entrada> buscarEntradaPorAtendimento(Long atendimentoId) {
        return entradaRepository.findByAtendimentoId(atendimentoId);
    }

    @Transactional(readOnly = true)
    public boolean podeAbrirEpisodio(Long atendimentoId) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        return !isEncerrado(atendimento) && atendimento.getEpisodio() != null;
    }

    @Transactional(readOnly = true)
    public List<TimelinePeriodoItem> timeline(Long atendimentoId) {
        Atendimento atendimento = buscarAtendimento(atendimentoId);
        UnidadeConfigFluxo config = getConfig(atendimento.getUnidade().getId());
        List<AtendimentoPeriodo> periodos = atendimentoPeriodoRepository.findByAtendimentoIdOrderByInicioEmAsc(atendimentoId);
        LocalDateTime now = LocalDateTime.now();
        List<AtendimentoPeriodo> ordenados = periodos.stream()
                .sorted(Comparator.comparing(AtendimentoPeriodo::getInicioEm))
                .toList();

        AtendimentoPeriodo chegada = ordenados.stream()
                .filter(p -> p.getTipo() == AtendimentoPeriodoTipo.CHEGADA)
                .findFirst()
                .orElse(null);

        boolean possuiTriagem = ordenados.stream().anyMatch(p -> p.getTipo() == AtendimentoPeriodoTipo.TRIAGEM);

        List<TimelinePeriodoItem> itens = new ArrayList<>(ordenados.stream()
                .filter(p -> p.getTipo() != AtendimentoPeriodoTipo.CHEGADA)
                .filter(p -> !ocultarPeriodoTecnicoNaTimeline(p, config, possuiTriagem))
                .map(p -> toTimelineItem(p, chegada, config, now))
                .toList());

        observacaoRepository.findByAtendimentoId(atendimentoId)
                .map(observacao -> toTimelineObservacao(observacao, now))
                .ifPresent(itens::add);
        internacaoRepository.findByAtendimentoId(atendimentoId)
                .map(internacao -> toTimelineInternacao(internacao, now))
                .ifPresent(itens::add);
        classificacaoReavaliacaoRepository.findByAtendimentoIdsOrderByDataHoraDesc(List.of(atendimentoId))
                .stream()
                .map(this::toTimelineReclassificacao)
                .forEach(itens::add);

        return itens.stream()
                .filter(item -> item.inicioEm() != null)
                .sorted(Comparator.comparing(TimelinePeriodoItem::inicioEm))
                .toList();
    }

    private TimelinePeriodoItem toTimelineObservacao(Observacao observacao, LocalDateTime now) {
        LocalDateTime inicio = observacao.getDataHoraInicio();
        LocalDateTime fim = observacao.getDataHoraFim();
        String label = observacao.getDataHoraCancelamento() != null ? "OBSERVACAO (CANCELADA)" : "OBSERVACAO";
        String usuarioFim = observacao.getDataHoraCancelamento() != null
                ? firstNonBlank(observacao.getCanceladoPor(), usernameOrNull(observacao.getCanceladoPorUsuario()))
                : null;
        return new TimelinePeriodoItem(
                null,
                label,
                inicio,
                fim,
                durationMinutes(inicio, fim == null ? now : fim),
                "-",
                usuarioFim);
    }

    private TimelinePeriodoItem toTimelineInternacao(Internacao internacao, LocalDateTime now) {
        LocalDateTime inicio = internacao.getDataHoraInicioInternacao() != null
                ? internacao.getDataHoraInicioInternacao()
                : internacao.getDataHoraDecisaoInternacao();
        LocalDateTime fim = internacao.getDataHoraFimInternacao();
        String label = internacao.getDataHoraCancelamento() != null ? "INTERNACAO (CANCELADA)" : "INTERNACAO";
        String usuarioFim = internacao.getDataHoraCancelamento() != null
                ? firstNonBlank(internacao.getCanceladoPor(), usernameOrNull(internacao.getCanceladoPorUsuario()))
                : null;
        return new TimelinePeriodoItem(
                null,
                label,
                inicio,
                fim,
                durationMinutes(inicio, fim == null ? now : fim),
                "-",
                usuarioFim);
    }

    private TimelinePeriodoItem toTimelineReclassificacao(ClassificacaoReavaliacao reavaliacao) {
        LocalDateTime dataHora = reavaliacao.getDataHora();
        String label = "CLASSIFICACAO";
        String usuario = firstNonBlank(reavaliacao.getUsuario(), usernameOrNull(reavaliacao.getUsuarioRegistro()));
        return new TimelinePeriodoItem(
                null,
                label,
                dataHora,
                dataHora,
                0L,
                usuario,
                usuario);
    }

    private long durationMinutes(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            return 0L;
        }
        return Math.max(0L, Duration.between(inicio, fim).toMinutes());
    }

    private ClassificacaoRisco ensureTriagemAberta(Atendimento atendimento) {
        return classificacaoRiscoRepository.findFirstByAtendimentoIdAndDataFimIsNull(atendimento.getId())
                .orElseGet(() -> {
                    ClassificacaoRisco classificacao = new ClassificacaoRisco();
                    classificacao.setAtendimento(atendimento);
                    classificacao.setDataInicio(LocalDateTime.now());
                    return classificacaoRiscoRepository.save(classificacao);
                });
    }

    private UnidadeConfigFluxo getConfig(Long unidadeId) {
        return unidadeConfigFluxoRepository.findById(unidadeId)
                .orElseGet(() -> {
                    UnidadeConfigFluxo config = new UnidadeConfigFluxo();
                    config.setUnidadeId(unidadeId);
                    config.setPrimeiroPasso(PrimeiroPassoFluxo.RECEPCAO);
                    config.setExigeFichaParaMedico(false);
                    config.setCriaEpisodioAutomatico(false);
                    return config;
                });
    }

    private boolean isTriagemObrigatoria(Long unidadeId, TipoAtendimento tipoAtendimento) {
        return unidadeRegraTriagemRepository.findByUnidadeIdAndTipoAtendimento(unidadeId, tipoAtendimento)
                .map(r -> r.isTriagemObrigatoria())
                .orElse(false);
    }

    private StatusAtendimento resolveInitialStatus(UnidadeConfigFluxo config) {
        return config.getPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM
                ? status("AGUARDANDO_TRIAGEM")
                : status("AGUARDANDO_RECEPCAO");
    }

    private void avancarFluxoAposEntrada(Atendimento atendimento) {
        AtendimentoPeriodoTipo periodoAtual = atendimentoPeriodoRepository
                .findFirstByAtendimentoIdAndTipoAndFimEmIsNull(atendimento.getId(), AtendimentoPeriodoTipo.RECEPCAO)
                .map(periodo -> AtendimentoPeriodoTipo.RECEPCAO)
                .orElseGet(() -> atendimentoPeriodoRepository
                        .findFirstByAtendimentoIdAndTipoAndFimEmIsNull(atendimento.getId(), AtendimentoPeriodoTipo.AGUARDANDO_RECEPCAO)
                        .map(periodo -> AtendimentoPeriodoTipo.AGUARDANDO_RECEPCAO)
                        .orElse(null));
        if (periodoAtual == null) {
            return;
        }

        fecharPeriodoAberto(atendimento.getId(), periodoAtual, currentUsername());

        AtendimentoPeriodoTipo proximoPeriodo;
        StatusAtendimento proximoStatus;
        if (periodoAtual == AtendimentoPeriodoTipo.RECEPCAO) {
            proximoPeriodo = isTriagemObrigatoria(atendimento.getUnidade().getId(), atendimento.getTipoAtendimento())
                    ? AtendimentoPeriodoTipo.AGUARDANDO_TRIAGEM
                    : AtendimentoPeriodoTipo.AGUARDANDO_MEDICO;
            proximoStatus = proximoPeriodo == AtendimentoPeriodoTipo.AGUARDANDO_TRIAGEM
                    ? status("AGUARDANDO_TRIAGEM")
                    : status("AGUARDANDO_MEDICO");
        } else {
            proximoPeriodo = AtendimentoPeriodoTipo.AGUARDANDO_MEDICO;
            proximoStatus = status("AGUARDANDO_MEDICO");
        }

        abrirPeriodo(atendimento, proximoPeriodo, LocalDateTime.now(), currentUsername(), false);
        atendimento.setStatus(proximoStatus);
        atendimentoRepository.save(atendimento);
    }

    private TimelinePeriodoItem toTimelineItem(AtendimentoPeriodo periodo,
                                               AtendimentoPeriodo chegada,
                                               UnidadeConfigFluxo config,
                                               LocalDateTime now) {
        LocalDateTime inicio = periodo.getInicioEm();
        String usuarioInicio = periodo.getUsuarioInicio();
        if (chegada != null && isPrimeiraEtapaOperacional(config, periodo.getTipo())) {
            inicio = chegada.getInicioEm();
            usuarioInicio = chegada.getUsuarioInicio();
        }
        LocalDateTime fim = periodo.getFimEm() == null ? now : periodo.getFimEm();
        long minutos = Math.max(0, Duration.between(inicio, fim).toMinutes());
        return new TimelinePeriodoItem(
                periodo.getTipo(),
                timelineLabel(periodo.getTipo()),
                inicio,
                periodo.getFimEm(),
                minutos,
                usuarioInicio,
                periodo.getUsuarioFim());
    }

    private boolean isPrimeiraEtapaOperacional(UnidadeConfigFluxo config, AtendimentoPeriodoTipo tipo) {
        return (config.getPrimeiroPasso() == PrimeiroPassoFluxo.RECEPCAO && tipo == AtendimentoPeriodoTipo.RECEPCAO)
                || (config.getPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM && tipo == AtendimentoPeriodoTipo.TRIAGEM);
    }

    private boolean ocultarPeriodoTecnicoNaTimeline(AtendimentoPeriodo periodo,
                                                    UnidadeConfigFluxo config,
                                                    boolean possuiTriagem) {
        if (config.getPrimeiroPasso() != PrimeiroPassoFluxo.TRIAGEM) {
            return false;
        }
        if (periodo.getTipo() != AtendimentoPeriodoTipo.AGUARDANDO_TRIAGEM) {
            return false;
        }
        return possuiTriagem;
    }

    private String timelineLabel(AtendimentoPeriodoTipo tipo) {
        return switch (tipo) {
            case AGUARDANDO_TRIAGEM -> "AGUARDANDO CLASSIFICACAO";
            case TRIAGEM -> "CLASSIFICACAO";
            default -> tipo.name().replace('_', ' ');
        };
    }

    private void abrirPeriodo(Atendimento atendimento,
                              AtendimentoPeriodoTipo tipo,
                              LocalDateTime inicioEm,
                              String usuario,
                              boolean autoCloseOpened) {
        LocalDateTime inicio = inicioEm == null ? LocalDateTime.now() : inicioEm;
        if (autoCloseOpened) {
            fecharTodosPeriodosAbertos(atendimento.getId(), usuario, inicio);
        }
        Optional<AtendimentoPeriodo> abertoMesmoTipo = atendimentoPeriodoRepository
                .findFirstByAtendimentoIdAndTipoAndFimEmIsNull(atendimento.getId(), tipo);
        if (abertoMesmoTipo.isPresent()) {
            return;
        }

        AtendimentoPeriodo periodo = new AtendimentoPeriodo();
        periodo.setAtendimento(atendimento);
        periodo.setTipo(tipo);
        periodo.setInicioEm(inicio);
        periodo.setUsuarioInicio(usuario);
        periodo.setUsuarioInicioUsuario(resolveUsuario(usuario));
        periodo.setMetadata("{}");
        atendimentoPeriodoRepository.save(periodo);
    }

    private void fecharTodosPeriodosAbertos(Long atendimentoId, String usuario) {
        fecharTodosPeriodosAbertos(atendimentoId, usuario, LocalDateTime.now());
    }

    private void fecharTodosPeriodosAbertos(Long atendimentoId, String usuario, LocalDateTime fimEm) {
        List<AtendimentoPeriodo> abertos = atendimentoPeriodoRepository.findByAtendimentoIdAndFimEmIsNullOrderByInicioEmAsc(atendimentoId);
        for (AtendimentoPeriodo periodo : abertos) {
            periodo.setFimEm(fimEm);
            periodo.setUsuarioFim(usuario);
            periodo.setUsuarioFimUsuario(resolveUsuario(usuario));
            atendimentoPeriodoRepository.save(periodo);
        }
    }

    private void fecharPeriodoAberto(Long atendimentoId, AtendimentoPeriodoTipo tipo, String usuario) {
        fecharPeriodoAberto(atendimentoId, tipo, usuario, LocalDateTime.now());
    }

    private void fecharPeriodoAberto(Long atendimentoId,
                                     AtendimentoPeriodoTipo tipo,
                                     String usuario,
                                     LocalDateTime fimEm) {
        atendimentoPeriodoRepository.findFirstByAtendimentoIdAndTipoAndFimEmIsNull(atendimentoId, tipo)
                .ifPresent(periodo -> {
                    periodo.setFimEm(fimEm == null ? LocalDateTime.now() : fimEm);
                    periodo.setUsuarioFim(usuario);
                    periodo.setUsuarioFimUsuario(resolveUsuario(usuario));
                    atendimentoPeriodoRepository.save(periodo);
                });
    }

    private void registrarEvento(Atendimento atendimento, AtendimentoEventoTipo tipo, String metadata) {
        AtendimentoEvento evento = new AtendimentoEvento();
        evento.setAtendimento(atendimento);
        evento.setTipo(tipo);
        evento.setDataHora(LocalDateTime.now());
        evento.setUsuario(currentUsername());
        evento.setUsuarioRegistro(currentUsuario());
        evento.setMetadata(metadata == null ? "{}" : metadata);
        atendimentoEventoRepository.save(evento);
    }

    private void salvarAlergiaEComorbidadesDaClassificacao(ClassificacaoRisco classificacao, TriagemForm form) {
        classificacaoRiscoAlergiaRepository.deleteByClassificacaoRiscoId(classificacao.getId());
        classificacaoRiscoComorbidadeRepository.deleteByClassificacaoRiscoId(classificacao.getId());

        List<TriagemForm.AlergiaItem> alergias = form.getAlergias() == null ? List.of() : form.getAlergias();
        for (TriagemForm.AlergiaItem itemForm : alergias) {
            if (itemForm == null || itemForm.getSubstanciaId() == null) {
                continue;
            }
            AlergiaSubstancia substancia = alergiaSubstanciaRepository.findById(itemForm.getSubstanciaId())
                    .orElseThrow(() -> new IllegalArgumentException("Substancia de alergia nao encontrada"));
            String descricaoSubstancia = normalize(substancia.getDescricao());
            boolean naoRelataAlergia = descricaoSubstancia != null
                    && "NAO RELATA ALERGIA".equalsIgnoreCase(descricaoSubstancia);

            String descricao = normalize(itemForm.getDescricao());
            if (descricao != null) {
                descricao = descricao.toUpperCase(Locale.ROOT);
            }
            if (!naoRelataAlergia && descricao == null) {
                throw new IllegalArgumentException("Detalhe da alergia e obrigatorio para a substancia selecionada");
            }

            AlergiaSeveridade severidade = null;
            if (!naoRelataAlergia && itemForm.getSeveridadeId() != null) {
                severidade = alergiaSeveridadeRepository.findById(itemForm.getSeveridadeId())
                        .orElseThrow(() -> new IllegalArgumentException("Severidade de alergia nao encontrada"));
            }

            ClassificacaoRiscoAlergia alergia = new ClassificacaoRiscoAlergia();
            alergia.setClassificacaoRisco(classificacao);
            alergia.setSubstancia(substancia);
            alergia.setSeveridade(severidade);
            alergia.setDescricao(naoRelataAlergia ? null : descricao);
            classificacaoRiscoAlergiaRepository.save(alergia);
        }

        List<Long> comorbidadeIds = form.getComorbidadeIds() == null ? List.of() : form.getComorbidadeIds();
        for (Long comorbidadeId : comorbidadeIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            Comorbidade comorbidade = comorbidadeRepository.findById(comorbidadeId)
                    .orElseThrow(() -> new IllegalArgumentException("Comorbidade nao encontrada"));
            ClassificacaoRiscoComorbidade item = new ClassificacaoRiscoComorbidade();
            item.setClassificacaoRisco(classificacao);
            item.setComorbidade(comorbidade);
            classificacaoRiscoComorbidadeRepository.save(item);
        }
    }

    private void salvarAvcSinaisAlertaDaClassificacao(ClassificacaoRisco classificacao, TriagemForm form) {
        classificacaoRiscoAvcSinalAlertaRepository.deleteByClassificacaoRiscoId(classificacao.getId());
        List<Long> avcSinalAlertaIds = form.getAvcSinalAlertaIds() == null ? List.of() : form.getAvcSinalAlertaIds();
        for (Long sinalId : avcSinalAlertaIds.stream().filter(java.util.Objects::nonNull).distinct().toList()) {
            AvcSinalAlerta sinal = avcSinalAlertaRepository.findById(sinalId)
                    .orElseThrow(() -> new IllegalArgumentException("Sinal de alerta de AVC nao encontrado"));
            ClassificacaoRiscoAvcSinalAlerta item = new ClassificacaoRiscoAvcSinalAlerta();
            item.setClassificacaoRisco(classificacao);
            item.setAvcSinalAlerta(sinal);
            classificacaoRiscoAvcSinalAlertaRepository.save(item);
        }
    }

    private ReguaDor resolveReguaDor(Long reguaDorId) {
        if (reguaDorId == null) {
            throw new IllegalArgumentException("Escala de dor obrigatoria");
        }
        ReguaDor reguaDor = reguaDorRepository.findById(reguaDorId)
                .orElseThrow(() -> new IllegalArgumentException("Escala de dor nao encontrada"));
        if (!reguaDor.isAtivo()) {
            throw new IllegalArgumentException("Escala de dor inativa");
        }
        return reguaDor;
    }

    private void salvarGlasgowDaClassificacao(ClassificacaoRisco classificacao, TriagemForm form) {
        Long ocularId = form.getGlasgowAberturaOcularId();
        Long verbalId = form.getGlasgowRespostaVerbalId();
        Long motoraId = form.getGlasgowRespostaMotoraId();
        Long pupilarId = form.getGlasgowRespostaPupilarId();

        boolean any = ocularId != null || verbalId != null || motoraId != null || pupilarId != null;
        if (!any) {
            classificacaoGlasgowRepository.findByClassificacaoRiscoId(classificacao.getId())
                    .ifPresent(classificacaoGlasgowRepository::delete);
            return;
        }

        if (ocularId == null || verbalId == null || motoraId == null) {
            throw new IllegalArgumentException("Glasgow incompleto: informe abertura ocular, resposta verbal e resposta motora");
        }

        GlasgowAberturaOcular aberturaOcular = glasgowAberturaOcularRepository.findById(ocularId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow abertura ocular nao encontrado"));
        GlasgowRespostaVerbal respostaVerbal = glasgowRespostaVerbalRepository.findById(verbalId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta verbal nao encontrado"));
        GlasgowRespostaMotora respostaMotora = glasgowRespostaMotoraRepository.findById(motoraId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta motora nao encontrado"));
        GlasgowRespostaPupilar respostaPupilar = null;
        if (pupilarId != null) {
            respostaPupilar = glasgowRespostaPupilarRepository.findById(pupilarId)
                    .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta pupilar nao encontrado"));
        }

        int pupilarPontos = respostaPupilar == null ? 0 : respostaPupilar.getPontos();
        int total = aberturaOcular.getPontos() + respostaVerbal.getPontos() + respostaMotora.getPontos() - pupilarPontos;
        if (total < 1 || total > 15) {
            throw new IllegalArgumentException("Total de Glasgow invalido");
        }

        ClassificacaoGlasgow glasgow = classificacaoGlasgowRepository.findByClassificacaoRiscoId(classificacao.getId())
                .orElseGet(ClassificacaoGlasgow::new);
        glasgow.setClassificacaoRisco(classificacao);
        glasgow.setAberturaOcular(aberturaOcular);
        glasgow.setRespostaVerbal(respostaVerbal);
        glasgow.setRespostaMotora(respostaMotora);
        glasgow.setRespostaPupilar(respostaPupilar);
        glasgow.setTotal(total);
        glasgow.setDataHora(LocalDateTime.now());
        classificacaoGlasgowRepository.save(glasgow);
    }

    private Map<Long, ClassificacaoReavaliacao> mapaUltimaReavaliacao(List<Long> atendimentoIds) {
        if (atendimentoIds == null || atendimentoIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, ClassificacaoReavaliacao> result = new LinkedHashMap<>();
        for (ClassificacaoReavaliacao reavaliacao : classificacaoReavaliacaoRepository
                .findByAtendimentoIdsOrderByDataHoraDesc(atendimentoIds)) {
            Long atendimentoId = reavaliacao.getClassificacaoRisco().getAtendimento().getId();
            result.putIfAbsent(atendimentoId, reavaliacao);
        }
        return result;
    }

    private ClassificacaoSinaisVitais buildSinaisVitaisSeInformado(TriagemForm form) {
        String pressaoArterial = normalize(form.getPressaoArterial());
        if (pressaoArterial == null
                && form.getTemperatura() == null
                && form.getFrequenciaCardiaca() == null
                && form.getSaturacaoO2() == null
                && form.getFrequenciaRespiratoria() == null) {
            return null;
        }
        ClassificacaoSinaisVitais sinaisVitais = new ClassificacaoSinaisVitais();
        sinaisVitais.setPressaoArterial(pressaoArterial);
        sinaisVitais.setTemperatura(form.getTemperatura());
        sinaisVitais.setFrequenciaCardiaca(form.getFrequenciaCardiaca());
        sinaisVitais.setSaturacaoO2(form.getSaturacaoO2());
        sinaisVitais.setFrequenciaRespiratoria(form.getFrequenciaRespiratoria());
        return sinaisVitais;
    }

    private ClassificacaoOxigenacao buildOxigenacaoSeInformado(TriagemForm form) {
        if (form.getSaturacaoO2ComTerapiaO2() == null && form.getSaturacaoO2Aa() == null) {
            return null;
        }
        ClassificacaoOxigenacao oxigenacao = new ClassificacaoOxigenacao();
        oxigenacao.setSaturacaoO2ComTerapiaO2(form.getSaturacaoO2ComTerapiaO2());
        oxigenacao.setSaturacaoO2Aa(form.getSaturacaoO2Aa());
        return oxigenacao;
    }

    private ClassificacaoGlicemia buildGlicemiaSeInformado(TriagemForm form) {
        if (form.getGlicemiaCapilar() == null && form.getHgt() == null) {
            return null;
        }
        ClassificacaoGlicemia glicemia = new ClassificacaoGlicemia();
        glicemia.setGlicemiaCapilar(form.getGlicemiaCapilar());
        glicemia.setHgt(form.getHgt());
        return glicemia;
    }

    private ClassificacaoAntropometria buildAntropometriaSeInformado(TriagemForm form) {
        if (form.getPesoKg() == null && form.getAlturaCm() == null) {
            return null;
        }
        ClassificacaoAntropometria antropometria = new ClassificacaoAntropometria();
        antropometria.setPesoKg(form.getPesoKg());
        antropometria.setAlturaCm(form.getAlturaCm());
        return antropometria;
    }

    private ClassificacaoPerfusao buildPerfusaoSeInformado(TriagemForm form) {
        if (form.getPerfusaoCapilarPerifericaSeg() == null && form.getPreenchimentoCapilarCentralSeg() == null) {
            return null;
        }
        ClassificacaoPerfusao perfusao = new ClassificacaoPerfusao();
        perfusao.setPerfusaoCapilarPerifericaSeg(form.getPerfusaoCapilarPerifericaSeg());
        perfusao.setPreenchimentoCapilarCentralSeg(form.getPreenchimentoCapilarCentralSeg());
        return perfusao;
    }

    private void preencherGlasgowDaReavaliacao(ClassificacaoReavaliacao reavaliacao, TriagemForm form) {
        Long ocularId = form.getGlasgowAberturaOcularId();
        Long verbalId = form.getGlasgowRespostaVerbalId();
        Long motoraId = form.getGlasgowRespostaMotoraId();
        Long pupilarId = form.getGlasgowRespostaPupilarId();

        boolean any = ocularId != null || verbalId != null || motoraId != null || pupilarId != null;
        if (!any) {
            reavaliacao.setGlasgowAberturaOcular(null);
            reavaliacao.setGlasgowRespostaVerbal(null);
            reavaliacao.setGlasgowRespostaMotora(null);
            reavaliacao.setGlasgowRespostaPupilar(null);
            reavaliacao.setGlasgowTotal(null);
            return;
        }
        if (ocularId == null || verbalId == null || motoraId == null) {
            throw new IllegalArgumentException("Glasgow incompleto: informe abertura ocular, resposta verbal e resposta motora");
        }

        GlasgowAberturaOcular aberturaOcular = glasgowAberturaOcularRepository.findById(ocularId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow abertura ocular nao encontrado"));
        GlasgowRespostaVerbal respostaVerbal = glasgowRespostaVerbalRepository.findById(verbalId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta verbal nao encontrado"));
        GlasgowRespostaMotora respostaMotora = glasgowRespostaMotoraRepository.findById(motoraId)
                .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta motora nao encontrado"));
        GlasgowRespostaPupilar respostaPupilar = null;
        if (pupilarId != null) {
            respostaPupilar = glasgowRespostaPupilarRepository.findById(pupilarId)
                    .orElseThrow(() -> new IllegalArgumentException("Glasgow resposta pupilar nao encontrado"));
        }

        int pupilarPontos = respostaPupilar == null ? 0 : respostaPupilar.getPontos();
        int total = aberturaOcular.getPontos() + respostaVerbal.getPontos() + respostaMotora.getPontos() - pupilarPontos;
        if (total < 1 || total > 15) {
            throw new IllegalArgumentException("Total de Glasgow invalido");
        }

        reavaliacao.setGlasgowAberturaOcular(aberturaOcular);
        reavaliacao.setGlasgowRespostaVerbal(respostaVerbal);
        reavaliacao.setGlasgowRespostaMotora(respostaMotora);
        reavaliacao.setGlasgowRespostaPupilar(respostaPupilar);
        reavaliacao.setGlasgowTotal(total);
    }

    private static boolean isEncerrado(Atendimento atendimento) {
        if (atendimento.getStatus() == null || atendimento.getStatus().getCodigo() == null) {
            return false;
        }
        String codigo = atendimento.getStatus().getCodigo();
        return "FINALIZADO".equalsIgnoreCase(codigo)
                || "EVADIU".equalsIgnoreCase(codigo)
                || "ABANDONO".equalsIgnoreCase(codigo)
                || "TRANSFERIDO".equalsIgnoreCase(codigo);
    }

    private static List<String> statusesAbertos() {
        return List.of(
                "AGUARDANDO",
                "EM_TRIAGEM",
                "AGUARDANDO_RECEPCAO",
                "AGUARDANDO_TRIAGEM",
                "AGUARDANDO_MEDICO",
                "EM_ATENDIMENTO");
    }

    private StatusAtendimento status(String codigo) {
        return statusAtendimentoRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Status de atendimento nao encontrado: " + codigo));
    }

    private Procedencia resolverOuCriarProcedenciaBairro(TipoProcedencia tipoProcedencia, Bairro bairro, Unidade unidade) {
        return procedenciaRepository
                .findPrimeiraAtivaByTipoEPorBairro(tipoProcedencia.getId(), bairro.getId(), unidade.getId())
                .orElseGet(() -> {
                    Procedencia nova = new Procedencia();
                    nova.setTipoProcedencia(tipoProcedencia);
                    nova.setBairro(bairro);
                    nova.setMunicipio(null);
                    nova.setDescricao(normalize(bairro.getDescricao()));
                    nova.setUnidade(null);
                    nova.setAtivo(true);
                    return procedenciaRepository.save(nova);
                });
    }

    private Procedencia resolverOuCriarProcedenciaMunicipio(TipoProcedencia tipoProcedencia, Municipio municipio, Unidade unidade) {
        return procedenciaRepository
                .findPrimeiraAtivaByTipoEPorMunicipio(tipoProcedencia.getId(), municipio.getId(), unidade.getId())
                .orElseGet(() -> {
                    Procedencia nova = new Procedencia();
                    nova.setTipoProcedencia(tipoProcedencia);
                    nova.setBairro(null);
                    nova.setMunicipio(municipio);
                    nova.setDescricao(normalize(municipio.getNome()));
                    nova.setUnidade(null);
                    nova.setAtivo(true);
                    return procedenciaRepository.save(nova);
                });
    }

    private Procedencia resolverOuCriarProcedenciaDescricao(TipoProcedencia tipoProcedencia, String descricao, Unidade unidade) {
        return procedenciaRepository
                .findPrimeiraAtivaByTipoEDescricao(tipoProcedencia.getId(), descricao, unidade.getId())
                .orElseGet(() -> {
                    Procedencia nova = new Procedencia();
                    nova.setTipoProcedencia(tipoProcedencia);
                    nova.setBairro(null);
                    nova.setMunicipio(null);
                    nova.setDescricao(descricao);
                    nova.setUnidade(null);
                    nova.setAtivo(true);
                    return procedenciaRepository.save(nova);
                });
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String currentUsername() {
        return usuarioAuditoriaService.usernameAtualOuSistema();
    }

    private Usuario currentUsuario() {
        return usuarioAuditoriaService.usuarioAtual().orElse(null);
    }

    private Usuario resolveUsuario(String principal) {
        return usuarioAuditoriaService.usuarioPorPrincipalOuNull(principal);
    }

    private static String usernameOrNull(Usuario usuario) {
        if (usuario == null || usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            return null;
        }
        return usuario.getUsername();
    }

    private static String firstNonBlank(String preferred, String fallback) {
        if (preferred != null && !preferred.isBlank()) {
            return preferred;
        }
        return (fallback == null || fallback.isBlank()) ? null : fallback;
    }
}
