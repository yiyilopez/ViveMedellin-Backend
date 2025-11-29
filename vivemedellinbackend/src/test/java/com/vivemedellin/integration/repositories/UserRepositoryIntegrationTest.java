package com.vivemedellin.integration.repositories;

import com.vivemedellin.models.User;
import com.vivemedellin.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para UserRepository
 * Verifica la interacción directa con la base de datos para usuarios
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración - UserRepository")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    public void setUp() {
        userRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar un usuario en la base de datos")
    public void testSaveUserSuccess() {
        User user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");

        User savedUser = userRepo.save(user);

        assertNotNull(savedUser.getUserId());
        assertEquals("testuser", savedUser.getUserName());
        assertEquals("test@example.com", savedUser.getUserEmail());
    }

    @Test
    @DisplayName("Debe encontrar un usuario por ID")
    public void testFindUserByIdSuccess() {
        User user = new User();
        user.setUserName("findme");
        user.setUserEmail("findme@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        Optional<User> foundUser = userRepo.findById(savedUser.getUserId());

        assertTrue(foundUser.isPresent());
        assertEquals("findme", foundUser.get().getUserName());
    }

    @Test
    @DisplayName("Debe encontrar un usuario por email")
    public void testFindUserByEmailSuccess() {
        User user = new User();
        user.setUserName("emailuser");
        user.setUserEmail("unique@example.com");
        user.setUserPassword("password123");
        userRepo.save(user);

        Optional<User> foundUser = userRepo.findByUserEmail("unique@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("emailuser", foundUser.get().getUserName());
    }

    @Test
    @DisplayName("Debe encontrar un usuario por nombre de usuario")
    public void testFindUserByUsernameSuccess() {
        User user = new User();
        user.setUserName("uniqueusername");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        userRepo.save(user);

        Optional<User> foundUser = userRepo.findByUserName("uniqueusername");

        assertTrue(foundUser.isPresent());
        assertEquals("uniqueusername", foundUser.get().getUserName());
    }

    @Test
    @DisplayName("Debe retornar vacío cuando el usuario no existe")
    public void testFindNonExistentUserReturnsEmpty() {
        Optional<User> notFound = userRepo.findById(999);
        assertFalse(notFound.isPresent());

        Optional<User> notFoundByEmail = userRepo.findByUserEmail("nonexistent@example.com");
        assertFalse(notFoundByEmail.isPresent());
    }

    @Test
    @DisplayName("Debe obtener todos los usuarios")
    public void testFindAllUsersSuccess() {
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

        List<User> allUsers = userRepo.findAll();

        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Debe actualizar un usuario")
    public void testUpdateUserSuccess() {
        User user = new User();
        user.setUserName("original");
        user.setUserEmail("original@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        savedUser.setUserName("updated");
        savedUser.setUserEmail("updated@example.com");
        User updatedUser = userRepo.save(savedUser);

        assertEquals("updated", updatedUser.getUserName());
        assertEquals("updated@example.com", updatedUser.getUserEmail());
    }

    @Test
    @DisplayName("Debe eliminar un usuario")
    public void testDeleteUserSuccess() {
        User user = new User();
        user.setUserName("deleteme");
        user.setUserEmail("delete@example.com");
        user.setUserPassword("password123");
        User savedUser = userRepo.save(user);

        userRepo.deleteById(savedUser.getUserId());

        Optional<User> deletedUser = userRepo.findById(savedUser.getUserId());
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Debe contar todos los usuarios")
    public void testCountUsersSuccess() {
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

        long count = userRepo.count();

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe un usuario por email")
    public void testExistsByEmailSuccess() {
        User user = new User();
        user.setUserName("exists");
        user.setUserEmail("exists@example.com");
        user.setUserPassword("password123");
        userRepo.save(user);

        assertTrue(userRepo.existsByUserEmail("exists@example.com"));
        assertFalse(userRepo.existsByUserEmail("notexists@example.com"));
    }

    @Test
    @DisplayName("Debe prevenir emails duplicados")
    public void testDuplicateEmailConstraint() {
        User user1 = new User();
        user1.setUserName("user1");
        user1.setUserEmail("duplicate@example.com");
        user1.setUserPassword("password123");
        userRepo.save(user1);

        User user2 = new User();
        user2.setUserName("user2");
        user2.setUserEmail("duplicate@example.com");
        user2.setUserPassword("password123");

        // Esto debería lanzar una excepción de restricción única
        assertThrows(Exception.class, () -> {
            userRepo.save(user2);
            userRepo.flush();
        });
    }
}
