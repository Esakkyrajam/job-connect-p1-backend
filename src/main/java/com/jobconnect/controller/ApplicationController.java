



package com.jobconnect.controller;

import com.jobconnect.entity.Application;
import com.jobconnect.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService appService;

    // ✅ Apply to a job using JWT (email from token)
    @PostMapping("/{jobId}/apply")
    public Application apply(@PathVariable Long jobId, Authentication authentication) {
        String email = authentication.getName(); // email from JWT
        return appService.applyToJob(jobId, email);
    }

    // ✅ Get my applications (jobseeker)
    @GetMapping("/my-applications")
    public List<Application> getMyApplications(Authentication authentication) {
        String email = authentication.getName();
        return appService.getJobseekerApplicationsByEmail(email);
    }

    // ✅ Get all applications for a job (recruiter)
    @GetMapping("/job/{jobId}")
    public List<Application> getJobApps(@PathVariable Long jobId) {
        return appService.getJobApplications(jobId);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveApplication(@PathVariable Long id) {
        appService.approveApplication(id);
        return ResponseEntity.ok("Candidate approved!");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectApplication(@PathVariable Long id) {
        appService.rejectApplication(id);
        return ResponseEntity.ok("Candidate rejected!");
    }
}
