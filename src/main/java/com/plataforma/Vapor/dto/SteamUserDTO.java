package com.plataforma.Vapor.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamUserDTO {
    private ResponseData response;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseData {
        private List<PlayerDTO> players;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerDTO {
        @JsonProperty("steamid")
        private Long steamid;
        
        @JsonProperty("personaname")
        private String personaname;

        @JsonProperty("realname")
        private String realname;
        
        private int personastate;

    }
}