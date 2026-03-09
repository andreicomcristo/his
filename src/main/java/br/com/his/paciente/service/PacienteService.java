package br.com.his.paciente.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Usuario;
import br.com.his.access.service.UsuarioAuditoriaService;
import br.com.his.configuracao.model.Cidade;
import br.com.his.paciente.dto.PacienteForm;
import br.com.his.paciente.model.Paciente;
import br.com.his.paciente.model.PacienteMergeLog;
import br.com.his.paciente.model.lookup.Deficiencia;
import br.com.his.paciente.model.lookup.Escolaridade;
import br.com.his.paciente.model.lookup.EstadoCivil;
import br.com.his.paciente.model.lookup.EtniaIndigena;
import br.com.his.paciente.model.lookup.IdentidadeGenero;
import br.com.his.paciente.model.lookup.Nacionalidade;
import br.com.his.paciente.model.lookup.Naturalidade;
import br.com.his.paciente.model.lookup.OrientacaoSexual;
import br.com.his.paciente.model.lookup.Procedencia;
import br.com.his.paciente.model.lookup.Profissao;
import br.com.his.paciente.model.lookup.RacaCor;
import br.com.his.paciente.model.lookup.Sexo;
import br.com.his.paciente.model.lookup.TipoSanguineo;
import br.com.his.paciente.repository.PacienteMergeLogRepository;
import br.com.his.paciente.repository.PacienteRepository;
import br.com.his.paciente.repository.SexoRepository;
import br.com.his.paciente.validation.CpfUtils;

@Service
public class PacienteService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PacienteRepository pacienteRepository;
    private final PacienteMergeLogRepository pacienteMergeLogRepository;
    private final PacienteLookupService pacienteLookupService;
    private final SexoRepository sexoRepository;
    private final UsuarioAuditoriaService usuarioAuditoriaService;

    public PacienteService(PacienteRepository pacienteRepository,
                           PacienteMergeLogRepository pacienteMergeLogRepository,
                           PacienteLookupService pacienteLookupService,
                           SexoRepository sexoRepository,
                           UsuarioAuditoriaService usuarioAuditoriaService) {
        this.pacienteRepository = pacienteRepository;
        this.pacienteMergeLogRepository = pacienteMergeLogRepository;
        this.pacienteLookupService = pacienteLookupService;
        this.sexoRepository = sexoRepository;
        this.usuarioAuditoriaService = usuarioAuditoriaService;
    }

    @Transactional
    public Paciente criarPacienteDefinitivo(PacienteForm dto) {
        Paciente paciente = new Paciente();
        applyForm(dto, paciente);
        paciente.setTemporario(false);
        paciente.setAtivo(true);
        paciente.setDataCancelamento(null);
        paciente.setMergedInto(null);
        paciente.setCriadoEm(LocalDateTime.now());
        paciente.setCriadoPor(currentUsername());
        paciente.setCriadoPorUsuario(currentUsuario());
        paciente.setAtualizadoEm(LocalDateTime.now());
        paciente.setAtualizadoPor(currentUsername());
        paciente.setAtualizadoPorUsuario(currentUsuario());

        validateTemporaryData(paciente);
        pacienteLookupService.validarReferencias(dto);
        validateUniqueCpf(paciente.getCpf(), null);

        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente criarPacienteTemporario(PacienteForm dto) {
        Paciente paciente = new Paciente();
        applyForm(dto, paciente);

        paciente.setTemporario(true);
        if (isBlank(paciente.getNome())) {
            paciente.setNome(generateTemporaryName(paciente.getSexo()));
        }
        paciente.setAtivo(true);
        paciente.setDataCancelamento(null);
        paciente.setMergedInto(null);
        paciente.setCriadoEm(LocalDateTime.now());
        paciente.setCriadoPor(currentUsername());
        paciente.setCriadoPorUsuario(currentUsuario());
        paciente.setAtualizadoEm(LocalDateTime.now());
        paciente.setAtualizadoPor(currentUsername());
        paciente.setAtualizadoPorUsuario(currentUsuario());

        validateTemporaryData(paciente);
        pacienteLookupService.validarReferencias(dto);
        validateUniqueCpf(paciente.getCpf(), null);

        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente criarPacienteTemporario(String sexo, Integer idadeAparente) {
        PacienteForm form = new PacienteForm();
        form.setTemporario(true);
        form.setSexo(normalizeSexo(sexo));
        form.setIdadeAparente(idadeAparente);
        form.setNome(generateTemporaryName(form.getSexo()));
        return criarPacienteTemporario(form);
    }

    @Transactional
    public Paciente atualizarPaciente(Long id, PacienteForm dto) {
        Paciente paciente = buscarPorId(id);

        if (!paciente.isAtivo()) {
            throw new IllegalArgumentException("Paciente inativo nao pode ser editado");
        }

        boolean wasTemporary = paciente.isTemporario();
        applyForm(dto, paciente);
        if (wasTemporary && paciente.getCpf() != null) {
            paciente.setTemporario(false);
        }
        paciente.setAtualizadoEm(LocalDateTime.now());
        paciente.setAtualizadoPor(currentUsername());
        paciente.setAtualizadoPorUsuario(currentUsuario());

        validateTemporaryData(paciente);
        pacienteLookupService.validarReferencias(dto);
        validateUniqueCpf(paciente.getCpf(), paciente.getId());

        Paciente updated = pacienteRepository.save(paciente);

        if (wasTemporary && !updated.isTemporario()) {
            Optional<Paciente> existingReal = findDefinitiveCandidate(updated);
            if (existingReal.isPresent()) {
                Paciente target = existingReal.get();
                if (!target.getId().equals(updated.getId())) {
                    copyMissingData(updated, target);
                    pacienteRepository.save(target);
                    mergePacienteInternal(updated, target,
                            "Identificacao de paciente temporario com cadastro definitivo existente");
                    return target;
                }
            }
        }

        return updated;
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscarPorCpfCnsNome(String nome, String cpf, String cns) {
        String nomeFilter = normalizeUpper(nome);
        String cpfFilter = CpfUtils.digitsOnly(cpf);
        String cnsFilter = normalizeUpper(cns);

        Specification<Paciente> spec = Specification.where(null);

        if (nomeFilter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("nome")), "%" + nomeFilter + "%"));
        }
        if (cpfFilter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("cpf"), cpfFilter));
        }
        if (cnsFilter != null) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("cns")), "%" + cnsFilter + "%"));
        }

        return pacienteRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional
    public void mergePaciente(Long fromId, Long toId, String motivo) {
        Paciente from = buscarPorId(fromId);
        Paciente to = buscarPorId(toId);
        mergePacienteInternal(from, to, motivo);
    }

    @Transactional
    public void cancelarPaciente(Long id) {
        Paciente paciente = buscarPorId(id);
        if (!paciente.isAtivo()) {
            return;
        }
        paciente.setAtivo(false);
        paciente.setDataCancelamento(LocalDateTime.now());
        paciente.setAtualizadoEm(LocalDateTime.now());
        paciente.setAtualizadoPor(currentUsername());
        paciente.setAtualizadoPorUsuario(currentUsuario());
        pacienteRepository.save(paciente);
    }

    @Transactional
    public void restaurarPaciente(Long id) {
        Paciente paciente = buscarPorId(id);
        if (paciente.isAtivo()) {
            return;
        }
        if (paciente.getMergedInto() != null) {
            throw new IllegalArgumentException("Paciente mergeado nao pode ser restaurado");
        }
        paciente.setAtivo(true);
        paciente.setDataCancelamento(null);
        paciente.setAtualizadoEm(LocalDateTime.now());
        paciente.setAtualizadoPor(currentUsername());
        paciente.setAtualizadoPorUsuario(currentUsuario());
        pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Paciente> buscarDefinitivoAtivoPorCpf(String cpf, Long ignorePacienteId) {
        String normalizedCpf = CpfUtils.digitsOnly(cpf);
        if (normalizedCpf == null) {
            return Optional.empty();
        }
        return pacienteRepository.findFirstByCpfAndAtivoTrueAndMergedIntoIsNullAndTemporarioFalse(normalizedCpf)
                .filter(p -> ignorePacienteId == null || !p.getId().equals(ignorePacienteId));
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarAtivosNaoMergeadosParaSelecao() {
        return pacienteRepository.findTop200ByAtivoTrueAndMergedIntoIsNullOrderByNomeAsc();
    }

    private void mergePacienteInternal(Paciente from, Paciente to, String motivo) {
        validateMerge(from, to);

        moveFutureRelationships(from, to);

        from.setAtivo(false);
        from.setDataCancelamento(LocalDateTime.now());
        from.setMergedInto(to);
        from.setAtualizadoEm(LocalDateTime.now());
        from.setAtualizadoPor(currentUsername());
        from.setAtualizadoPorUsuario(currentUsuario());

        pacienteRepository.save(from);

        PacienteMergeLog log = new PacienteMergeLog();
        log.setFromPaciente(from);
        log.setToPaciente(to);
        log.setMergedEm(LocalDateTime.now());
        log.setMergedPor(currentUsername());
        log.setMergedPorUsuario(currentUsuario());
        log.setMotivo(isBlank(motivo) ? "Merge manual" : motivo.trim());
        pacienteMergeLogRepository.save(log);
    }

    private void validateMerge(Paciente from, Paciente to) {
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Merge invalido: pacientes de origem e destino sao iguais");
        }
        if (!from.isAtivo() || from.getMergedInto() != null) {
            throw new IllegalArgumentException("Merge invalido: paciente de origem inativo ou ja mergeado");
        }
        if (!to.isAtivo()) {
            throw new IllegalArgumentException("Merge invalido: paciente destino inativo");
        }
    }

    private void moveFutureRelationships(Paciente from, Paciente to) {
        // Estrutura pronta para futuros modulos clinicos.
        // Quando houver tabelas relacionais (episodio, entrada, etc),
        // mover as FKs de from -> to nesta mesma transacao.
    }

    private void applyForm(PacienteForm dto, Paciente paciente) {
        paciente.setNome(normalizeName(dto.getNome()));
        paciente.setNomeSocial(normalizeText(dto.getNomeSocial()));
        paciente.setCpf(CpfUtils.digitsOnly(dto.getCpf()));
        paciente.setCns(digitsOnlyOrNull(dto.getCns()));
        paciente.setRg(normalizeText(dto.getRg()));
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setSexoRegistro(resolveSexo(normalizeSexo(dto.getSexo())));
        paciente.setTelefone(digitsOnlyOrNull(dto.getTelefone()));
        paciente.setNomeMae(normalizeText(dto.getNomeMae()));
        paciente.setNomePai(normalizeText(dto.getNomePai()));
        paciente.setRacaCor(reference(dto.getRacaCorId(), RacaCor.class));
        paciente.setEtniaIndigena(reference(dto.getEtniaIndigenaId(), EtniaIndigena.class));
        paciente.setNacionalidade(reference(dto.getNacionalidadeId(), Nacionalidade.class));
        paciente.setNaturalidade(reference(dto.getNaturalidadeId(), Naturalidade.class));
        paciente.setEstadoCivil(reference(dto.getEstadoCivilId(), EstadoCivil.class));
        paciente.setEscolaridade(reference(dto.getEscolaridadeId(), Escolaridade.class));
        paciente.setTipoSanguineo(reference(dto.getTipoSanguineoId(), TipoSanguineo.class));
        paciente.setOrientacaoSexual(reference(dto.getOrientacaoSexualId(), OrientacaoSexual.class));
        paciente.setIdentidadeGenero(reference(dto.getIdentidadeGeneroId(), IdentidadeGenero.class));
        paciente.setDeficiencia(reference(dto.getDeficienciaId(), Deficiencia.class));
        paciente.setProfissao(reference(dto.getProfissaoId(), Profissao.class));
        paciente.setProcedencia(reference(dto.getProcedenciaId(), Procedencia.class));
        paciente.setEmail(normalizeText(dto.getEmail()));
        paciente.setObservacoes(normalizeText(dto.getObservacoes()));
        paciente.setCep(digitsOnlyOrNull(dto.getCep()));
        paciente.setLogradouro(normalizeText(dto.getLogradouro()));
        paciente.setNumero(normalizeText(dto.getNumero()));
        paciente.setComplemento(normalizeText(dto.getComplemento()));
        paciente.setBairro(normalizeText(dto.getBairro()));
        paciente.setCidade(reference(dto.getCidadeId(), Cidade.class));
        paciente.setTemporario(dto.isTemporario());
        paciente.setIdadeAparente(dto.getIdadeAparente());
        validateCidadeUf(dto, paciente);
    }

    private void validateTemporaryData(Paciente paciente) {
        if (!paciente.isTemporario() && isBlank(paciente.getNome())) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (paciente.getDataNascimento() != null && paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento invalida");
        }
        if (paciente.getRacaCor() != null && Long.valueOf(7L).equals(paciente.getRacaCor().getId())
                && paciente.getEtniaIndigena() == null) {
            throw new IllegalArgumentException("A Etnia Indigena e obrigatoria quando a Raça/Cor e indigena");
        }
    }

    private void validateUniqueCpf(String cpf, Long id) {
        if (cpf == null) {
            return;
        }
        if (id == null) {
            Optional<Paciente> existing = pacienteRepository.findFirstByCpfAndAtivoTrueAndMergedIntoIsNull(cpf);
            if (existing.isPresent()) {
                throw new IllegalArgumentException("CPF ja cadastrado em outro paciente");
            }
            return;
        }

        if (pacienteRepository.existsByCpfAndAtivoTrueAndMergedIntoIsNullAndIdNot(cpf, id)) {
            throw new IllegalArgumentException("CPF ja cadastrado em outro paciente");
        }
    }

    private Optional<Paciente> findDefinitiveCandidate(Paciente paciente) {
        if (paciente.getCpf() != null) {
            Optional<Paciente> byCpf = pacienteRepository.findFirstByCpfAndAtivoTrueAndMergedIntoIsNull(paciente.getCpf());
            if (byCpf.isPresent() && !byCpf.get().isTemporario()) {
                return byCpf;
            }
        }

        if (paciente.getCns() != null) {
            Optional<Paciente> byCns = pacienteRepository.findFirstByCnsIgnoreCaseAndAtivoTrueAndMergedIntoIsNull(paciente.getCns());
            if (byCns.isPresent() && !byCns.get().isTemporario()) {
                return byCns;
            }
        }

        return Optional.empty();
    }

    private void copyMissingData(Paciente from, Paciente to) {
        if (isBlank(to.getNomeSocial()) && !isBlank(from.getNomeSocial())) {
            to.setNomeSocial(from.getNomeSocial());
        }
        if (to.getDataNascimento() == null && from.getDataNascimento() != null) {
            to.setDataNascimento(from.getDataNascimento());
        }
        if (isBlank(to.getSexo()) && !isBlank(from.getSexo())) {
            to.setSexoRegistro(from.getSexoRegistro());
        }
        if (isBlank(to.getNomeMae()) && !isBlank(from.getNomeMae())) {
            to.setNomeMae(from.getNomeMae());
        }
        if (isBlank(to.getCns()) && !isBlank(from.getCns())) {
            to.setCns(from.getCns());
        }
        if (isBlank(to.getTelefone()) && !isBlank(from.getTelefone())) {
            to.setTelefone(from.getTelefone());
        }
        if (isBlank(to.getLogradouro()) && !isBlank(from.getLogradouro())) {
            to.setLogradouro(from.getLogradouro());
        }
        if (isBlank(to.getNumero()) && !isBlank(from.getNumero())) {
            to.setNumero(from.getNumero());
        }
        if (isBlank(to.getBairro()) && !isBlank(from.getBairro())) {
            to.setBairro(from.getBairro());
        }
        if (to.getCidade() == null && from.getCidade() != null) {
            to.setCidade(from.getCidade());
        }
        if (to.getIdadeAparente() == null && from.getIdadeAparente() != null) {
            to.setIdadeAparente(from.getIdadeAparente());
        }
        if (to.getRacaCor() == null && from.getRacaCor() != null) {
            to.setRacaCor(from.getRacaCor());
        }
        if (to.getEtniaIndigena() == null && from.getEtniaIndigena() != null) {
            to.setEtniaIndigena(from.getEtniaIndigena());
        }
        if (to.getNacionalidade() == null && from.getNacionalidade() != null) {
            to.setNacionalidade(from.getNacionalidade());
        }
        if (to.getNaturalidade() == null && from.getNaturalidade() != null) {
            to.setNaturalidade(from.getNaturalidade());
        }
        if (to.getEstadoCivil() == null && from.getEstadoCivil() != null) {
            to.setEstadoCivil(from.getEstadoCivil());
        }
        if (to.getEscolaridade() == null && from.getEscolaridade() != null) {
            to.setEscolaridade(from.getEscolaridade());
        }
        if (to.getTipoSanguineo() == null && from.getTipoSanguineo() != null) {
            to.setTipoSanguineo(from.getTipoSanguineo());
        }
        if (to.getOrientacaoSexual() == null && from.getOrientacaoSexual() != null) {
            to.setOrientacaoSexual(from.getOrientacaoSexual());
        }
        if (to.getIdentidadeGenero() == null && from.getIdentidadeGenero() != null) {
            to.setIdentidadeGenero(from.getIdentidadeGenero());
        }
        if (to.getDeficiencia() == null && from.getDeficiencia() != null) {
            to.setDeficiencia(from.getDeficiencia());
        }
        if (to.getProfissao() == null && from.getProfissao() != null) {
            to.setProfissao(from.getProfissao());
        }
        if (to.getProcedencia() == null && from.getProcedencia() != null) {
            to.setProcedencia(from.getProcedencia());
        }
        to.setAtualizadoEm(LocalDateTime.now());
        to.setAtualizadoPor(currentUsername());
        to.setAtualizadoPorUsuario(currentUsuario());
    }

    private <T> T reference(Long id, Class<T> type) {
        if (id == null) {
            return null;
        }
        return entityManager.getReference(type, id);
    }

    private String generateTemporaryName(String sexo) {
        String normalizedSexo = normalizeSexo(sexo);
        long next = pacienteRepository.countByTemporarioTrueAndSexo_CodigoIgnoreCase(normalizedSexo) + 1;
        return "Desconhecido " + normalizedSexo + " " + next;
    }

    private Sexo resolveSexo(String codigo) {
        return sexoRepository.findByCodigoIgnoreCase(codigo == null ? "NI" : codigo)
                .orElseThrow(() -> new IllegalArgumentException("Sexo invalido"));
    }

    private String currentUsername() {
        return usuarioAuditoriaService.usernameAtualOuSistema();
    }

    private Usuario currentUsuario() {
        return usuarioAuditoriaService.usuarioAtual().orElse(null);
    }

    private void validateCidadeUf(PacienteForm dto, Paciente paciente) {
        if (dto.getCidadeId() == null) {
            return;
        }
        Cidade cidade = paciente.getCidade();
        if (cidade == null) {
            throw new IllegalArgumentException("Cidade invalida");
        }
        if (dto.getUnidadeFederativaId() != null
                && !dto.getUnidadeFederativaId().equals(cidade.getUnidadeFederativa().getId())) {
            throw new IllegalArgumentException("Cidade nao pertence a UF informada");
        }
    }

    private static String normalizeName(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private static String normalizeSexo(String value) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return "NI";
        }
        String upper = normalized.toUpperCase();
        if ("M".equals(upper) || "F".equals(upper) || "NI".equals(upper)) {
            return upper;
        }
        return "NI";
    }

    private static String normalizeUpper(String value) {
        String normalized = normalizeText(value);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private static String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String text = value.trim();
        return text.isEmpty() ? null : text;
    }

    private static String digitsOnlyOrNull(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "").trim();
        return digits.isEmpty() ? null : digits;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
