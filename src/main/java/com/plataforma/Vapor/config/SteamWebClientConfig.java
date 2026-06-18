package com.plataforma.Vapor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SteamWebClientConfig { 

    private final SteamConfig steamConfig;

    @Bean
    public WebClient steamWebClient() {
        return WebClient.builder()
                .baseUrl(steamConfig.getApiUrl())
                .build();
                
    }
}