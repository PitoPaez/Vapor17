package com.plataforma.Vapor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SteamGameDTO {
    private ResponseData response;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        @JsonProperty("game_count")
        private int gameCount;
        private List<GameDTO> games;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GameDTO {
        @JsonProperty("appid")
        private Long appId;
        private String name;
    }
}