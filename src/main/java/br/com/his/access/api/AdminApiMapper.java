package br.com.his.access.api;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.his.access.model.Perfil;
import br.com.his.access.model.Unidade;
import br.com.his.access.model.Usuario;
import br.com.his.access.model.UsuarioUnidadePerfil;
import br.com.his.access.api.dto.PerfilAdminResponse;
import br.com.his.access.api.dto.UnidadeAdminResponse;
import br.com.his.access.api.dto.UsuarioAdminDetalheResponse;
import br.com.his.access.api.dto.UsuarioAdminResponse;
import br.com.his.access.api.dto.UsuarioVinculoResponse;

@Component
public class AdminApiMapper {

    public UnidadeAdminResponse toResponse(Unidade unidade) {
        UnidadeAdminResponse response = new UnidadeAdminResponse();
        response.setId(unidade.getId());
        response.setNome(unidade.getNome());
        response.setTipoEstabelecimento(unidade.getTipoEstabelecimento());
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

    public UsuarioAdminDetalheResponse toDetalheResponse(Usuario usuario, List<UsuarioUnidadePerfil> vinculos) {
        UsuarioAdminDetalheResponse response = new UsuarioAdminDetalheResponse();
        response.setId(usuario.getId());
        response.setKeycloakId(usuario.getKeycloakId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setAtivo(usuario.isAtivo());
        response.setVinculos(vinculos.stream().map(this::toResponse).toList());
        return response;
    }

    public UsuarioVinculoResponse toResponse(UsuarioUnidadePerfil vinculo) {
        UsuarioVinculoResponse response = new UsuarioVinculoResponse();
        response.setId(vinculo.getId());
        response.setAtivo(vinculo.isAtivo());
        response.setUnidadeId(vinculo.getUnidade().getId());
        response.setUnidadeNome(vinculo.getUnidade().getNome());
        response.setPerfilId(vinculo.getPerfil().getId());
        response.setPerfilNome(vinculo.getPerfil().getNome());
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
