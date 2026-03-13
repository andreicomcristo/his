package br.com.his.access.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Perfil;
import br.com.his.access.model.PerfilPermissao;
import br.com.his.access.model.PerfilPermissaoId;
import br.com.his.access.model.Permissao;
import br.com.his.access.repository.ColaboradorUnidadeAtuacaoRepository;
import br.com.his.access.repository.PerfilPermissaoRepository;
import br.com.his.access.repository.PerfilRepository;
import br.com.his.access.repository.PermissaoRepository;
import br.com.his.access.dto.PerfilForm;

@Service
public class PerfilAdminService {

    private final PerfilRepository perfilRepository;
    private final PermissaoRepository permissaoRepository;
    private final PerfilPermissaoRepository perfilPermissaoRepository;
    private final ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository;

    public PerfilAdminService(PerfilRepository perfilRepository,
                              PermissaoRepository permissaoRepository,
                              PerfilPermissaoRepository perfilPermissaoRepository,
                              ColaboradorUnidadeAtuacaoRepository colaboradorUnidadeAtuacaoRepository) {
        this.perfilRepository = perfilRepository;
        this.permissaoRepository = permissaoRepository;
        this.perfilPermissaoRepository = perfilPermissaoRepository;
        this.colaboradorUnidadeAtuacaoRepository = colaboradorUnidadeAtuacaoRepository;
    }

    @Transactional(readOnly = true)
    public List<Perfil> listar(String filtro) {
        String normalized = normalize(filtro);
        if (normalized == null) {
            return perfilRepository.findAllByOrderByNomeAsc();
        }
        return perfilRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(normalized);
    }

    @Transactional(readOnly = true)
    public Perfil buscarPorId(Long id) {
        return perfilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil nao encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public List<Permissao> listarPermissoes() {
        return permissaoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Set<Long> listarIdsPermissoesPerfil(Long perfilId) {
        return new HashSet<>(perfilPermissaoRepository.findPermissaoIdsByPerfilId(perfilId));
    }

    @Transactional
    public Perfil criar(PerfilForm form) {
        Perfil perfil = new Perfil();
        perfil.setNome(normalizeUpper(form.getNome()));
        return salvar(perfil);
    }

    @Transactional
    public Perfil atualizar(Long id, PerfilForm form) {
        Perfil perfil = buscarPorId(id);
        perfil.setNome(normalizeUpper(form.getNome()));
        return salvar(perfil);
    }

    @Transactional
    public void remover(Long id) {
        Perfil perfil = buscarPorId(id);
        if (colaboradorUnidadeAtuacaoRepository.countByPerfil(perfil) > 0) {
            throw new IllegalArgumentException("Perfil possui vinculos ativos/inativos e nao pode ser removido");
        }
        perfilPermissaoRepository.deleteByPerfilId(id);
        perfilRepository.delete(perfil);
    }

    @Transactional
    public void atualizarPermissoes(Long perfilId, List<Long> permissaoIds) {
        Perfil perfil = buscarPorId(perfilId);
        List<Long> ids = permissaoIds == null ? List.of() : permissaoIds.stream().distinct().toList();
        List<Permissao> permissoes = ids.isEmpty() ? List.of() : permissaoRepository.findByIdIn(ids);

        if (permissoes.size() != ids.size()) {
            throw new IllegalArgumentException("Uma ou mais permissoes informadas nao existem");
        }

        perfilPermissaoRepository.deleteByPerfilId(perfilId);

        for (Permissao permissao : permissoes) {
            PerfilPermissao pp = new PerfilPermissao();
            pp.setId(new PerfilPermissaoId(perfilId, permissao.getId()));
            pp.setPerfil(perfil);
            pp.setPermissao(permissao);
            perfilPermissaoRepository.save(pp);
        }
    }

    private Perfil salvar(Perfil perfil) {
        try {
            return perfilRepository.save(perfil);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Nome de perfil ja cadastrado");
        }
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeUpper(String value) {
        String normalized = normalize(value);
        return normalized == null ? null : normalized.toUpperCase();
    }
}
