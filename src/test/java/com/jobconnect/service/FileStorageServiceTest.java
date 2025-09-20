package com.jobconnect.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;
    private String uploadDir = "test-uploads";

    @BeforeEach
    public void setUp() throws Exception {
        fileStorageService = new FileStorageService();

        // Set the private field 'uploadDir' using reflection
        Field field = FileStorageService.class.getDeclaredField("uploadDir");
        field.setAccessible(true);
        field.set(fileStorageService, uploadDir);
    }

    @Test
    public void testSaveFile_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "Hello, world!".getBytes()
        );

        String savedPath = fileStorageService.saveFile(file);

        assertTrue(Files.exists(Paths.get(savedPath)));
        assertTrue(savedPath.contains("testfile.txt"));

        // Cleanup
        Files.deleteIfExists(Paths.get(savedPath));
        new File(uploadDir).delete();
    }

    @Test
    public void testSaveFile_CreateDirectory() throws IOException {
        File dir = new File(uploadDir);
        if (dir.exists()) {
            dir.delete();
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "Hello, world!".getBytes()
        );

        String savedPath = fileStorageService.saveFile(file);

        assertTrue(Files.exists(Paths.get(savedPath)));
        assertTrue(new File(uploadDir).exists());

        // Cleanup
        Files.deleteIfExists(Paths.get(savedPath));
        new File(uploadDir).delete();
    }
}
