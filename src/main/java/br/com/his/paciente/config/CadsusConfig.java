package br.com.his.paciente.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CadsusProperties.class)
public class CadsusConfig {
}
