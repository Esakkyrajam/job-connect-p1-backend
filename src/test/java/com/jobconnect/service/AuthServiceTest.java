package com.jobconnect.service;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private JobseekerRepository jobseekerRepo;

    @Mock
    private RecruiterRepository recruiterRepo;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private AuthService authService;

    private Jobseeker jobseeker;
    private Recruiter recruiter;

    @BeforeEach
    public void setUp() {
        jobseeker = new Jobseeker();
        jobseeker.setName("Test JS");
        jobseeker.setEmail("js@example.com");
        jobseeker.setPassword("password");
        jobseeker.setPhoneNumber("1234567890");
        jobseeker.setSkills("Java, Spring");

        recruiter = new Recruiter();
        recruiter.setName("Test Recruiter");
        recruiter.setPhoneNumber("0987654321");
    }

    @Test
    public void testRegisterJobseeker_Success() throws IOException {
        MultipartFile file = mock(MultipartFile.class);

        when(fileStorageService.saveFile(file)).thenReturn("path/to/resume.pdf");
        when(jobseekerRepo.save(any(Jobseeker.class))).thenAnswer(invocation -> {
            Jobseeker js = invocation.getArgument(0);
            js.setId(1L);
            return js;
        });

        Jobseeker result = authService.registerJobseeker(
                jobseeker.getName(),
                jobseeker.getEmail(),
                jobseeker.getPassword(),
                jobseeker.getPhoneNumber(),
                jobseeker.getSkills(),
                file
        );

        assertNotNull(result);
        assertEquals("Test JS", result.getName());
        assertEquals("path/to/resume.pdf", result.getResumeUrl());
        verify(fileStorageService, times(1)).saveFile(file);
        verify(jobseekerRepo, times(1)).save(any(Jobseeker.class));
        verify(smsService, times(1)).sendSms(eq(jobseeker.getPhoneNumber()), contains("Welcome"));
    }

    @Test
    public void testRegisterRecruiter_Success() {
        when(recruiterRepo.save(recruiter)).thenAnswer(invocation -> {
            Recruiter r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        Recruiter result = authService.registerRecruiter(recruiter);

        assertNotNull(result);
        assertEquals("Test Recruiter", result.getName());
        verify(recruiterRepo, times(1)).save(recruiter);
        verify(smsService, times(1)).sendSms(eq(recruiter.getPhoneNumber()), contains("Welcome"));
    }
}
