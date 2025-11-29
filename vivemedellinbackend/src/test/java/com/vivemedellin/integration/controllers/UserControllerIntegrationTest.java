package com.vivemedellin.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.UserDto;
import com.vivemedellin.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para UserController
 * Verifica la integración completa del flujo de usuarios
 */
@DisplayName("Pruebas de Integración - UserController")
public class UserControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    public void setUp() {
        userRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe crear un nuevo usuario exitosamente")
    public void testCreateUserSuccess() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUserName("newuser");
        userDto.setUserEmail("newuser@example.com");
        userDto.setUserPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName", equalTo("newuser")))
                .andExpect(jsonPath("$.userEmail", equalTo("newuser@example.com")));
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    public void testGetAllUsersSuccess() throws Exception {
        // Crear usuarios de prueba
        User user1 = new User();
        user1.setUserName("user1");
        user1.setUserEmail("user1@example.com");
        user1.setUserPassword("password123");
        userRepo.save(user1);

        User user2 = new User();
        user2.setUserName("user2");
        user2.setUserEmail("user2@example.com");
        user2.setUserPassword("password123");
        userRepo.save(user2);

        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID")
    public void testGetUserByIdSuccess() throws Exception {
        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        mockMvc.perform(get("/api/users/{userId}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", equalTo("testuser")))
                .andExpect(jsonPath("$.userEmail", equalTo("test@example.com")));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el usuario no existe")
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe actualizar un usuario existente")
    public void testUpdateUserSuccess() throws Exception {
        User user = new User();
        user.setUserName("originaluser");
        user.setUserEmail("original@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        UserDto updateDto = new UserDto();
        updateDto.setUserName("updateduser");
        updateDto.setUserEmail("updated@example.com");

        mockMvc.perform(put("/api/users/{userId}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", equalTo("updateduser")))
                .andExpect(jsonPath("$.userEmail", equalTo("updated@example.com")));
    }

    @Test
    @DisplayName("Debe eliminar un usuario")
    public void testDeleteUserSuccess() throws Exception {
        User user = new User();
        user.setUserName("deleteme");
        user.setUserEmail("delete@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        mockMvc.perform(delete("/api/users/{userId}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/api/users/{userId}", savedUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe prevenir la creación de usuarios con email duplicado")
    public void testCreateUserWithDuplicateEmailFails() throws Exception {
        // Crear primer usuario
        User user = new User();
        user.setUserName("firstuser");
        user.setUserEmail("duplicate@example.com");
        user.setUserPassword("password123");
        userRepo.save(user);

        // Intentar crear segundo usuario con mismo email
        UserDto duplicateUserDto = new UserDto();
        duplicateUserDto.setUserName("seconduser");
        duplicateUserDto.setUserEmail("duplicate@example.com");
        duplicateUserDto.setUserPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateUserDto)))
                .andExpect(status().isBadRequest());
    }
}
