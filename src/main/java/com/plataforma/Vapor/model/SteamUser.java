package com.plataforma.Vapor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios_steam")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamUser {

    @Id
    private Long steamId;
    
    private String apodo;
    
    private String nombreReal;

    private int personaState;

}