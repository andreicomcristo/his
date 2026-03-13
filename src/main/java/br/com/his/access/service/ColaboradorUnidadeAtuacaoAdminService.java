package br.com.his.access.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.dto.ColaboradorUnidadeAtuacaoForm;
import br.com.his.access.model.ColaboradorUnidadeAtuacao;
import br.com.his.access.model.ColaboradorUnidadeVinculo;
import br.com.his.access.model.FuncaoUnidade;
import br.com.his.access.model.Perfil;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;
import br.com.his.access.repository.ColaboradorUnidadeVinculoRepository;
import br.com.his.access.repository.FuncaoUnidadeRepository;
import br.com.his.access.repository.PerfilRepository;
import br.com.his.care.scheduling.model.Especialidade;
import br.com.his.care.scheduling.repository.EspecialidadeRepository;

@Service
public class ColaboradorUnidadeAtuacaoAdminService {

    private final ColaboradorUnidadeAtuacaoRepository repository;
    private final ColaboradorUnidadeVinculoRepository vinculoRepository;
    private final FuncaoUnidadeRepository funcaoUnidadeRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PerfilRepository perfilRepository;

    public ColaboradorUnidadeAtuacaoAdminService(ColaboradorUnidadeAtuacaoRepository repository,
                                                 ColaboradorUnidadeVinculoRepository vinculoRepository,
                                                 FuncaoUnidadeRepository funcaoUnidadeRepository,
                                                 EspecialidadeRepository especialidadeRepository,
                                                 PerfilRepository perfilRepository) {
        this.repository = repository;
        this.vinculoRepository = vinculoRepository;
        this.funcaoUnidadeRepository = funcaoUnidadeRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.perfilRepository = perfilRepository;
    }

    @Transactional(readOnly = true)
    public List<ColaboradorUnidadeAtuacao> listar(String q, Boolean ativo) {
        String filtro = normalize(q);
        if (filtro == null) {
            return ativo == null
                    ? repository.findAllComDetalhesOrderByContextoAsc()
                    : repository.findByAtivoComDetalhesOrderByContextoAsc(ativo);
        }
        return ativo == null
                ? repository.listarPorBusca(filtro)
                : repository.listarPorFiltroComBusca(ativo, filtro);
    }

    @Transactional(readOnly = true)
    public ColaboradorUnidadeAtuacao buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atuacao nao encontrada"));
    }

    @Transactional
    public ColaboradorUnidadeAtuacao criar(ColaboradorUnidadeAtuacaoForm form) {
        validarVigencia(form.getInicioVigencia(), form.getFimVigencia());
        ColaboradorUnidadeAtuacao item = new ColaboradorUnidadeAtuacao();
        apply(item, form, null);
        return repository.save(item);
    }

    @Transactional
    public ColaboradorUnidadeAtuacao atualizar(Long id, ColaboradorUnidadeAtuacaoForm form) {
        validarVigencia(form.getInicioVigencia(), form.getFimVigencia());
        ColaboradorUnidadeAtuacao item = buscar(id);
        apply(item, form, id);
        return repository.save(item);
    }

    @Transactional
    public void excluir(Long id) {
        ColaboradorUnidadeAtuacao item = buscar(id);
        try {
            repository.delete(item);
            repository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nao foi possivel excluir: atuacao em uso por registros operacionais");
        }
    }

    @Transactional(readOnly = true)
    public ColaboradorUnidadeAtuacaoForm toForm(ColaboradorUnidadeAtuacao item) {
        ColaboradorUnidadeAtuacaoForm form = new ColaboradorUnidadeAtuacaoForm();
        form.setColaboradorUnidadeVinculoId(item.getColaboradorUnidadeVinculo().getId());
        form.setFuncaoUnidadeId(item.getFuncaoUnidade().getId());
        form.setEspecialidadeId(item.getEspecialidade() == null ? null : item.getEspecialidade().getId());
        form.setPerfilId(item.getPerfil().getId());
        form.setInicioVigencia(item.getInicioVigencia());
        form.setFimVigencia(item.getFimVigencia());
        form.setAtivo(item.isAtivo());
        return form;
    }

    private void apply(ColaboradorUnidadeAtuacao item, ColaboradorUnidadeAtuacaoForm form, Long idIgnorar) {
        ColaboradorUnidadeVinculo vinculo = resolveVinculo(form.getColaboradorUnidadeVinculoId());
        FuncaoUnidade funcao = resolveFuncao(form.getFuncaoUnidadeId());
        Perfil perfil = resolvePerfil(form.getPerfilId());

        Especialidade especialidade = resolveEspecialidade(form.getEspecialidadeId());
        if (funcao.isRequerEspecialidade() && especialidade == null) {
            throw new IllegalArgumentException("A funcao selecionada exige especialidade");
        }
        if (!funcao.isRequerEspecialidade()) {
            especialidade = null;
        }

        validarContextoDuplicado(vinculo.getId(), funcao.getId(), especialidade == null ? null : especialidade.getId(), perfil.getId(), idIgnorar);

        item.setColaboradorUnidadeVinculo(vinculo);
        item.setFuncaoUnidade(funcao);
        item.setEspecialidade(especialidade);
        item.setPerfil(perfil);
        item.setInicioVigencia(form.getInicioVigencia());
        item.setFimVigencia(form.getFimVigencia());
        item.setAtivo(form.isAtivo());
    }

    private void validarContextoDuplicado(Long vinculoId,
                                          Long funcaoId,
                                          Long especialidadeId,
                                          Long perfilId,
                                          Long idIgnorar) {
        boolean existe = especialidadeId == null
                ? repository.existsContextoSemEspecialidade(vinculoId, funcaoId, perfilId, idIgnorar)
                : repository.existsContextoComEspecialidade(vinculoId, funcaoId, especialidadeId, perfilId, idIgnorar);
        if (existe) {
            throw new IllegalArgumentException("Ja existe atuacao com este mesmo contexto");
        }
    }

    private ColaboradorUnidadeVinculo resolveVinculo(Long vinculoId) {
        return vinculoRepository.findById(vinculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vinculo de unidade nao encontrado"));
    }

    private FuncaoUnidade resolveFuncao(Long funcaoId) {
        return funcaoUnidadeRepository.findById(funcaoId)
                .orElseThrow(() -> new IllegalArgumentException("Funcao de unidade nao encontrada"));
    }

    private Especialidade resolveEspecialidade(Long especialidadeId) {
        if (especialidadeId == null) {
            return null;
        }
        return especialidadeRepository.findById(especialidadeId)
                .orElseThrow(() -> new IllegalArgumentException("Especialidade nao encontrada"));
    }

    private Perfil resolvePerfil(Long perfilId) {
        return perfilRepository.findById(perfilId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil nao encontrado"));
    }

    private static void validarVigencia(java.time.LocalDate inicio, java.time.LocalDate fim) {
        if (inicio != null && fim != null && fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Fim da vigencia nao pode ser anterior ao inicio");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
