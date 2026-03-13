package br.com.his.care.attendance.service;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.care.attendance.dto.UnidadeFluxoForm;
import br.com.his.care.attendance.model.PrimeiroPassoFluxo;
import br.com.his.care.attendance.model.TipoAtendimento;
import br.com.his.care.attendance.model.UnidadeConfigFluxo;
import br.com.his.care.triage.model.UnidadeRegraTriagem;
import br.com.his.care.attendance.repository.UnidadeConfigFluxoRepository;
import br.com.his.care.triage.repository.UnidadeRegraTriagemRepository;

@Service
public class UnidadeFluxoAdminService {

    private final UnidadeRepository unidadeRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;
    private final UnidadeRegraTriagemRepository unidadeRegraTriagemRepository;

    public UnidadeFluxoAdminService(UnidadeRepository unidadeRepository,
                                    UnidadeConfigFluxoRepository unidadeConfigFluxoRepository,
                                    UnidadeRegraTriagemRepository unidadeRegraTriagemRepository) {
        this.unidadeRepository = unidadeRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
        this.unidadeRegraTriagemRepository = unidadeRegraTriagemRepository;
    }

    @Transactional(readOnly = true)
    public UnidadeFluxoForm carregarFormulario(Long unidadeId) {
        UnidadeFluxoForm form = new UnidadeFluxoForm();
        UnidadeConfigFluxo config = unidadeConfigFluxoRepository.findById(unidadeId).orElse(null);

        form.setPrimeiroPasso(config == null ? PrimeiroPassoFluxo.RECEPCAO : config.getPrimeiroPasso());
        form.setExigeFichaParaMedico(config != null && config.isExigeFichaParaMedico());
        form.setPermiteAgendamento(config != null && config.isPermiteAgendamento());

        Map<TipoAtendimento, Boolean> triagemPorTipo = new EnumMap<>(TipoAtendimento.class);
        for (TipoAtendimento tipo : TipoAtendimento.values()) {
            boolean obrigatoria = unidadeRegraTriagemRepository.findByUnidadeIdAndTipoAtendimento(unidadeId, tipo)
                    .map(UnidadeRegraTriagem::isTriagemObrigatoria)
                    .orElse(false);
            triagemPorTipo.put(tipo, obrigatoria);
        }

        form.setTriagemObrigatoriaUrgencia(triagemPorTipo.get(TipoAtendimento.URGENCIA));
        form.setTriagemObrigatoriaAmbulatorial(triagemPorTipo.get(TipoAtendimento.AMBULATORIAL));
        form.setTriagemObrigatoriaInternacaoDireta(triagemPorTipo.get(TipoAtendimento.INTERNACAO_DIRETA));
        form.setTriagemObrigatoriaProcedimento(triagemPorTipo.get(TipoAtendimento.PROCEDIMENTO));
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

        salvarRegra(unidade, TipoAtendimento.URGENCIA, form.isTriagemObrigatoriaUrgencia());
        salvarRegra(unidade, TipoAtendimento.AMBULATORIAL, form.isTriagemObrigatoriaAmbulatorial());
        salvarRegra(unidade, TipoAtendimento.INTERNACAO_DIRETA, form.isTriagemObrigatoriaInternacaoDireta());
        salvarRegra(unidade, TipoAtendimento.PROCEDIMENTO, form.isTriagemObrigatoriaProcedimento());
    }

    private void salvarRegra(Unidade unidade, TipoAtendimento tipo, boolean obrigatoria) {
        UnidadeRegraTriagem regra = unidadeRegraTriagemRepository
                .findByUnidadeIdAndTipoAtendimento(unidade.getId(), tipo)
                .orElseGet(UnidadeRegraTriagem::new);
        regra.setUnidade(unidade);
        regra.setTipoAtendimento(tipo);
        regra.setTriagemObrigatoria(obrigatoria);
        unidadeRegraTriagemRepository.save(regra);
    }
}
