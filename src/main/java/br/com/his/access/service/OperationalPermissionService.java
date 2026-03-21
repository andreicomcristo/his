package br.com.his.access.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import br.com.his.care.attendance.model.UnidadeTipoAtendimento;
import br.com.his.care.attendance.service.TipoAtendimentoService;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.attendance.repository.UnidadeTipoAtendimentoRepository;

@Service
public class OperationalPermissionService {

    public static final String PERM_ATENDIMENTO_ACESSAR = "ATENDIMENTO_ACESSAR";
    public static final String PERM_TRIAGEM_EXECUTAR = "TRIAGEM_EXECUTAR";
    public static final String PERM_RECEPCAO_EXECUTAR = "RECEPCAO_EXECUTAR";
    public static final String PERM_EPISODIO_ABRIR = "EPISODIO_ABRIR";
    public static final String PERM_ENTRADA_REGISTRAR = "ENTRADA_REGISTRAR";
    public static final String PERM_PACIENTE_IDENTIFICAR = "PACIENTE_IDENTIFICAR";
    public static final String PERM_BUROCRATA_EXECUTAR = "BUROCRATA_EXECUTAR";

    private final AccessContextService accessContextService;
    private final UnidadeContext unidadeContext;
    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository;
    private final TipoAtendimentoService tipoAtendimentoService;

    public OperationalPermissionService(AccessContextService accessContextService,
                                        UnidadeContext unidadeContext,
                                        ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository,
                                        UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                        UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository,
                                        TipoAtendimentoService tipoAtendimentoService) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.unidadeTipoAtendimentoRepository = unidadeTipoAtendimentoRepository;
        this.tipoAtendimentoService = tipoAtendimentoService;
    }

    public boolean has(Authentication authentication, String permission) {
        return hasAny(authentication, Set.of(permission));
    }

    public boolean hasAny(Authentication authentication, Set<String> permissionNames) {
        Set<String> requested = Set.copyOf(permissionNames);
        if (!requested.isEmpty()) {
            var combined = new java.util.HashSet<>(requested);
            combined.addAll(AdminAuthorizationService.ADMIN_PERMISSIONS);
            return accessContextService.resolveAuthenticatedUser(authentication)
                    .flatMap(identity -> unidadeContext.getUnidadeAtual()
                            .flatMap(unidadeId -> unidadeContext.getAtuacaoAtual()
                                    .map(colaboradorUnidadeAtuacaoId ->
                                            colaboradorUnidadeAtuacaoRepository.hasAnyPermissionAtAtuacao(
                                                    identity.keycloakId(),
                                                    unidadeId,
                                                    colaboradorUnidadeAtuacaoId,
                                                    combined))))
                    .orElse(false);
        }
        return false;
    }

    public boolean canCriarAtendimento(Authentication authentication) {
        return !tiposPermitidosCriarAtendimentoCodigos(authentication).isEmpty();
    }

    public boolean canGerirPermanencia(Authentication authentication) {
        return hasAny(authentication, Set.of(PERM_RECEPCAO_EXECUTAR, PERM_BUROCRATA_EXECUTAR));
    }

    public boolean unidadePermiteAgendamento() {
        return unidadeContext.getUnidadeAtual()
                .flatMap(unidadeConfigFluxoRepository::findById)
                .map(config -> config.isPermiteAgendamento())
                .orElse(false);
    }

    public boolean canCriarAtendimento(Authentication authentication, String tipoAtendimentoCodigo) {
        String codigo = TipoAtendimentoService.normalizeCodigo(tipoAtendimentoCodigo);
        if (codigo == null) {
            return false;
        }
        return tiposPermitidosCriarAtendimentoCodigos(authentication).contains(codigo);
    }

    public Set<String> tiposPermitidosCriarAtendimentoCodigos(Authentication authentication) {
        boolean canRecepcao = has(authentication, PERM_RECEPCAO_EXECUTAR);
        boolean canTriagem = has(authentication, PERM_TRIAGEM_EXECUTAR);

        return unidadeContext.getUnidadeAtual()
                .map(unidadeId -> resolveTiposPermitidosPorUnidade(unidadeId, canRecepcao, canTriagem))
                .orElseGet(() -> canRecepcao ? tiposAtivosGlobais() : Set.of());
    }

    private Set<String> resolveTiposPermitidosPorUnidade(Long unidadeId, boolean canRecepcao, boolean canTriagem) {
        PrimeiroPassoFluxo primeiroPasso = unidadeConfigFluxoRepository.findById(unidadeId)
                .map(config -> config.getPrimeiroPasso())
                .orElse(PrimeiroPassoFluxo.RECEPCAO);
        List<UnidadeTipoAtendimento> tiposConfigurados = unidadeTipoAtendimentoRepository
                .findByUnidadeIdAndAtivoTrueOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(unidadeId);
        if (tiposConfigurados.isEmpty()) {
            return canRecepcao ? tiposAtivosGlobais() : Set.of();
        }

        Set<String> tiposAtivosUnidade = new LinkedHashSet<>();
        for (UnidadeTipoAtendimento config : tiposConfigurados) {
            parseTipo(config).ifPresent(tiposAtivosUnidade::add);
        }
        if (tiposAtivosUnidade.isEmpty()) {
            return Set.of();
        }
        if (primeiroPasso == PrimeiroPassoFluxo.RECEPCAO) {
            return canRecepcao ? tiposAtivosUnidade : Set.of();
        }

        Set<String> permitidos = new LinkedHashSet<>();
        if (canTriagem) {
            permitidos.addAll(tiposAtivosUnidade);
        }
        if (canRecepcao) {
            for (UnidadeTipoAtendimento config : tiposConfigurados) {
                if (config.isTriagemObrigatoria()) {
                    continue;
                }
                parseTipo(config).ifPresent(permitidos::add);
            }
        }
        return permitidos;
    }

    private java.util.Optional<String> parseTipo(UnidadeTipoAtendimento config) {
        if (config == null || config.getTipoAtendimento() == null || config.getTipoAtendimento().getCodigo() == null) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.ofNullable(TipoAtendimentoService.normalizeCodigo(config.getTipoAtendimento().getCodigo()));
    }

    private Set<String> tiposAtivosGlobais() {
        return tipoAtendimentoService.listarCodigosAtivosPorUnidadeOuGlobal(null);
    }
}
