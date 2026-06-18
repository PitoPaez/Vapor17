package com.plataforma.Vapor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Configuration
@ConfigurationProperties(prefix = "steam")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamConfig {

    private String apiKey;
    private String apiUrl;

}