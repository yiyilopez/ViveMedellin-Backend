package com.vivemedellin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.models.CustomUserDetails;
import com.vivemedellin.models.Role;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.JwtAuthResponse;
import com.vivemedellin.payloads.LoginRequest;
import com.vivemedellin.security.CustomUserDetailService; //NUEVO
import com.vivemedellin.services.UserService;
import com.vivemedellin.filters.JwtAuthenticationFilter;  //NUEVO
import com.vivemedellin.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    void shouldAuthenticateUser_whenValidCredentialsProvided() throws Exception {
        // Arrange
        String email = "usuario@test.com";
        String password = "password123";
        String expectedToken = "jwt-token-12345";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(email);
        loginRequest.setPassword(password);

        // Crear usuario de prueba
        User testUser = new User("Usuario Test", email, "encodedPassword", "Soy un usuario de prueba");
        testUser.setId(1);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        testUser.setRoles(roles);

        UserDetails userDetails = new CustomUserDetails(testUser);
        
        // Mock de Authentication
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        // Configurar mocks
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.loadUserByUsername(eq(email)))
            .thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class)))
            .thenReturn(expectedToken);

        // Act & Assert
        String response = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value(expectedToken))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verificación adicional: parsear respuesta y verificar estructura
        JwtAuthResponse authResponse = objectMapper.readValue(response, JwtAuthResponse.class);
        assertNotNull(authResponse, "La respuesta no debería ser nula");
        assertEquals("Login successful", authResponse.getMessage(), 
            "El mensaje debería ser 'Login successful'");
        assertNotNull(authResponse.getToken(), "El token no debería ser nulo");
        assertEquals(expectedToken, authResponse.getToken(), 
            "El token debería coincidir con el esperado");
    }
}
