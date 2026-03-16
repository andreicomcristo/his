package br.com.his.access.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            ObjectProvider<ClientRegistrationRepository> clientRegistrations)
            throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/",
                        "/login/**",
                        "/oauth2/**",
                        "/error",
                        "/kero/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/webjars/**",
                        "/favicon.ico")
                .permitAll()
                .anyRequest()
                .authenticated());

        if (clientRegistrations.getIfAvailable() != null) {
            ClientRegistrationRepository registrations = clientRegistrations.getIfAvailable();
            http.oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .authorizationEndpoint(authorization -> authorization
                            .authorizationRequestResolver(forceLoginAuthorizationRequestResolver(registrations)))
                    .defaultSuccessUrl("/ui/home", true));
        } else {
            http.formLogin(Customizer.withDefaults());
        }

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID"));

        return http.build();
    }

    private OAuth2AuthorizationRequestResolver forceLoginAuthorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new KeycloakAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");
    }
}
