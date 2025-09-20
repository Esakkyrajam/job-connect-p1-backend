package com.jobconnect.config;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JobseekerRepository jobseekerRepo;
    private RecruiterRepository recruiterRepo;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        jobseekerRepo = mock(JobseekerRepository.class);
        recruiterRepo = mock(RecruiterRepository.class);

        filter = new JwtAuthenticationFilter(jwtService, jobseekerRepo, recruiterRepo);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        // Clear SecurityContext before each test
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

    @Test
    void testDoFilterInternal_withValidJobseekerToken() throws Exception {
        String token = "Bearer validToken";
        String email = "jobseeker@example.com";
        Jobseeker js = new Jobseeker();
        js.setEmail(email);
        js.setPassword("pass123");

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractUsername("validToken")).thenReturn(email);
        when(jobseekerRepo.findByEmail(email)).thenReturn(Optional.of(js));
        when(jwtService.validateToken(eq("validToken"), any(User.class))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        // Verify filter chain continued
        verify(filterChain).doFilter(request, response);

        // Verify authentication is set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void testDoFilterInternal_withValidRecruiterToken() throws Exception {
        String token = "Bearer validToken";
        String email = "recruiter@example.com";
        Recruiter r = new Recruiter();
        r.setEmail(email);
        r.setPassword("pass123");

        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtService.extractUsername("validToken")).thenReturn(email);
        when(recruiterRepo.findByEmail(email)).thenReturn(Optional.of(r));
        // Correct matcher usage
        when(jwtService.validateToken(eq("validToken"), any(User.class))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
    }


    @Test
    void testDoFilterInternal_withInvalidTokenOrNoUser() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtService.extractUsername("invalidToken")).thenReturn("unknown@example.com");
        when(jobseekerRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(recruiterRepo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        filter.doFilterInternal(request, response, filterChain);

        // Authentication should remain null
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_withoutAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
