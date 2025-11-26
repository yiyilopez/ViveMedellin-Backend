package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.EmailAlreadyExistsException;
import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Role;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.UserDto;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.security.CustomUserDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepo userRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success_UserRole() {
        UserDto dto = new UserDto();
        dto.setEmail("user@example.com");
        dto.setPassword("12345");

        User user = new User();
        User savedUser = new User();

        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(encoder.encode("12345")).thenReturn("encoded");
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDto.class)).thenReturn(dto);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertTrue(result.getRoles().contains(Role.ROLE_USER));
        verify(userRepo).save(user);
    }

    @Test
    void testCreateUser_AdminRole() {
        UserDto dto = new UserDto();
        dto.setEmail("admin@admin.com");
        dto.setPassword("12345");

        User user = new User();
        User savedUser = new User();

        when(userRepo.findByEmail("admin@admin.com")).thenReturn(Optional.empty());
        when(encoder.encode("12345")).thenReturn("encoded");
        when(modelMapper.map(dto, User.class)).thenReturn(user);
        when(userRepo.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDto.class)).thenReturn(dto);

        UserDto result = userService.createUser(dto);

        assertTrue(result.getRoles().contains(Role.ROLE_ADMIN));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        UserDto dto = new UserDto();
        dto.setEmail("exists@example.com");

        when(userRepo.findByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void testUpdateUser_Success() {
        UserDto dto = new UserDto();
        dto.setName("New Name");
        dto.setEmail("new@example.com");
        dto.setPassword("newpass");
        dto.setAbout("About me");
        dto.setRoles(Set.of(Role.ROLE_USER));

        User user = new User();
        User updatedUser = new User();

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(encoder.encode("newpass")).thenReturn("encoded");
        when(userRepo.save(user)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(dto);

        UserDto result = userService.updateUser(dto, 1);

        assertEquals("New Name", result.getName());
        verify(userRepo).save(user);
    }

    @Test
    void testGetUserById_Success() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.getUserById(1);

        assertNotNull(result);
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
    }

    @Test
    void testDeleteUser_Success() {
        User user = new User();
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        userService.deleteUser(1);

        verify(userRepo).delete(user);
    }

    @Test
    void testGetUsersByRole() {
        User user = new User();
        UserDto dto = new UserDto();

        when(userRepo.findByRoles(Role.ROLE_USER)).thenReturn(List.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        List<UserDto> result = userService.getUsersByRole("ROLE_USER");

        assertEquals(1, result.size());
    }

    @Test
    void testLoadUserByUsername() {
        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailService.loadUserByUsername("test")).thenReturn(userDetails);

        UserDetails result = userService.loadUserByUsername("test");

        assertNotNull(result);
    }
}
