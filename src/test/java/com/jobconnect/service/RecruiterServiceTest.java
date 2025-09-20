package com.jobconnect.service;

import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecruiterServiceTest {

    @Mock
    private RecruiterRepository recruiterRepo;

    @InjectMocks
    private RecruiterService recruiterService;

    private Recruiter recruiter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        recruiter = new Recruiter();
        recruiter.setId(1L);
        recruiter.setName("Test Name");
        recruiter.setEmail("test@example.com");
        recruiter.setCompanyName("TestCompany");
    }

    @Test
    public void testUpdateRecruiterSuccess() {
        Recruiter updatedDetails = new Recruiter();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@example.com");
        updatedDetails.setCompanyName("UpdatedCompany");

        when(recruiterRepo.findById(1L)).thenReturn(Optional.of(recruiter));
        when(recruiterRepo.save(any(Recruiter.class))).thenAnswer(i -> i.getArguments()[0]);

        Recruiter updatedRecruiter = recruiterService.updateRecruiter(1L, updatedDetails);

        assertEquals("Updated Name", updatedRecruiter.getName());
        assertEquals("updated@example.com", updatedRecruiter.getEmail());
        assertEquals("UpdatedCompany", updatedRecruiter.getCompanyName());

        verify(recruiterRepo, times(1)).findById(1L);
        verify(recruiterRepo, times(1)).save(recruiter);
    }

    @Test
    public void testUpdateRecruiterNotFound() {
        when(recruiterRepo.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recruiterService.updateRecruiter(2L, recruiter);
        });

        assertEquals("Recruiter not found", exception.getMessage());
        verify(recruiterRepo, times(1)).findById(2L);
        verify(recruiterRepo, never()).save(any());
    }

    @Test
    public void testDeleteRecruiter() {
        doNothing().when(recruiterRepo).deleteById(1L);

        recruiterService.deleteRecruiter(1L);

        verify(recruiterRepo, times(1)).deleteById(1L);
    }

    @Test
    public void testGetRecruiterByIdFound() {
        when(recruiterRepo.findById(1L)).thenReturn(Optional.of(recruiter));

        Optional<Recruiter> result = recruiterService.getRecruiterById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Name", result.get().getName());
        verify(recruiterRepo, times(1)).findById(1L);
    }

    @Test
    public void testGetRecruiterByIdNotFound() {
        when(recruiterRepo.findById(2L)).thenReturn(Optional.empty());

        Optional<Recruiter> result = recruiterService.getRecruiterById(2L);

        assertFalse(result.isPresent());
        verify(recruiterRepo, times(1)).findById(2L);
    }
}
