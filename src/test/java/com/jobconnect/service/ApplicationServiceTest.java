package com.jobconnect.service;

import com.jobconnect.entity.Application;
import com.jobconnect.entity.Job;
import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.ApplicationRepository;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.JobseekerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository appRepo;

    @Mock
    private JobRepository jobRepo;

    @Mock
    private JobseekerRepository jsRepo;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private ApplicationService applicationService;

    private Job job;
    private Jobseeker jobseeker;
    private Application application;

    @BeforeEach
    public void setUp() {
        job = new Job();
        job.setId(1L);

        jobseeker = new Jobseeker();
        jobseeker.setId(1L);
        jobseeker.setEmail("test@example.com");
        jobseeker.setName("Test User");
        jobseeker.setPhoneNumber("1234567890");

        application = new Application();
        application.setId(1L);
        application.setJob(job);
        application.setJobseeker(jobseeker);
        application.setStatus("Applied");
    }

    @Test
    public void testApplyToJob_Success() {
        when(jobRepo.findById(1L)).thenReturn(Optional.of(job));
        when(jsRepo.findByEmail("test@example.com")).thenReturn(Optional.of(jobseeker));
        when(appRepo.findByJobIdAndJobseekerId(1L, 1L)).thenReturn(Optional.empty());
        when(appRepo.save(any(Application.class))).thenReturn(application);

        Application result = applicationService.applyToJob(1L, "test@example.com");

        assertNotNull(result);
        assertEquals("Applied", result.getStatus());
        verify(appRepo, times(1)).save(any(Application.class));
    }

    @Test
    public void testGetJobseekerApplicationsByEmail_Success() {
        when(jsRepo.findByEmail("test@example.com")).thenReturn(Optional.of(jobseeker));
        when(appRepo.findByJobseekerId(1L)).thenReturn(Collections.singletonList(application));

        List<Application> result = applicationService.getJobseekerApplicationsByEmail("test@example.com");

        assertEquals(1, result.size());
        assertEquals(application, result.get(0));
    }

    @Test
    public void testGetJobApplications_Success() {
        when(appRepo.findByJobId(1L)).thenReturn(Collections.singletonList(application));

        List<Application> result = applicationService.getJobApplications(1L);

        assertEquals(1, result.size());
        assertEquals(application, result.get(0));
    }

    @Test
    public void testApproveApplication_Success() {
        when(appRepo.findById(1L)).thenReturn(Optional.of(application));
        when(appRepo.save(application)).thenReturn(application);

        applicationService.approveApplication(1L);

        assertEquals("APPROVED", application.getStatus());
        verify(smsService, times(1)).sendSms(eq(jobseeker.getPhoneNumber()), contains("APPROVED"));
    }

    @Test
    public void testRejectApplication_Success() {
        when(appRepo.findById(1L)).thenReturn(Optional.of(application));
        when(appRepo.save(application)).thenReturn(application);

        applicationService.rejectApplication(1L);

        assertEquals("REJECTED", application.getStatus());
        verify(smsService, times(1)).sendSms(eq(jobseeker.getPhoneNumber()), contains("REJECTED"));
    }

}
