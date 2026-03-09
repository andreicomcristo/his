package br.com.his.ui;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.his.access.context.UnidadeContext;
import br.com.his.access.repository.UnidadeRepository;

@ControllerAdvice
public class CurrentUserViewAdvice {

    private final UnidadeContext unidadeContext;
    private final UnidadeRepository unidadeRepository;

    public CurrentUserViewAdvice(UnidadeContext unidadeContext, UnidadeRepository unidadeRepository) {
        this.unidadeContext = unidadeContext;
        this.unidadeRepository = unidadeRepository;
    }

    @ModelAttribute("currentUser")
    public CurrentUser currentUser(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return CurrentUser.anonymous();
        }

        Map<String, Object> attributes = Map.of();
        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            attributes = oidcUser.getClaims();
        } else if (principal instanceof OAuth2User oauth2User) {
            attributes = oauth2User.getAttributes();
        }

        String displayName = firstNonBlank(
                str(attributes.get("name")),
                str(attributes.get("preferred_username")),
                authentication.getName(),
                "USER");

        String unidadeNome = unidadeContext.getUnidadeAtual()
                .flatMap(unidadeRepository::findById)
                .map(unidade -> unidade.getNome())
                .orElse(null);

        String company = firstNonBlank(
                unidadeNome,
                str(attributes.get("company")),
                str(attributes.get("organization")),
                "Unidade nao selecionada");

        String avatarUrl = firstNonBlank(
                str(attributes.get("picture")),
                "/kero/assets/images/avatars/1.jpg");

        String role = hasAdmin(authentication.getAuthorities()) ? "ADMIN" : "USER";

        return new CurrentUser(true, displayName.toUpperCase(), company, role, avatarUrl);
    }

    private static boolean hasAdmin(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> "ROLE_ADMIN".equalsIgnoreCase(a) || "ADMIN".equalsIgnoreCase(a));
    }

    private static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    public record CurrentUser(boolean authenticated, String name, String company, String role, String avatarUrl) {
        static CurrentUser anonymous() {
            return new CurrentUser(false, "USER", "Unidade nao selecionada", "USER", "/kero/assets/images/avatars/1.jpg");
        }
    }
}
