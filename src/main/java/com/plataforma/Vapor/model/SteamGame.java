package com.plataforma.Vapor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "steam_games")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamGame {

    @Id
    private Long appId;

    @NotBlank
    private String name;

}