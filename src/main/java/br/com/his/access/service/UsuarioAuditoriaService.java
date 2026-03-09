package br.com.his.access.service;

import java.util.Optional;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.his.access.model.Usuario;
import br.com.his.access.repository.UsuarioRepository;

@Service
public class UsuarioAuditoriaService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAuditoriaService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> usuarioAtual() {
        return principalAtual()
                .flatMap(this::buscarPorPrincipal);
    }

    @Transactional(readOnly = true)
    public String usernameAtualOuSistema() {
        return usuarioAtual()
                .map(Usuario::getUsername)
                .filter(v -> v != null && !v.isBlank())
                .orElse("sistema");
    }

    @Transactional(readOnly = true)
    public Usuario usuarioPorPrincipalOuNull(String principal) {
        return buscarPorPrincipal(principal).orElse(null);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorPrincipal(String principal) {
        if (principal == null || principal.isBlank()) {
            return Optional.empty();
        }
        return usuarioRepository.findByKeycloakId(principal)
                .or(() -> usuarioRepository.findByUsername(principal));
    }

    private Optional<String> principalAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(authentication.getName());
    }
}
