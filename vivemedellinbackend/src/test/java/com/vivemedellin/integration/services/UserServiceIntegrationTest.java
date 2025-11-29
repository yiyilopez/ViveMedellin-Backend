package com.vivemedellin.integration.services;

import com.vivemedellin.models.User;
import com.vivemedellin.payloads.UserDto;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para UserService
 * Verifica la lógica de negocio de usuarios integrada con la base de datos
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración - UserService")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    public void setUp() {
        userRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe crear un usuario exitosamente")
    public void testCreateUserSuccess() {
        UserDto userDto = new UserDto();
        userDto.setUserName("newuser");
        userDto.setUserEmail("newuser@example.com");
        userDto.setUserPassword("password123");

        UserDto createdUser = userService.createUser(userDto);

        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
        assertEquals("newuser", createdUser.getUserName());
        assertEquals("newuser@example.com", createdUser.getUserEmail());
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID")
    public void testGetUserByIdSuccess() {
        // Crear usuario
        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        // Obtener usuario
        UserDto retrievedUser = userService.getUserById(savedUser.getUserId());

        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUserName());
        assertEquals("test@example.com", retrievedUser.getUserEmail());
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    public void testGetAllUsersSuccess() {
        // Crear múltiples usuarios
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

        // Obtener todos
        List<UserDto> allUsers = userService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Debe actualizar un usuario")
    public void testUpdateUserSuccess() {
        // Crear usuario
        User user = new User();
        user.setUserName("originaluser");
        user.setUserEmail("original@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        // Actualizar
        UserDto updateDto = new UserDto();
        updateDto.setUserName("updateduser");
        updateDto.setUserEmail("updated@example.com");

        UserDto updatedUser = userService.updateUser(updateDto, savedUser.getUserId());

        assertNotNull(updatedUser);
        assertEquals("updateduser", updatedUser.getUserName());
        assertEquals("updated@example.com", updatedUser.getUserEmail());
    }

    @Test
    @DisplayName("Debe eliminar un usuario")
    public void testDeleteUserSuccess() {
        // Crear usuario
        User user = new User();
        user.setUserName("deleteme");
        user.setUserEmail("delete@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        // Verificar que existe
        assertTrue(userRepo.existsById(savedUser.getUserId()));

        // Eliminar
        userService.deleteUser(savedUser.getUserId());

        // Verificar que fue eliminado
        assertFalse(userRepo.existsById(savedUser.getUserId()));
    }

    @Test
    @DisplayName("Debe obtener usuarios por rol")
    public void testGetUsersByRoleSuccess() {
        // Crear usuarios con diferentes roles
        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        userRepo.save(user);

        // Obtener usuarios por rol
        List<UserDto> usersByRole = userService.getUsersByRole("USER");

        assertNotNull(usersByRole);
        assertTrue(usersByRole.size() >= 1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    public void testGetNonExistentUserThrowsException() {
        assertThrows(Exception.class, () -> {
            userService.getUserById(999);
        });
    }
}
