package com.plataforma.Vapor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plataforma.Vapor.dto.SteamUserDTO;
import com.plataforma.Vapor.service.SteamUserService;

@RestController
@RequestMapping("/Steam")
public class SteamUserController {
     @Autowired
    public SteamUserService steamUserService;

    @GetMapping("/{steamId}")
    public ResponseEntity<?> getPerfil(@PathVariable String steamId) {
        try {
            SteamUserDTO.PlayerDTO perfil = steamUserService.BuscarUsuarioPorID(steamId);
            return ResponseEntity.ok(perfil);
            
        } catch (RuntimeException e) {
            System.out.println("Error de validacion dentro del Service");
            return ResponseEntity.status(500).body(e.getMessage());
        }

    }
}
