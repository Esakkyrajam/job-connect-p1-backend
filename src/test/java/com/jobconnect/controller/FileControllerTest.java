package com.jobconnect.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {

    private final FileController fileController = new FileController();

    @Test
    void testGetResume_fileExists() throws Exception {
        // Arrange: create a temporary file in "uploads"
        Path uploadsDir = Paths.get("uploads");
        Files.createDirectories(uploadsDir);
        Path tempFile = uploadsDir.resolve("test_resume.pdf");
        Files.write(tempFile, "Dummy PDF content".getBytes());

        // Act
        ResponseEntity<Resource> response = fileController.getResume("test_resume.pdf");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0)
                .contains("test_resume.pdf"));
        assertNotNull(response.getBody());
        assertTrue(response.getBody().exists());

        // Cleanup
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testGetResume_fileNotFound() throws MalformedURLException {
        // Act
        ResponseEntity<Resource> response = fileController.getResume("nonexistent.pdf");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
