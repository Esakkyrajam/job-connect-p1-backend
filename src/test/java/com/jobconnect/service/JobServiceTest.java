package com.jobconnect.service;

import com.jobconnect.entity.Job;
import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private JobseekerRepository jobseekerRepository;

    @InjectMocks
    private JobService jobService;

    private Job job;
    private Recruiter recruiter;
    private Jobseeker jobseeker;

    @BeforeEach
    public void setup() {
        recruiter = new Recruiter();
        recruiter.setId(1L);
        recruiter.setEmail("recruiter@example.com");
        recruiter.setName("Recruiter One");

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setLocation("Chennai");
        job.setRecruiter(recruiter);

        jobseeker = new Jobseeker();
        jobseeker.setId(1L);
        jobseeker.setEmail("jobseeker@example.com");
        jobseeker.setName("Jobseeker One");
    }

    @Test
    public void testPostJob() {
        when(jobRepository.save(job)).thenReturn(job);

        Job savedJob = jobService.postJob(job);

        assertNotNull(savedJob);
        assertEquals("Java Developer", savedJob.getTitle());
    }

    @Test
    public void testListJobs() {
        when(jobRepository.findAll()).thenReturn(List.of(job));

        List<Job> jobs = jobService.listJobs();

        assertEquals(1, jobs.size());
        assertEquals("Java Developer", jobs.get(0).getTitle());
    }

    @Test
    public void testCreateJob() {
        when(recruiterRepository.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.save(job)).thenReturn(job);

        Job createdJob = jobService.createJob(job, "recruiter@example.com");

        assertNotNull(createdJob);
        assertEquals("Java Developer", createdJob.getTitle());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    public void testApplyForJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobseekerRepository.findByEmail("jobseeker@example.com")).thenReturn(Optional.of(jobseeker));

        assertDoesNotThrow(() -> jobService.applyForJob(1L, "jobseeker@example.com"));

        verify(jobRepository, times(1)).findById(1L);
        verify(jobseekerRepository, times(1)).findByEmail("jobseeker@example.com");
    }

    @Test
    public void testUpdateJob() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(jobRepository.save(job)).thenReturn(job);

        Job updatedDetails = new Job();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setLocation("Mumbai");
        updatedDetails.setSkillsRequired("Spring Boot");
        updatedDetails.setSalary(80000.00);
        updatedDetails.setDescription("Updated description");

        Job updatedJob = jobService.updateJob(1L, updatedDetails, "recruiter@example.com");

        assertEquals("Updated Title", updatedJob.getTitle());
        assertEquals("Mumbai", updatedJob.getLocation());
    }

    @Test
    public void testListJobsByRecruiter() {
        when(recruiterRepository.findByEmail("recruiter@example.com")).thenReturn(Optional.of(recruiter));
        when(jobRepository.findByRecruiter(recruiter)).thenReturn(List.of(job));

        List<Job> jobs = jobService.listJobsByRecruiter("recruiter@example.com");

        assertEquals(1, jobs.size());
        assertEquals("Java Developer", jobs.get(0).getTitle());
    }
}
