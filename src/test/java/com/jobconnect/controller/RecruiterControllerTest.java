package com.jobconnect.controller;

import com.jobconnect.entity.Application;
import com.jobconnect.entity.Job;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.ApplicationRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.JobService;
import com.jobconnect.service.JwtService;
import com.jobconnect.service.RecruiterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecruiterControllerTest {

    @InjectMocks
    private RecruiterController recruiterController;

    @Mock
    private RecruiterService recruiterService;

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private JobService jobService;

    @Mock
    private Authentication authentication;

    private Recruiter recruiter;
    private Application application;
    private Job job;

    @BeforeEach
    void setup() {
        recruiter = new Recruiter();
        recruiter.setId(1L);
        recruiter.setName("Raja");
        recruiter.setPhoneNumber("1234567890");
        recruiter.setCompanyName("MyCompany");

        application = new Application();
        application.setId(1L);
        application.setStatus("PENDING");
        application.setJob(job);

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
    }

    @Test
    void testGetApplications() {
        // Arrange
        String token = "mock-token";
        when(jwtService.extractCompanyName(token)).thenReturn("MyCompany");

        Recruiter recruiter = new Recruiter();
        recruiter.setCompanyName("MyCompany");

        Job job = new Job();
        job.setRecruiter(recruiter);

        Application app = new Application();
        app.setId(1L);
        app.setStatus("PENDING");
        app.setJob(job);

        // Correct repository method
        when(applicationRepository.findByCompanyName("MyCompany"))
                .thenReturn(Arrays.asList(app));

        // Act
        ResponseEntity<List<Application>> response = recruiterController.getApplications("Bearer " + token);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("MyCompany", response.getBody().get(0).getJob().getRecruiter().getCompanyName());
        assertEquals("PENDING", response.getBody().get(0).getStatus());
    }



    @Test
    void testApproveApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(application)).thenReturn(application);

        ResponseEntity<String> response = recruiterController.approveApplication(1L);

        assertEquals("Application approved!", response.getBody());
        assertEquals("APPROVED", application.getStatus());
    }

    @Test
    void testUpdateRecruiter() {
        when(recruiterRepository.findById(1L)).thenReturn(Optional.of(recruiter));
        when(recruiterRepository.save(recruiter)).thenReturn(recruiter);

        ResponseEntity<?> response = recruiterController.updateRecruiter(1L, "Raja Updated", "9876543210", "NewCompany");

        Recruiter updated = (Recruiter) response.getBody();
        assertEquals("Raja Updated", updated.getName());
        assertEquals("9876543210", updated.getPhoneNumber());
        assertEquals("NewCompany", updated.getCompanyName());
    }

    @Test
    void testDeleteRecruiter() {
        doNothing().when(recruiterService).deleteRecruiter(1L);

        String response = recruiterController.deleteRecruiter(1L);

        assertEquals("Recruiter deleted successfully!", response);
        verify(recruiterService, times(1)).deleteRecruiter(1L);
    }

    @Test
    void testGetRecruiter() {
        when(recruiterService.getRecruiterById(1L)).thenReturn(Optional.of(recruiter));

        Optional<Recruiter> response = recruiterController.getRecruiter(1L);

        assertTrue(response.isPresent());
        assertEquals("Raja", response.get().getName());
    }

    @Test
    void testGetRecruiterJobs() {
        when(authentication.getName()).thenReturn("rec@example.com");
        when(jobService.listJobsByRecruiter("rec@example.com")).thenReturn(Arrays.asList(job));

        List<Job> response = recruiterController.getRecruiterJobs(authentication);

        assertEquals(1, response.size());
        assertEquals("Java Developer", response.get(0).getTitle());
    }
}
