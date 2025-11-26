package com.vivemedellin.services.impl;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceImplTest {

    private FileServiceImpl fileService;

    private static Path tempDir;

    @BeforeAll
    static void setupAll() throws IOException {
        // Crear un directorio temporal para pruebas
        tempDir = Files.createTempDirectory("test-files");
    }

    @AfterAll
    static void cleanupAll() throws IOException {
        // Limpiar archivos después de todas las pruebas
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl();
    }

    @Test
    void testUploadImage_Success() throws IOException {
        // Crear un archivo de prueba
        MultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "dummy content".getBytes()
        );

        String fileName = fileService.uploadImage(tempDir.toString(), file);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".png"));

        // Verificar que el archivo realmente exista
        File savedFile = new File(tempDir.toString() + File.separator + fileName);
        assertTrue(savedFile.exists());
    }

    @Test
    void testUploadImage_NotImage() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "dummy content".getBytes()
        );

        assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadImage(tempDir.toString(), file);
        });
    }

    @Test
    void testGetResource_Success() throws IOException {
        // Crear un archivo de prueba
        String content = "Hello World";
        String fileName = "hello.txt";
        Path filePath = tempDir.resolve(fileName);
        Files.write(filePath, content.getBytes());

        InputStream is = fileService.getResource(tempDir.toString(), fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();

        assertEquals("Hello World", line);
    }

    @Test
    void testGetResource_FileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            fileService.getResource(tempDir.toString(), "nonexistent.txt");
        });
    }
}
