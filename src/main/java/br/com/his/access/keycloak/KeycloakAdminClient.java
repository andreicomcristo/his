package br.com.his.access.keycloak;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClient;

@Component
public class KeycloakAdminClient {

    private final KeycloakAdminProperties properties;
    private final RestClient restClient;

    public KeycloakAdminClient(KeycloakAdminProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public String criarUsuario(String username,
                               String email,
                               String firstName,
                               String lastName,
                               String temporaryPassword,
                               boolean forceUpdatePassword) {
        if (!properties.isEnabled()) {
            throw new IllegalArgumentException("Integracao Keycloak Admin desabilitada");
        }

        String token = obterToken();

        Map<String, Object> payload = buildUserPayload(
                username, email, firstName, lastName, forceUpdatePassword);

        URI location;
        try {
            location = restClient.post()
                    .uri(adminUsersUrl())
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .getHeaders()
                    .getLocation();
        } catch (HttpClientErrorException.Conflict ex) {
            throw new IllegalArgumentException("Ja existe usuario no Keycloak com este username.", ex);
        } catch (HttpClientErrorException ex) {
            throw new IllegalArgumentException(
                    "Falha ao criar usuario no Keycloak: " + extrairMensagemErro(ex),
                    ex);
        }

        if (location == null) {
            throw new IllegalArgumentException("Falha ao criar usuario no Keycloak: Location nao retornada");
        }

        String userId = location.getPath().substring(location.getPath().lastIndexOf('/') + 1);
        if (temporaryPassword != null && !temporaryPassword.isBlank()) {
            setSenhaTemporaria(userId, temporaryPassword, true, token);
        }
        return userId;
    }

    public String provisionarOuRecriarUsuario(String username,
                                              String email,
                                              String firstName,
                                              String lastName,
                                              String temporaryPassword,
                                              boolean forceUpdatePassword) {
        if (!properties.isEnabled()) {
            throw new IllegalArgumentException("Integracao Keycloak Admin desabilitada");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username e obrigatorio para provisionar no Keycloak");
        }

        String token = obterToken();
        Optional<String> userIdOpt = buscarUserIdPorUsername(username, token);
        String userId;

        if (userIdOpt.isPresent()) {
            userId = userIdOpt.get();
            atualizarUsuario(userId, username, email, firstName, lastName, forceUpdatePassword, token);
        } else {
            Map<String, Object> payload = buildUserPayload(
                    username, email, firstName, lastName, forceUpdatePassword);
            URI location;
            try {
                location = restClient.post()
                        .uri(adminUsersUrl())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(payload)
                        .retrieve()
                        .toBodilessEntity()
                        .getHeaders()
                        .getLocation();
            } catch (HttpClientErrorException.Conflict ex) {
                throw new IllegalArgumentException("Ja existe usuario no Keycloak com este username.", ex);
            } catch (HttpClientErrorException ex) {
                throw new IllegalArgumentException(
                        "Falha ao criar usuario no Keycloak: " + extrairMensagemErro(ex),
                        ex);
            }
            if (location == null) {
                throw new IllegalArgumentException("Falha ao criar usuario no Keycloak: Location nao retornada");
            }
            userId = location.getPath().substring(location.getPath().lastIndexOf('/') + 1);
        }

        if (temporaryPassword != null && !temporaryPassword.isBlank()) {
            setSenhaTemporaria(userId, temporaryPassword, true, token);
        }
        return userId;
    }

    private void setSenhaTemporaria(String userId, String senha, boolean temporary, String token) {
        Map<String, Object> body = Map.of(
                "type", "password",
                "value", senha,
                "temporary", temporary);

        restClient.put()
                .uri(adminUsersUrl() + "/" + userId + "/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    @SuppressWarnings("unchecked")
    private Optional<String> buscarUserIdPorUsername(String username, String token) {
        String url = UriComponentsBuilder.fromHttpUrl(adminUsersUrl())
                .queryParam("username", username)
                .queryParam("exact", true)
                .toUriString();

        List<Map<String, Object>> users = restClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(List.class);

        if (users == null || users.isEmpty()) {
            return Optional.empty();
        }
        Object id = users.getFirst().get("id");
        return id == null ? Optional.empty() : Optional.of(String.valueOf(id));
    }

    private void atualizarUsuario(String userId,
                                  String username,
                                  String email,
                                  String firstName,
                                  String lastName,
                                  boolean forceUpdatePassword,
                                  String token) {
        Map<String, Object> payload = buildUserPayload(
                username, email, firstName, lastName, forceUpdatePassword);

        restClient.put()
                .uri(adminUsersUrl() + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    private static Map<String, Object> buildUserPayload(String username,
                                                        String email,
                                                        String firstName,
                                                        String lastName,
                                                        boolean forceUpdatePassword) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("enabled", true);
        if (email != null && !email.isBlank()) {
            payload.put("email", email);
        }
        if (firstName != null && !firstName.isBlank()) {
            payload.put("firstName", firstName);
        }
        if (lastName != null && !lastName.isBlank()) {
            payload.put("lastName", lastName);
        }
        payload.put("requiredActions", forceUpdatePassword ? List.of("UPDATE_PASSWORD") : List.of());
        return payload;
    }

    @SuppressWarnings("unchecked")
    private String obterToken() {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", properties.getClientId());
            body.add("client_secret", properties.getClientSecret());

            Map<String, Object> response = restClient.post()
                    .uri(tokenUrl())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("access_token") == null) {
                throw new IllegalArgumentException("Falha ao autenticar no Keycloak Admin API");
            }
            return String.valueOf(response.get("access_token"));
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new IllegalArgumentException(
                    "Keycloak recusou client_credentials. Habilite 'Service accounts roles' no client e confira client secret.",
                    ex);
        } catch (HttpClientErrorException ex) {
            throw new IllegalArgumentException(
                    "Falha na autenticacao com Keycloak Admin API: " + ex.getStatusCode(),
                    ex);
        }
    }

    private String tokenUrl() {
        return normalize(properties.getServerUrl())
                + "/realms/" + properties.getRealm() + "/protocol/openid-connect/token";
    }

    private String adminUsersUrl() {
        return normalize(properties.getServerUrl())
                + "/admin/realms/" + properties.getRealm() + "/users";
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private static String extrairMensagemErro(HttpClientErrorException ex) {
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            return ex.getStatusCode().toString();
        }
        int idx = body.indexOf("\"errorMessage\"");
        if (idx >= 0) {
            int start = body.indexOf(':', idx);
            int firstQuote = body.indexOf('"', start + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);
            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return body.substring(firstQuote + 1, secondQuote);
            }
        }
        return body;
    }
}
