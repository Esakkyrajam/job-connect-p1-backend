package com.jobconnect.controller;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.payload.JwtResponse;
import com.jobconnect.payload.LoginRequest;
import com.jobconnect.payload.SignupRequest;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.AuthService;
import com.jobconnect.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private JobseekerRepository jobSeekerRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    // -------------------- Recruiter Registration Test --------------------
    @Test
    void testRegisterRecruiter_success() {
        SignupRequest request = new SignupRequest();
        request.setName("Raja");
        request.setEmail("raj@example.com");
        request.setPassword("password");
        request.setPhoneNumber("1234567890");
        request.setCompanyName("MyCompany");

        Recruiter mockRecruiter = new Recruiter();
        mockRecruiter.setName(request.getName());
        mockRecruiter.setEmail(request.getEmail());
        mockRecruiter.setCompanyName(request.getCompanyName());

        when(authService.registerRecruiter(any(Recruiter.class))).thenReturn(mockRecruiter);

        ResponseEntity<?> response = authController.registerRecruiter(request);

        assertNotNull(response.getBody());
        Recruiter savedRecruiter = (Recruiter) response.getBody();
        assertEquals("raj@example.com", savedRecruiter.getEmail());
        assertEquals("MyCompany", savedRecruiter.getCompanyName());
    }

    // -------------------- Login Test --------------------
    @Test
    void testLogin_success_jobseeker() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("job@example.com");
        request.setPassword("pass");

        // Mock dependencies
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        // Mark as lenient to avoid UnnecessaryStubbingException
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("job@example.com");

        Jobseeker js = new Jobseeker();
        js.setEmail("job@example.com");
        when(jobSeekerRepository.findByEmail("job@example.com")).thenReturn(Optional.of(js));

        when(jwtService.generateToken(userDetails, "JOBSEEKER", null)).thenReturn("mock-jwt");

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("mock-jwt", jwtResponse.getToken());
        assertEquals("JOBSEEKER", jwtResponse.getRole());
        assertNull(jwtResponse.getCompanyName());
    }


    @Test
    void testLogin_success_recruiter() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("rec@example.com");
        request.setPassword("pass");

        // Use lenient() to avoid unnecessary stubbing exception
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("rec@example.com");

        Recruiter rec = new Recruiter();
        rec.setEmail("rec@example.com");
        rec.setCompanyName("MyCompany");

        lenient().when(jobSeekerRepository.findByEmail("rec@example.com")).thenReturn(Optional.empty());
        lenient().when(recruiterRepository.findByEmail("rec@example.com")).thenReturn(Optional.of(rec));
        lenient().when(jwtService.generateToken(userDetails, "RECRUITER", "MyCompany")).thenReturn("mock-jwt");

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertNotNull(response.getBody());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("mock-jwt", jwtResponse.getToken());
        assertEquals("RECRUITER", jwtResponse.getRole());
        assertEquals("MyCompany", jwtResponse.getCompanyName());
    }


    @Test
    void testLogin_invalid_user() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid@example.com");
        request.setPassword("pass");

        // Use lenient() to avoid unnecessary stubbing warnings
        lenient().when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userDetails);
        lenient().when(userDetails.getUsername()).thenReturn("invalid@example.com");

        lenient().when(jobSeekerRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());
        lenient().when(recruiterRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.login(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid user", response.getBody());
    }


    // -------------------- Get Current User Test --------------------
    @Test
    void testGetCurrentUser_jobseeker() {
        when(userDetails.getUsername()).thenReturn("job@example.com");
        Jobseeker js = new Jobseeker();
        js.setEmail("job@example.com");
        when(jobSeekerRepository.findByEmail("job@example.com")).thenReturn(Optional.of(js));

        ResponseEntity<?> response = authController.getCurrentUser(userDetails);
        assertEquals(js, response.getBody());
    }

    @Test
    void testGetCurrentUser_recruiter() {
        when(userDetails.getUsername()).thenReturn("rec@example.com");
        Recruiter rec = new Recruiter();
        rec.setEmail("rec@example.com");
        when(jobSeekerRepository.findByEmail("rec@example.com")).thenReturn(Optional.empty());
        when(recruiterRepository.findByEmail("rec@example.com")).thenReturn(Optional.of(rec));

        ResponseEntity<?> response = authController.getCurrentUser(userDetails);
        assertEquals(rec, response.getBody());
    }

    @Test
    void testGetCurrentUser_not_found() {
        when(userDetails.getUsername()).thenReturn("unknown@example.com");
        when(jobSeekerRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        when(recruiterRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.getCurrentUser(userDetails);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
    }
}
