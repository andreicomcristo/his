package br.com.his.access.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class KeycloakAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private static final String KC_ACTION_PARAM = "kc_action";
    private static final String PROMPT_PARAM = "prompt";
    private static final String PROMPT_LOGIN = "login";

    private final OAuth2AuthorizationRequestResolver delegate;

    public KeycloakAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository,
                                                String authorizationRequestBaseUri) {
        this.delegate = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, authorizationRequestBaseUri);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customize(delegate.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customize(delegate.resolve(request, clientRegistrationId), request);
    }

    private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest original, HttpServletRequest request) {
        if (original == null) {
            return null;
        }

        Map<String, Object> parameters = new LinkedHashMap<>(original.getAdditionalParameters());
        parameters.put(PROMPT_PARAM, PROMPT_LOGIN);

        String kcAction = request.getParameter(KC_ACTION_PARAM);
        if (StringUtils.hasText(kcAction)) {
            parameters.put(KC_ACTION_PARAM, kcAction.trim());
        }

        return OAuth2AuthorizationRequest.from(original)
                .additionalParameters(parameters)
                .build();
    }
}
