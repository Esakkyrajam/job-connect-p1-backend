package com.jobconnect.controller;

import com.jobconnect.entity.Job;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.JobService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JobControllerTest {

    @InjectMocks
    private JobController jobController;

    @Mock
    private JobService jobService;

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private Authentication authentication;

    private Recruiter recruiter;
    private Job job;

    @BeforeEach
    void setup() {
        recruiter = new Recruiter();
        recruiter.setEmail("recruiter@example.com");

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
    }

    @Test
    void testApplyForJob_success() {
        when(authentication.getName()).thenReturn("jobseeker@example.com");

        ResponseEntity<String> response = jobController.applyForJob(1L, authentication);

        verify(jobService).applyForJob(1L, "jobseeker@example.com");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Applied successfully!", response.getBody());
    }

    @Test
    void testCreateJob_success() {
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(jobService.createJob(job, "recruiter@example.com")).thenReturn(job);

        ResponseEntity<Job> response = jobController.createJob(job, authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(job, response.getBody());
    }

    @Test
    void testUpdateJob_success() {
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(jobService.updateJob(1L, job, "recruiter@example.com")).thenReturn(job);

        ResponseEntity<Job> response = jobController.updateJob(1L, job, authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(job, response.getBody());
    }

    @Test
    void testGetMyJobs_success() {
        when(authentication.getName()).thenReturn("recruiter@example.com");
        when(recruiterRepository.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findByRecruiter(recruiter)).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobController.getMyJobs(authentication);

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
        assertEquals("Java Developer", jobs.get(0).getTitle());
    }

    @Test
    void testListJobs_success() {
        when(jobService.listJobs()).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobController.listJobs();

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
    }

    @Test
    void testSearchJobs_success() {
        when(jobService.searchJobs("Java", "Chennai")).thenReturn(Arrays.asList(job));

        List<Job> jobs = jobController.searchJobs("Java", "Chennai");

        assertNotNull(jobs);
        assertEquals(1, jobs.size());
    }
}
