package com.jobconnect.controller;

import com.jobconnect.entity.Application;
import com.jobconnect.entity.Job;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.ApplicationRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.JobService;
import com.jobconnect.service.JwtService;
import com.jobconnect.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recruiters")
public class RecruiterController {

    @Autowired
    private RecruiterService recruiterService;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private  ApplicationRepository applicationRepository;
    @Autowired
    private  JwtService jwtService;

    @Autowired
    private JobService jobService;

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getApplications(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String companyName = jwtService.extractCompanyName(token); // ✅ extract companyName from token

        List<Application> applications = applicationRepository.findByCompanyName(companyName);

        return ResponseEntity.ok(applications);
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<String> approveApplication(@PathVariable Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus("APPROVED");
        applicationRepository.save(application);

        return ResponseEntity.ok("Application approved!");
    }

    // ✅ Update Recruiter
    @PutMapping("/recruiter/update")
    public ResponseEntity<?> updateRecruiter(@RequestParam Long id,
                                             @RequestParam String name,
                                             @RequestParam String phoneNumber,
                                             @RequestParam(required = false) String companyName) {
        Recruiter r = recruiterRepository.findById(id).orElseThrow(() -> new RuntimeException("Recruiter not found"));

        r.setName(name);
        r.setPhoneNumber(phoneNumber);
        if (companyName != null) r.setCompanyName(companyName);

        recruiterRepository.save(r);
        return ResponseEntity.ok(r);
    }
    // ✅ Delete Recruiter
    @DeleteMapping("/{id}")
    public String deleteRecruiter(@PathVariable Long id) {
        recruiterService.deleteRecruiter(id);
        return "Recruiter deleted successfully!";
    }

    // (Optional: Get by ID if needed)
    @GetMapping("/{id}")
    public Optional<Recruiter> getRecruiter(@PathVariable Long id) {
        return recruiterService.getRecruiterById(id);
    }

    @GetMapping("/recruiter/jobs")
    public List<Job> getRecruiterJobs(Authentication authentication) {
        String email = authentication.getName();
        return jobService.listJobsByRecruiter(email);
    }

}
