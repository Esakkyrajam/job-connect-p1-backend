package com.jobconnect.controller;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private JobseekerRepository jobseekerRepository;

    @Mock
    private RecruiterRepository recruiterRepository;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCounts() {
        // Arrange
        when(jobseekerRepository.count()).thenReturn(5L);
        when(recruiterRepository.count()).thenReturn(3L);

        // Act
        ResponseEntity<?> response = adminController.getCounts();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        Map<String, Long> counts = (Map<String, Long>) response.getBody();
        assertThat(counts).isNotNull();
        assertThat(counts.get("jobseekers")).isEqualTo(5L);
        assertThat(counts.get("recruiters")).isEqualTo(3L);

        verify(jobseekerRepository, times(1)).count();
        verify(recruiterRepository, times(1)).count();
    }

    @Test
    void testGetJobseekers() {
        // Arrange
        Jobseeker js1 = new Jobseeker();
        js1.setEmail("job1@example.com");
        Jobseeker js2 = new Jobseeker();
        js2.setEmail("job2@example.com");

        List<Jobseeker> jobseekers = Arrays.asList(js1, js2);
        when(jobseekerRepository.findAll()).thenReturn(jobseekers);

        // Act
        ResponseEntity<?> response = adminController.getJobseekers();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        List<Jobseeker> result = (List<Jobseeker>) response.getBody();
        assertThat(result).hasSize(2).contains(js1, js2);

        verify(jobseekerRepository, times(1)).findAll();
    }

    @Test
    void testGetRecruiters() {
        // Arrange
        Recruiter r1 = new Recruiter();
        r1.setEmail("recruiter1@example.com");
        Recruiter r2 = new Recruiter();
        r2.setEmail("recruiter2@example.com");

        List<Recruiter> recruiters = Arrays.asList(r1, r2);
        when(recruiterRepository.findAll()).thenReturn(recruiters);

        // Act
        ResponseEntity<?> response = adminController.getRecruiters();

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        List<Recruiter> result = (List<Recruiter>) response.getBody();
        assertThat(result).hasSize(2).contains(r1, r2);

        verify(recruiterRepository, times(1)).findAll();
    }
}
