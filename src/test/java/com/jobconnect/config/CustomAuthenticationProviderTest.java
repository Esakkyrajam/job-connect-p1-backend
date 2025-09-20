package com.jobconnect.config;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomAuthenticationProviderTest {

    private JobseekerRepository jobseekerRepo;
    private RecruiterRepository recruiterRepo;
    private CustomAuthenticationProvider authProvider;

    @BeforeEach
    void setUp() {
        jobseekerRepo = mock(JobseekerRepository.class);
        recruiterRepo = mock(RecruiterRepository.class);
        authProvider = new CustomAuthenticationProvider(jobseekerRepo, recruiterRepo);
    }

    @Test
    void testAuthenticateJobseekerSuccess() {
        String email = "jobseeker@example.com";
        String password = "pass123";

        Jobseeker js = new Jobseeker();
        js.setEmail(email);
        js.setPassword(password);

        when(jobseekerRepo.findByEmail(email)).thenReturn(Optional.of(js));

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        Authentication result = authProvider.authenticate(auth);

        assertNotNull(result);
        assertEquals(email, result.getName());
        verify(jobseekerRepo, times(1)).findByEmail(email);
        verify(recruiterRepo, never()).findByEmail(any());
    }

    @Test
    void testAuthenticateRecruiterSuccess() {
        String email = "recruiter@example.com";
        String password = "pass123";

        Recruiter r = new Recruiter();
        r.setEmail(email);
        r.setPassword(password);

        when(jobseekerRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(recruiterRepo.findByEmail(email)).thenReturn(Optional.of(r));

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);
        Authentication result = authProvider.authenticate(auth);

        assertNotNull(result);
        assertEquals(email, result.getName());
        verify(jobseekerRepo, times(1)).findByEmail(email);
        verify(recruiterRepo, times(1)).findByEmail(email);
    }

    @Test
    void testAuthenticateFailure() {
        String email = "unknown@example.com";
        String password = "wrongpass";

        when(jobseekerRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(recruiterRepo.findByEmail(email)).thenReturn(Optional.empty());

        Authentication auth = new UsernamePasswordAuthenticationToken(email, password);

        assertThrows(BadCredentialsException.class, () -> authProvider.authenticate(auth));

        verify(jobseekerRepo, times(1)).findByEmail(email);
        verify(recruiterRepo, times(1)).findByEmail(email);
    }

    @Test
    void testSupports() {
        assertTrue(authProvider.supports(UsernamePasswordAuthenticationToken.class));
        assertFalse(authProvider.supports(String.class));
    }
}
