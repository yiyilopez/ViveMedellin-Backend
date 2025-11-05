package com.vivemedellin.services.impl;

import com.vivemedellin.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed to upload!");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null) {
            originalName = "file"; // fallback name
        }

        String randomID = UUID.randomUUID().toString();
        int dotIndex = originalName.lastIndexOf('.');
        String fileExtension = dotIndex >= 0 ? originalName.substring(dotIndex) : "";
        String fileName = randomID.concat(fileExtension);

        // Ensure the directory exists (create parent directories if needed)
        java.nio.file.Path folderPath = Paths.get(path);
        Files.createDirectories(folderPath);

        java.nio.file.Path destination = folderPath.resolve(fileName);

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, destination);
        }

        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        java.nio.file.Path fullPath = Paths.get(path).resolve(fileName);
        File f = fullPath.toFile();
        if (!f.exists()) {
            throw new FileNotFoundException("File not found: " + fullPath.toString());
        }
        return new FileInputStream(f);
    }
}
