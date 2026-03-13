package br.com.his.access.api;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.his.access.model.Perfil;
import br.com.his.access.model.Unidade;
import br.com.his.access.model.Usuario;
import br.com.his.access.api.dto.PerfilAdminResponse;
import br.com.his.access.api.dto.UnidadeAdminResponse;
import br.com.his.access.api.dto.UsuarioAdminDetalheResponse;
import br.com.his.access.api.dto.UsuarioAdminResponse;

@Component
public class AdminApiMapper {

    public UnidadeAdminResponse toResponse(Unidade unidade) {
        UnidadeAdminResponse response = new UnidadeAdminResponse();
        response.setId(unidade.getId());
        response.setNome(unidade.getNome());
        response.setTipoUnidadeId(unidade.getTipoUnidade() == null ? null : unidade.getTipoUnidade().getId());
        response.setTipoUnidadeDescricao(unidade.getTipoUnidade() == null ? null : unidade.getTipoUnidade().getDescricao());
        response.setTipoEstabelecimento(unidade.getTipoEstabelecimento());
        response.setSigla(unidade.getSigla());
        response.setCnes(unidade.getCnes());
        response.setAtivo(unidade.isAtivo());
        return response;
    }

    public UsuarioAdminResponse toResponse(Usuario usuario) {
        UsuarioAdminResponse response = new UsuarioAdminResponse();
        response.setId(usuario.getId());
        response.setKeycloakId(usuario.getKeycloakId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setAtivo(usuario.isAtivo());
        return response;
    }

    public UsuarioAdminDetalheResponse toDetalheResponse(Usuario usuario) {
        UsuarioAdminDetalheResponse response = new UsuarioAdminDetalheResponse();
        response.setId(usuario.getId());
        response.setKeycloakId(usuario.getKeycloakId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setAtivo(usuario.isAtivo());
        return response;
    }

    public PerfilAdminResponse toResponse(Perfil perfil, List<Long> permissaoIds) {
        PerfilAdminResponse response = new PerfilAdminResponse();
        response.setId(perfil.getId());
        response.setNome(perfil.getNome());
        response.setPermissaoIds(permissaoIds);
        return response;
    }
}
