package com.vivemedellin.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.vivemedellin.models.User;;

@DataJpaTest
public class UserRepoTest {
    @Autowired
    private UserRepo userRepo;

    @Test
    void shouldReturnUser_whenEmailExists() {
        // Arrange
        User testUser = new User(
            "Test",
            "user@gmail.com",
            "1234",
            "Soy un usuario de prueba"
        );
        userRepo.save(testUser);

        // Act
        Optional<User> foundOptional = userRepo.findByEmail(testUser.getEmail());

        // Assert
        assertTrue(foundOptional.isPresent(), "El usuario debería existir en la base de datos");
        User foundUser = foundOptional.get();
        assertEquals("user@gmail.com", foundUser.getEmail(),
            "El email del usuario encontrado debería coincidir con el de prueba");
        }

    @Test
    void shouldReturnEmpty_whenEmailDoesNotExist() {
        // Arrange

        // Act
        Optional<User> foundOptional = userRepo.findByEmail("nonexistent@gmail.com");
        
        // Assert
        assertTrue(foundOptional.isEmpty(), "El resultado debería estar vacío si el email no existe");
    }

}
