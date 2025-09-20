package com.jobconnect.controller;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.service.FileStorageService;
import com.jobconnect.service.JobseekerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobseekerControllerTest {

    @InjectMocks
    private JobseekerController jobseekerController;

    @Mock
    private JobseekerService jobSeekerService;

    @Mock
    private JobseekerRepository jobSeekerRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private MultipartFile multipartFile;

    private Jobseeker js;

    @BeforeEach
    void setup() {
        js = new Jobseeker();
        js.setId(1L);
        js.setName("Raja");
        js.setPhoneNumber("1234567890");
        js.setSkills("Java");
    }

    @Test
    void testUpdateJobseeker_withResume() throws IOException {
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(js));
        when(fileStorageService.saveFile(multipartFile)).thenReturn("resume/path/mock.pdf");
        when(jobSeekerRepository.save(js)).thenReturn(js);

        ResponseEntity<?> response = jobseekerController.updateJobseeker(
                1L, "Raja Updated", "9876543210", "Spring Boot", multipartFile);

        assertEquals(200, response.getStatusCodeValue());
        Jobseeker updatedJs = (Jobseeker) response.getBody();
        assertEquals("Raja Updated", updatedJs.getName());
        assertEquals("9876543210", updatedJs.getPhoneNumber());
        assertEquals("Spring Boot", updatedJs.getSkills());
        assertEquals("resume/path/mock.pdf", updatedJs.getResumeUrl());
    }

    @Test
    void testUpdateJobseeker_withoutResume() throws IOException {
        when(jobSeekerRepository.findById(1L)).thenReturn(Optional.of(js));
        when(jobSeekerRepository.save(js)).thenReturn(js);

        ResponseEntity<?> response = jobseekerController.updateJobseeker(
                1L, "Raja Updated", "9876543210", "Spring Boot", null);

        assertEquals(200, response.getStatusCodeValue());
        Jobseeker updatedJs = (Jobseeker) response.getBody();
        assertEquals("Raja Updated", updatedJs.getName());
        assertEquals("9876543210", updatedJs.getPhoneNumber());
        assertEquals("Spring Boot", updatedJs.getSkills());
        assertNull(updatedJs.getResumeUrl());
    }

    @Test
    void testDeleteJobSeeker() {
        doNothing().when(jobSeekerService).deleteJobSeeker(1L);

        String response = jobseekerController.deleteJobSeeker(1L);

        assertEquals("JobSeeker deleted successfully!", response);
        verify(jobSeekerService, times(1)).deleteJobSeeker(1L);
    }

    @Test
    void testGetJobSeeker() {
        when(jobSeekerService.getJobSeekerById(1L)).thenReturn(Optional.of(js));

        Optional<Jobseeker> response = jobseekerController.getJobSeeker(1L);

        assertTrue(response.isPresent());
        assertEquals(js, response.get());
    }
}
