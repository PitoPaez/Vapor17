package com.plataforma.Vapor.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Steam_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SteamUser {

    @Id
    private Long id;
    
    @NotBlank
    private String username;
    
    private String realname;

    @NotNull
    private int conectionState;

}