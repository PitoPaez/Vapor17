package com.plataforma.Vapor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plataforma.Vapor.dto.SteamUserDTO;
import com.plataforma.Vapor.model.SteamUser;
import com.plataforma.Vapor.service.SteamUserService;

@RestController
@RequestMapping("/api/v1/steam")
public class SteamUserController {

   @Autowired
    public SteamUserService steamUserService;

    // buscar usuario por db de steam
    @GetMapping("/{steamId}")
    public ResponseEntity<?> getProfile(@PathVariable long steamId) {
        try {
            System.out.println("Request made to Service from Controller to find Steam profile with ID: " + steamId);
            SteamUserDTO.PlayerDTO profile = steamUserService.getFromSteam(steamId);
            System.out.println("Passed validation for finding profile within Service");
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            System.out.println("Validation error within Service when finding profile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            System.out.println("Request made to Service from Controller to list all users");
            List<SteamUser> users = steamUserService.getAllUsers();
            System.out.println("Passed validation for listing all users within Service");
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            System.out.println("Validation error within Service when listing users");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/register/{steamId}")
    public ResponseEntity<?> registerUser(@PathVariable Long steamId) {
        try {
            System.out.println("Request made to Service from Controller to register Steam user with ID: " + steamId);
            SteamUser newUser = steamUserService.registerSteamUser(steamId);
            System.out.println("Passed validations for adding user within Service");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            System.out.println("Error in Service validations when registering user");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/library/{steamId}")
    public ResponseEntity<?> syncLibrary(@PathVariable Long steamId) {
        try {
            System.out.println("Request made to Service from Controller to synchronize user library with ID: " + steamId);
            SteamUser updatedUser = steamUserService.syncLibrary(steamId);
            System.out.println("Passed validations for synchronizing user library within Service");
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.out.println("Validation error within Service when synchronizing library");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{steamId}")
    public ResponseEntity<?> updateUser(@PathVariable long steamId) {
        try {
            System.out.println("Request made to Service from Controller to update user data with steamId: " + steamId);
            SteamUser updatedUser = steamUserService.updateSteamUser(steamId);
            System.out.println("Passed validations for updating user within Service");
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            System.out.println("Validation error within Service when updating user");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            System.out.println("Request made to Service from Controller to delete user with ID: " + id);
            steamUserService.deleteUser(id);
            System.out.println("Passed validations for deleting user within Service");
            return ResponseEntity.ok("User with ID " + id + " has been successfully deleted!");
        } catch (RuntimeException e) {
            System.out.println("Validation error within Service when attempting to delete");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}