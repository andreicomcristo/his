package br.com.his.access.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcUnitConfig implements WebMvcConfigurer {

    private final OperationalUnitInterceptor operationalUnitInterceptor;
    private final AdminAccessInterceptor adminAccessInterceptor;

    public WebMvcUnitConfig(OperationalUnitInterceptor operationalUnitInterceptor,
                            AdminAccessInterceptor adminAccessInterceptor) {
        this.operationalUnitInterceptor = operationalUnitInterceptor;
        this.adminAccessInterceptor = adminAccessInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(operationalUnitInterceptor)
                .addPathPatterns(
                        "/ui/**")
                .excludePathPatterns(
                        "/ui/admin/**",
                        "/ui/escolher-unidade",
                        "/ui/escolher-unidade/**",
                        "/ui/escolher-atuacao",
                        "/ui/escolher-atuacao/**",
                        "/ui/sem-unidade",
                        "/ui/sem-atuacao",
                        "/login/**",
                        "/oauth2/**",
                        "/error",
                        "/kero/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/webjars/**",
                        "/favicon.ico");

        registry.addInterceptor(adminAccessInterceptor)
                .addPathPatterns("/ui/admin/**", "/api/admin/**")
                .excludePathPatterns(
                        "/login/**",
                        "/oauth2/**",
                        "/error",
                        "/kero/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/webjars/**",
                        "/favicon.ico");
    }
}
