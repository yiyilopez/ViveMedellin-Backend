package com.vivemedellin.services;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
    InputStream getResource(String path , String fileName) throws FileNotFoundException;
}