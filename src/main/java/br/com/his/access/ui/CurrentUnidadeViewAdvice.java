package br.com.his.access.ui;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.model.Unidade;
import br.com.his.access.repository.UnidadeRepository;
import br.com.his.assistencial.model.PrimeiroPassoFluxo;
import br.com.his.assistencial.repository.UnidadeConfigFluxoRepository;

@ControllerAdvice
public class CurrentUnidadeViewAdvice {

    private final UnidadeContext unidadeContext;
    private final UnidadeRepository unidadeRepository;
    private final UnidadeConfigFluxoRepository unidadeConfigFluxoRepository;

    public CurrentUnidadeViewAdvice(UnidadeContext unidadeContext,
                                    UnidadeRepository unidadeRepository,
                                    UnidadeConfigFluxoRepository unidadeConfigFluxoRepository) {
        this.unidadeContext = unidadeContext;
        this.unidadeRepository = unidadeRepository;
        this.unidadeConfigFluxoRepository = unidadeConfigFluxoRepository;
    }

    @ModelAttribute("currentUnidade")
    public UnidadeResumo currentUnidade() {
        return unidadeContext.getUnidadeAtual()
                .flatMap(unidadeRepository::findById)
                .map(u -> new UnidadeResumo(u.getId(), u.getNome(), u.getTipoEstabelecimento(), u.getCnes()))
                .orElse(null);
    }

    @ModelAttribute("currentPrimeiroPasso")
    public PrimeiroPassoFluxo currentPrimeiroPasso() {
        return unidadeContext.getUnidadeAtual()
                .flatMap(unidadeConfigFluxoRepository::findById)
                .map(config -> config.getPrimeiroPasso())
                .orElse(null);
    }

    @ModelAttribute("currentFluxoRecepcaoPrimeiro")
    public boolean currentFluxoRecepcaoPrimeiro() {
        return currentPrimeiroPasso() == PrimeiroPassoFluxo.RECEPCAO;
    }

    @ModelAttribute("currentFluxoTriagemPrimeiro")
    public boolean currentFluxoTriagemPrimeiro() {
        return currentPrimeiroPasso() == PrimeiroPassoFluxo.TRIAGEM;
    }

    public record UnidadeResumo(Long id, String nome, String tipoEstabelecimento, String cnes) {
    }
}
