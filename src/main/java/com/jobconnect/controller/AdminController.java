package com.jobconnect.controller;

import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

// AdminController.java
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final JobseekerRepository jobseekerRepository;
    private final RecruiterRepository recruiterRepository;

    @GetMapping("/counts")
    public ResponseEntity<?> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("jobseekers", jobseekerRepository.count());
        counts.put("recruiters", recruiterRepository.count());
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/jobseekers")
    public ResponseEntity<?> getJobseekers() {
        return ResponseEntity.ok(jobseekerRepository.findAll());
    }

    @GetMapping("/recruiters")
    public ResponseEntity<?> getRecruiters() {
        return ResponseEntity.ok(recruiterRepository.findAll());
    }
}
