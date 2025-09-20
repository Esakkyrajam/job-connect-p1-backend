package com.jobconnect.service;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.JobseekerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobseekerServiceTest {

    @Mock
    private JobseekerRepository jobSeekerRepo;

    @InjectMocks
    private JobseekerService jobseekerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateJobSeeker_Success() {
        Jobseeker existing = new Jobseeker();
        existing.setId(1L);
        existing.setName("Old Name");

        Jobseeker updates = new Jobseeker();
        updates.setName("New Name");

        when(jobSeekerRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(jobSeekerRepo.save(any(Jobseeker.class))).thenAnswer(i -> i.getArguments()[0]);

        Jobseeker result = jobseekerService.updateJobSeeker(1L, updates);

        assertEquals("New Name", result.getName());
        verify(jobSeekerRepo).save(existing);
    }

    @Test
    public void testUpdateJobSeeker_NotFound() {
        when(jobSeekerRepo.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            jobseekerService.updateJobSeeker(1L, new Jobseeker());
        });

        assertEquals("JobSeeker not found", exception.getMessage());
    }

    @Test
    public void testDeleteJobSeeker() {
        jobseekerService.deleteJobSeeker(1L);
        verify(jobSeekerRepo).deleteById(1L);
    }

    @Test
    public void testGetJobSeekerById_Found() {
        Jobseeker jobseeker = new Jobseeker();
        jobseeker.setId(1L);
        when(jobSeekerRepo.findById(1L)).thenReturn(Optional.of(jobseeker));

        Optional<Jobseeker> result = jobseekerService.getJobSeekerById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    public void testGetJobSeekerById_NotFound() {
        when(jobSeekerRepo.findById(1L)).thenReturn(Optional.empty());

        Optional<Jobseeker> result = jobseekerService.getJobSeekerById(1L);

        assertFalse(result.isPresent());
    }
}
