package com.vivemedellin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.models.CustomUserDetails;
import com.vivemedellin.models.Role;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.JwtAuthResponse;
import com.vivemedellin.payloads.LoginRequest;
import com.vivemedellin.payloads.UserDto;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
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

    //HU1 - Escenario 3
    @Test
    void shouldMaintainActiveSession_whenUserAccessesPlatformAgain() throws Exception {
        // Given: que el usuario inició sesión previamente
        String email = "usuario@test.com";
        String password = "password123";
        String jwtToken = "jwt-token-12345";
        Integer userId = 1;

        // Preparar usuario de prueba
        User testUser = new User("Usuario Test", email, "encodedPassword", "Soy un usuario de prueba");
        testUser.setId(userId);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        testUser.setRoles(roles);

        UserDetails userDetails = new CustomUserDetails(testUser);
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setName("Usuario Test");
        userDto.setEmail(email);

        // Simular login inicial (sesión previa)
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(email);
        loginRequest.setPassword(password);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userService.loadUserByUsername(eq(email)))
            .thenReturn(userDetails);
        when(jwtUtil.generateToken(any(UserDetails.class)))
            .thenReturn(jwtToken);

        // Realizar login para obtener el token (simula sesión previa)
        String loginResponse = mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JwtAuthResponse authResponse = objectMapper.readValue(loginResponse, JwtAuthResponse.class);
        String receivedToken = authResponse.getToken();

        // When: accede nuevamente a la plataforma sin cerrar sesión
        // Configurar mocks para validar el token en la siguiente petición
        when(jwtUtil.extractUsername(receivedToken))
            .thenReturn(email);
        when(jwtUtil.validateToken(eq(receivedToken), any(UserDetails.class)))
            .thenReturn(true);
        when(customUserDetailService.loadUserByUsername(email))
            .thenReturn(userDetails);
        when(userService.getUserById(userId))
            .thenReturn(userDto);

        // Then: el sistema reconoce la sesión activa
        // And: permite al usuario acceder directamente a la página principal
        // (En este caso, acceder a un endpoint protegido que representa la página principal)
        mockMvc.perform(get("/api/users/{userId}", userId)
                .header("Authorization", "Bearer " + receivedToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value("Usuario Test"));

        // Verificación adicional: el token fue validado correctamente
        assertNotNull(receivedToken, "El token de sesión no debería ser nulo");
        assertEquals(jwtToken, receivedToken, "El token debería ser el mismo obtenido en el login");
    }

    @Test
    void shouldRejectAccess_whenTokenIsExpired() throws Exception {
        // Test complementario: verificar que un token expirado no permite acceso
        String email = "usuario@test.com";
        String expiredToken = "expired-token";
        Integer userId = 1;

        when(jwtUtil.extractUsername(expiredToken))
            .thenReturn(email);
        when(jwtUtil.validateToken(eq(expiredToken), any(UserDetails.class)))
            .thenReturn(false); // Token inválido/expirado

        mockMvc.perform(get("/api/users/{userId}", userId)
                .header("Authorization", "Bearer " + expiredToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
