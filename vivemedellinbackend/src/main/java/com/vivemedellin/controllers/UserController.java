package com.vivemedellin.controllers;

import com.vivemedellin.payloads.ApiResponse;
import com.vivemedellin.payloads.ImageResponse;
import com.vivemedellin.payloads.JwtAuthResponse;
import com.vivemedellin.payloads.LoginRequest;
import com.vivemedellin.payloads.UserDto;
import com.vivemedellin.services.FileService;
import com.vivemedellin.utils.JwtUtil;
import com.vivemedellin.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios, autenticación y autorización")
public class UserController {

        @Autowired
        private UserService userService;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private JwtUtil jwtUtil;

        @Autowired
        private FileService fileService;

        @Value("${project.image}")
        private String path;

        @Operation(summary = "Registrar nuevo usuario", description = "Crea una nueva cuenta de usuario en el sistema. Por defecto, los nuevos usuarios tienen rol USER.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = UserDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "El email ya está registrado")
        })
        @PostMapping("/register")
        public ResponseEntity<JwtAuthResponse> createUser(@Valid @RequestBody UserDto userDto) {
                // Create the user
                UserDto createdUser = this.userService.createUser(userDto);

                // Load the UserDetails and generate a JWT so the client can be
                // auto-authenticated after signup
                UserDetails userDetails = userService.loadUserByUsername(createdUser.getEmail());
                String token = jwtUtil.generateToken(userDetails);

                return new ResponseEntity<>(new JwtAuthResponse("User created", token), HttpStatus.CREATED);
        }

        @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT válido por 30 minutos")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        })
        @PostMapping("/login")
        public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
                        String token = jwtUtil.generateToken(userDetails);

                        return ResponseEntity.ok(new JwtAuthResponse("Login successful", token));
                } catch (BadCredentialsException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(new JwtAuthResponse("Invalid credentials", null));
                }
        }

        @Operation(summary = "Cerrar sesión", description = "Invalida el token JWT actual del usuario autenticado", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sesión cerrada exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido o expirado")
        })
        @PostMapping("/logout")
        public ResponseEntity<String> logout(HttpServletRequest request) {
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);

                        if (jwtUtil.isTokenExpired(token)) {
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
                        }

                        jwtUtil.invalidateToken(token);
                }
                return ResponseEntity.ok("Logged out successfully");
        }

        @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario. Solo el propietario de la cuenta puede actualizarla.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(schema = @Schema(implementation = UserDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado")
        })
        @PutMapping("/{userId}")
        public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto,
                        @PathVariable("userId") Integer uid) {
                UserDto updatedUser = this.userService.updateUser(userDto, uid);
                return ResponseEntity.ok(updatedUser);
        }

        @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario del sistema. Solo administradores pueden ejecutar esta acción.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario eliminado exitosamente", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores")
        })
        @DeleteMapping("/{userId}")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
                this.userService.deleteUser(uid);
                return new ResponseEntity<>(new ApiResponse("User deleted successfully", true), HttpStatus.OK);
        }

        @Operation(summary = "Listar todos los usuarios", description = "Obtiene la lista completa de usuarios registrados. Solo administradores pueden acceder.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado - Solo administradores")
        })
        @GetMapping("/")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<List<UserDto>> getAllUsers() {
                return ResponseEntity.ok(this.userService.getAllUsers());
        }

        @Operation(summary = "Obtener usuario por ID", description = "Obtiene la información detallada de un usuario específico. Requiere autenticación.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UserDto.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autenticado")
        })
        @GetMapping("/{userId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
                return ResponseEntity.ok(this.userService.getUserById(userId));
        }

        @Operation(summary = "Subir imagen de perfil", description = "Permite al usuario subir una imagen de perfil. La imagen se guardará y se asociará al usuario.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Imagen subida exitosamente", content = @Content(schema = @Schema(implementation = ImageResponse.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado")
        })
        @PostMapping("/profile-image/upload/{userId}")
        public ResponseEntity<ImageResponse> uploadProfileImage(
                        @RequestParam("image") MultipartFile image,
                        @PathVariable Integer userId) throws IOException {
                UserDto userDto = this.userService.getUserById(userId);

                String fileName = this.fileService.uploadImage(path, image);
                userDto.setProfileImage(fileName);
                UserDto updatedUser = this.userService.updateUser(userDto, userId);

                return new ResponseEntity<>(new ImageResponse(fileName, "Profile image uploaded successfully"),
                                HttpStatus.OK);
        }

        @Operation(summary = "Obtener imagen de perfil", description = "Descarga la imagen de perfil de un usuario")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Imagen obtenida exitosamente"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Imagen no encontrada")
        })
        @GetMapping(value = "/profile-image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
        public void downloadProfileImage(
                        @PathVariable("imageName") String imageName,
                        HttpServletResponse response) throws IOException {
                InputStream resource = this.fileService.getResource(path, imageName);
                response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                StreamUtils.copy(resource, response.getOutputStream());
        }

}
