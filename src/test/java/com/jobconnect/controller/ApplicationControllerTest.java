package com.jobconnect.controller;

import com.jobconnect.entity.Application;
import com.jobconnect.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ApplicationControllerTest {

    @Mock
    private ApplicationService appService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ApplicationController applicationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApply() {
        // Arrange
        Long jobId = 1L;
        String email = "jobseeker@example.com";
        when(authentication.getName()).thenReturn(email);
        Application app = new Application();
        app.setId(100L);
        when(appService.applyToJob(jobId, email)).thenReturn(app);

        // Act
        Application result = applicationController.apply(jobId, authentication);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        verify(appService, times(1)).applyToJob(jobId, email);
    }

    @Test
    void testGetMyApplications() {
        // Arrange
        String email = "jobseeker@example.com";
        when(authentication.getName()).thenReturn(email);
        Application app1 = new Application();
        Application app2 = new Application();
        List<Application> applications = Arrays.asList(app1, app2);
        when(appService.getJobseekerApplicationsByEmail(email)).thenReturn(applications);

        // Act
        List<Application> result = applicationController.getMyApplications(authentication);

        // Assert
        assertThat(result).hasSize(2).contains(app1, app2);
        verify(appService, times(1)).getJobseekerApplicationsByEmail(email);
    }

    @Test
    void testGetJobApps() {
        // Arrange
        Long jobId = 5L;
        Application app1 = new Application();
        Application app2 = new Application();
        List<Application> applications = Arrays.asList(app1, app2);
        when(appService.getJobApplications(jobId)).thenReturn(applications);

        // Act
        List<Application> result = applicationController.getJobApps(jobId);

        // Assert
        assertThat(result).hasSize(2).contains(app1, app2);
        verify(appService, times(1)).getJobApplications(jobId);
    }

    @Test
    void testApproveApplication() {
        // Arrange
        Long appId = 10L;
        doNothing().when(appService).approveApplication(appId);

        // Act
        ResponseEntity<String> response = applicationController.approveApplication(appId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Candidate approved!");
        verify(appService, times(1)).approveApplication(appId);
    }

    @Test
    void testRejectApplication() {
        // Arrange
        Long appId = 20L;
        doNothing().when(appService).rejectApplication(appId);

        // Act
        ResponseEntity<String> response = applicationController.rejectApplication(appId);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Candidate rejected!");
        verify(appService, times(1)).rejectApplication(appId);
    }
}
