package com.vivemedellin.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.payloads.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AuthController
 * Verifica la integración de autenticación y seguridad
 */
@DisplayName("Pruebas de Integración - AuthController")
public class AuthControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe cargar la página de autenticación correctamente")
    public void testShowAuthPageSuccess() throws Exception {
        mockMvc.perform(get("/auth")
                .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("auth"));
    }

    @Test
    @DisplayName("Debe retornar error 404 para rutas no autenticadas no existentes")
    public void testUnauthorizedRouteNotFound() throws Exception {
        mockMvc.perform(get("/non-existent-route"))
                .andExpect(status().isNotFound());
    }
}
