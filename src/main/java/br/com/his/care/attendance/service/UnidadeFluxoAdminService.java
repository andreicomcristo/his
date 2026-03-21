package br.com.his.care.attendance.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.care.attendance.dto.UnidadeTipoAtendimentoConfigForm;
import br.com.his.care.attendance.dto.UnidadeFluxoForm;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import br.com.his.care.attendance.model.TipoAtendimentoCadastro;
import br.com.his.care.attendance.model.UnidadeConfigFluxo;
import br.com.his.care.attendance.model.UnidadeTipoAtendimento;
import br.com.his.care.attendance.repository.TipoAtendimentoCadastroRepository;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.attendance.repository.UnidadeTipoAtendimentoRepository;

@Service
public class UnidadeFluxoAdminService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final TipoAtendimentoCadastroRepository tipoAtendimentoCadastroRepository;
    private final UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository;

    public UnidadeFluxoAdminService(UnidadeRepository unidadeRepository,
                                    UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                    TipoAtendimentoCadastroRepository tipoAtendimentoCadastroRepository,
                                    UnidadeTipoAtendimentoRepository unidadeTipoAtendimentoRepository) {
        this.unidadeRepository = unidadeRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.tipoAtendimentoCadastroRepository = tipoAtendimentoCadastroRepository;
        this.unidadeTipoAtendimentoRepository = unidadeTipoAtendimentoRepository;
    }

    @Transactional(readOnly = true)
    public UnidadeFluxoForm carregarFormulario(Long unidadeId) {
        UnidadeFluxoForm form = new UnidadeFluxoForm();
        UnidadeConfigFluxo config = unidadeConfigFluxoRepository.findById(unidadeId).orElse(null);

        form.setPrimeiroPasso(config == null ? PrimeiroPassoFluxo.RECEPCAO : config.getPrimeiroPasso());
        form.setExigeFichaParaMedico(config != null && config.isExigeFichaParaMedico());
        form.setPermiteAgendamento(config != null && config.isPermiteAgendamento());
        form.setTiposAtendimento(buildTiposAtendimentoConfig(unidadeId));
        return form;
    }

    @Transactional
    public void salvar(Long unidadeId, UnidadeFluxoForm form) {
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Unidade nao encontrada: " + unidadeId));

        UnidadeConfigFluxo config = unidadeConfigFluxoRepository.findById(unidadeId).orElseGet(() -> {
            UnidadeConfigFluxo novo = new UnidadeConfigFluxo();
            novo.setUnidadeId(unidadeId);
            novo.setUnidade(unidade);
            return novo;
        });
        config.setPrimeiroPasso(form.getPrimeiroPasso());
        config.setExigeFichaParaMedico(form.isExigeFichaParaMedico());
        config.setCriaEpisodioAutomatico(true);
        config.setPermiteAgendamento(form.isPermiteAgendamento());
        unidadeConfigFluxoRepository.save(config);
        salvarTiposAtendimento(unidade, form);
    }

    private List<UnidadeTipoAtendimentoConfigForm> buildTiposAtendimentoConfig(Long unidadeId) {
        Map<Long, UnidadeTipoAtendimento> configsExistentes = unidadeTipoAtendimentoRepository
                .findByUnidadeIdOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(unidadeId)
                .stream()
                .collect(Collectors.toMap(item -> item.getTipoAtendimento().getId(),
                        item -> item,
                        (a, b) -> a,
                        LinkedHashMap::new));
        List<UnidadeTipoAtendimentoConfigForm> resultado = new ArrayList<>();
        for (TipoAtendimentoCadastro tipo : tipoAtendimentoCadastroRepository.findAllByOrderByOrdemExibicaoAscDescricaoAsc()) {
            UnidadeTipoAtendimentoConfigForm item = new UnidadeTipoAtendimentoConfigForm();
            item.setTipoAtendimentoId(tipo.getId());
            item.setCodigo(tipo.getCodigo());
            item.setDescricao(tipo.getDescricao());

            UnidadeTipoAtendimento config = configsExistentes.get(tipo.getId());
            if (config == null) {
                item.setAtivo(tipo.isAtivo());
                item.setTriagemObrigatoria(false);
                item.setPassaConsultorio(defaultPassaConsultorio(tipo.getCodigo()));
                item.setPermiteAgendamento(defaultPermiteAgendamento(tipo.getCodigo()));
            } else {
                item.setAtivo(config.isAtivo() && tipo.isAtivo());
                item.setTriagemObrigatoria(config.isTriagemObrigatoria());
                item.setPassaConsultorio(config.isPassaConsultorio());
                item.setPermiteAgendamento(config.isPermiteAgendamento());
            }
            resultado.add(item);
        }
        return resultado;
    }

    private void salvarTiposAtendimento(Unidade unidade, UnidadeFluxoForm form) {
        List<UnidadeTipoAtendimentoConfigForm> submitted = form.getTiposAtendimento();
        if (submitted == null || submitted.isEmpty()) {
            throw new IllegalArgumentException("Nenhum tipo de atendimento informado");
        }
        Map<Long, TipoAtendimentoCadastro> tiposById = tipoAtendimentoCadastroRepository.findAllById(
                        submitted.stream()
                                .map(UnidadeTipoAtendimentoConfigForm::getTipoAtendimentoId)
                                .filter(id -> id != null)
                                .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(TipoAtendimentoCadastro::getId, item -> item));
        if (tiposById.size() != submitted.stream().map(UnidadeTipoAtendimentoConfigForm::getTipoAtendimentoId).collect(Collectors.toSet()).size()) {
            throw new IllegalArgumentException("Tipo de atendimento invalido na configuracao");
        }

        Map<Long, UnidadeTipoAtendimento> existentes = unidadeTipoAtendimentoRepository
                .findByUnidadeIdOrderByTipoAtendimentoOrdemExibicaoAscTipoAtendimentoDescricaoAsc(unidade.getId())
                .stream()
                .collect(Collectors.toMap(item -> item.getTipoAtendimento().getId(), item -> item));

        for (UnidadeTipoAtendimentoConfigForm item : submitted) {
            if (item.getTipoAtendimentoId() == null) {
                continue;
            }
            TipoAtendimentoCadastro tipo = tiposById.get(item.getTipoAtendimentoId());
            UnidadeTipoAtendimento config = existentes.remove(item.getTipoAtendimentoId());
            if (config == null) {
                config = new UnidadeTipoAtendimento();
                config.setUnidade(unidade);
                config.setTipoAtendimento(tipo);
            }
            config.setAtivo(tipo.isAtivo() && item.isAtivo());
            config.setTriagemObrigatoria(item.isTriagemObrigatoria());
            config.setPassaConsultorio(item.isPassaConsultorio());
            config.setPermiteAgendamento(item.isPermiteAgendamento());
            unidadeTipoAtendimentoRepository.save(config);
        }

        for (UnidadeTipoAtendimento remanescente : existentes.values()) {
            remanescente.setAtivo(false);
            unidadeTipoAtendimentoRepository.save(remanescente);
        }
    }

    private boolean defaultPassaConsultorio(String codigo) {
        return !"PROCEDIMENTO".equalsIgnoreCase(codigo);
    }

    private boolean defaultPermiteAgendamento(String codigo) {
        return Set.of("AMBULATORIAL", "PROCEDIMENTO").contains(codigo == null ? null : codigo.toUpperCase());
    }
}
