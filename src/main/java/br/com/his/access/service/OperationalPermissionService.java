package br.com.his.access.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import br.com.his.access.context.UnidadeContext;
import br.com.his.assistencial.model.PrimeiroPassoFluxo;
import br.com.his.assistencial.model.TipoAtendimento;
import br.com.his.assistencial.model.UnidadeRegraTriagem;
import br.com.his.assistencial.repository.UnidadeConfigFluxoRepository;
import br.com.his.assistencial.repository.UnidadeRegraTriagemRepository;
import br.com.his.access.repository.UsuarioUnidadePerfilRepository;

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
    private final UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final UnidadeRegraTriagemRepository unidadeRegraTriagemRepository;

    public OperationalPermissionService(AccessContextService accessContextService,
                                        UnidadeContext unidadeContext,
                                        UsuarioUnidadePerfilRepository usuarioUnidadePerfilRepository,
                                        UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                        UnidadeRegraTriagemRepository unidadeRegraTriagemRepository) {
        this.accessContextService = accessContextService;
        this.unidadeContext = unidadeContext;
        this.usuarioUnidadePerfilRepository = usuarioUnidadePerfilRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.unidadeRegraTriagemRepository = unidadeRegraTriagemRepository;
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
                    .flatMap(identity -> unidadeContext.getUnidadeAtual().map(unidadeId ->
                            usuarioUnidadePerfilRepository.hasAnyPermissionAtUnidade(
                                    identity.keycloakId(), unidadeId, combined)))
                    .orElse(false);
        }
        return false;
    }

    public boolean canCriarAtendimento(Authentication authentication) {
        return !tiposPermitidosCriarAtendimento(authentication).isEmpty();
    }

    public boolean canGerirPermanencia(Authentication authentication) {
        return hasAny(authentication, Set.of(PERM_RECEPCAO_EXECUTAR, PERM_BUROCRATA_EXECUTAR));
    }

    public boolean canCriarAtendimento(Authentication authentication, TipoAtendimento tipoAtendimento) {
        if (tipoAtendimento == null) {
            return false;
        }
        return tiposPermitidosCriarAtendimento(authentication).contains(tipoAtendimento);
    }

    public Set<TipoAtendimento> tiposPermitidosCriarAtendimento(Authentication authentication) {
        boolean canRecepcao = has(authentication, PERM_RECEPCAO_EXECUTAR);
        boolean canTriagem = has(authentication, PERM_TRIAGEM_EXECUTAR);

        return unidadeContext.getUnidadeAtual()
                .map(unidadeId -> resolveTiposPermitidosPorUnidade(unidadeId, canRecepcao, canTriagem))
                .orElseGet(() -> canRecepcao ? EnumSet.allOf(TipoAtendimento.class) : EnumSet.noneOf(TipoAtendimento.class));
    }

    private Set<TipoAtendimento> resolveTiposPermitidosPorUnidade(Long unidadeId, boolean canRecepcao, boolean canTriagem) {
        PrimeiroPassoFluxo primeiroPasso = unidadeConfigFluxoRepository.findById(unidadeId)
                .map(config -> config.getPrimeiroPasso())
                .orElse(PrimeiroPassoFluxo.RECEPCAO);

        if (primeiroPasso == PrimeiroPassoFluxo.RECEPCAO) {
            return canRecepcao ? EnumSet.allOf(TipoAtendimento.class) : EnumSet.noneOf(TipoAtendimento.class);
        }

        EnumSet<TipoAtendimento> permitidos = EnumSet.noneOf(TipoAtendimento.class);
        if (canTriagem) {
            permitidos.addAll(Arrays.asList(TipoAtendimento.values()));
        }
        if (canRecepcao) {
            Map<TipoAtendimento, Boolean> triagemObrigatoria = triagemObrigatoriaPorTipo(unidadeId);
            for (TipoAtendimento tipo : TipoAtendimento.values()) {
                boolean obrigatoria = triagemObrigatoria.getOrDefault(tipo, false);
                if (!obrigatoria) {
                    permitidos.add(tipo);
                }
            }
        }
        return permitidos;
    }

    private Map<TipoAtendimento, Boolean> triagemObrigatoriaPorTipo(Long unidadeId) {
        Map<TipoAtendimento, Boolean> result = new EnumMap<>(TipoAtendimento.class);
        List<UnidadeRegraTriagem> regras = unidadeRegraTriagemRepository.findByUnidadeId(unidadeId);
        for (UnidadeRegraTriagem regra : regras) {
            result.put(regra.getTipoAtendimento(), regra.isTriagemObrigatoria());
        }
        return result;
    }
}
