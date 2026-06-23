package com.plataforma.Vapor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.plataforma.Vapor.config.SteamConfig;
import com.plataforma.Vapor.dto.SteamGameDTO;
import com.plataforma.Vapor.dto.SteamUserDTO;
import com.plataforma.Vapor.model.SteamGame;
import com.plataforma.Vapor.model.SteamUser;
import com.plataforma.Vapor.repository.SteamGameRepository;
import com.plataforma.Vapor.repository.SteamUserRepository;

@Service
public class SteamUserService {

    @Autowired
    @Qualifier("steamWebClient")
    private WebClient steamWebClient;
    
    @Autowired
    private SteamConfig steamConfig;

    @Autowired
    private SteamUserRepository steamUserRepository;

    @Autowired
    private SteamGameRepository steamGameRepository;

    public SteamUserDTO.PlayerDTO getFromSteam(Long steamId) {
        if (steamConfig.getApiKey() == null) {
            throw new RuntimeException("Steam API Key is missing");
        }

        SteamUserDTO apiUser = steamWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ISteamUser/GetPlayerSummaries/v0002/")
                        .queryParam("key", steamConfig.getApiKey())
                        .queryParam("steamids", steamId)
                        .build())
                .retrieve()
                .bodyToMono(SteamUserDTO.class)
                .block(); 

        if (apiUser == null || apiUser.getResponse() == null || apiUser.getResponse().getPlayers().isEmpty()) {
            throw new RuntimeException("User not found with ID: " + steamId);
        }

        return apiUser.getResponse().getPlayers().get(0);
    }

    public SteamUser registerSteamUser(Long steamId) {
        Long id = Long.valueOf(steamId);

        SteamUser existingUser = steamUserRepository.findById(id).orElse(null);
        if (existingUser != null) {
            return existingUser;
        }

        SteamUserDTO.PlayerDTO playerDto = getFromSteam(steamId);

        SteamUser newUser = new SteamUser();
        newUser.setId(id);
        newUser.setUsername(playerDto.getPersonaname());
        newUser.setConectionState(playerDto.getPersonastate());

        return steamUserRepository.save(newUser);
    }

    public SteamUser syncLibrary(Long steamId) {
        SteamUser steamUser = steamUserRepository.findById(steamId)
                .orElseThrow(() -> new RuntimeException("Steam user not registered on the platform"));

        SteamGameDTO apiResponse = steamWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/IPlayerService/GetOwnedGames/v0001/")
                        .queryParam("key", steamConfig.getApiKey())
                        .queryParam("steamid", steamId)
                        .queryParam("include_appinfo", true)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(SteamGameDTO.class)
                .block();

        if (apiResponse != null && apiResponse.getResponse() != null && apiResponse.getResponse().getGames() != null) {

            steamUser.getLibrary().clear();

            for (SteamGameDTO.GameDTO gameDto : apiResponse.getResponse().getGames()) {
                // Changed variable name 'juego' to 'game'
                SteamGame game = steamGameRepository.findById(gameDto.getAppId())
                        .orElseGet(() -> steamGameRepository.save(new SteamGame(gameDto.getAppId(), gameDto.getName())));
                steamUser.getLibrary().add(game);
            }
        }
        return steamUserRepository.save(steamUser);
    }

    public SteamUser updateSteamUser(Long steamId) {
        Long id = Long.valueOf(steamId);
    
        SteamUser existingUser = steamUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Steam user not found with ID: " + steamId));

        SteamUserDTO.PlayerDTO playerDto = getFromSteam(steamId);

        existingUser.setUsername(playerDto.getPersonaname());
        existingUser.setConectionState(playerDto.getPersonastate());

        return steamUserRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!steamUserRepository.existsById(id)) {
            throw new RuntimeException("No user found with ID " + id);
        }
        steamUserRepository.deleteById(id);
    }
}