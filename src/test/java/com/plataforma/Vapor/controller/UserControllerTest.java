package com.plataforma.Vapor.controller;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;

import com.plataforma.Vapor.dto.SteamUserDTO;
import com.plataforma.Vapor.model.SteamUser;
import com.plataforma.Vapor.service.SteamUserService;

class UserControllerTest {

    @Mock
    private SteamUserService steamUserService;

    @InjectMocks
    private SteamUserController steamUserController;

    @Test
    void getProfile_retorna200_cuandoIdExisteEnSteam() {
        // Vamos a verificar que el método getProfile del controlador funciona correctamente
        // Para ello crearemos un DTO de perfil simulado y definiremos el comportamiento del servicio
        long steamId = 76561198000000000L;
        SteamUserDTO.PlayerDTO mockProfile = new SteamUserDTO.PlayerDTO();
        mockProfile.setPersonaname("GamerPro2026");
        mockProfile.setPersonastate(1);

        // "Simulamos" el comportamiento del servicio (mock)
        // Cuando el controlador invoque getFromSteam con ese ID, Mockito devolverá el perfil al instante
        when(steamUserService.getFromSteam(steamId)).thenReturn(mockProfile);

        // Llamamos al método del controlador que queremos probar
        var respuesta = steamUserController.getProfile(steamId);

        // Verificamos varios aspectos de la respuesta para que el test sea completo
        assertNotNull(respuesta);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        
        var body = (SteamUserDTO.PlayerDTO) respuesta.getBody();
        assertNotNull(body);
        assertEquals("GamerPro2026", body.getPersonaname());
    }

    @Test
    void registerUser_retorna201_cuandoRegistroEsExitoso() {
        // Vamos a verificar que el método registerUser del controlador registra un usuario correctamente
        Long steamId = 76561198000000000L;
        // Usamos Collections.emptyList() en lugar de ArrayList para respetar la abstracción de tu modelo
        SteamUser mockUser = new SteamUser(steamId, "VaporPlayer", "Real Name", 1, Collections.emptyList());

        // Simulamos que el servicio registra y retorna exitosamente el nuevo SteamUser
        when(steamUserService.registerSteamUser(steamId)).thenReturn(mockUser);

        // Llamamos al método del controlador que queremos probar
        var respuesta = steamUserController.registerUser(steamId);

        // Verificamos que el estado HTTP sea 201 (CREATED) y los datos coincidan
        assertNotNull(respuesta);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        
        var body = (SteamUser) respuesta.getBody();
        assertNotNull(body);
        assertEquals("VaporPlayer", body.getUsername());
    }

    @Test
    void getAllUsers_retorna200_cuandoExistenUsuariosEnBd() {
        // Vamos a verificar que el método getAllUsers lista los usuarios guardados de forma correcta
        // Usamos List.of() para crear una lista inmutable rápidamente, evitando acoplarnos a ArrayList
        List<SteamUser> mockList = List.of(
            new SteamUser(1L, "UserOne", "Name One", 1, Collections.emptyList()),
            new SteamUser(2L, "UserTwo", "Name Two", 0, Collections.emptyList())
        );

        // Simulamos que el servicio devuelve la lista con los dos usuarios
        when(steamUserService.getAllUsers()).thenReturn(mockList);

        // Llamamos al método del controlador que queremos probar
        var respuesta = steamUserController.getAllUsers();

        // Validamos que devuelva un estado 200 OK y que la lista contenga los elementos esperados
        assertNotNull(respuesta);
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        
        var body = (List<?>) respuesta.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
    }

    @Test
    void deleteUser_retorna404_cuandoUsuarioNoExiste() {
        // Vamos a verificar el manejo de excepciones en el método deleteUser del controlador
        Long idInexistente = 999999L;

        // Simulamos que el servicio lanza una RuntimeException porque el usuario no existe en la BD
        // Como el método de servicio devuelve void, usamos doThrow
        doThrow(new RuntimeException("No user found with ID " + idInexistente))
                .when(steamUserService).deleteUser(idInexistente);

        // Llamamos al método del controlador que queremos probar
        var respuesta = steamUserController.deleteUser(idInexistente);

        // Validamos que el bloque try-catch del controlador capture la excepción
        // y responda correctamente con un estado 404 (NOT_FOUND) y el mensaje de error
        assertNotNull(respuesta);
        assertEquals(HttpStatus.NOT_FOUND, respuesta.getStatusCode());
        
        String mensajeError = respuesta.getBody();
        assertNotNull(mensajeError);
        assertTrue(mensajeError.contains("No user found with ID " + idInexistente));
    }
}
