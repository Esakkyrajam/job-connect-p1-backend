package com.jobconnect.controller;

import com.jobconnect.entity.Job;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private JobRepository jobRepository;

//    @PostMapping
//    public Job postJob(@RequestBody Job job) { return jobService.postJob(job); }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<String> applyForJob(@PathVariable Long jobId, Authentication authentication) {
        String userEmail = authentication.getName(); // jobseeker email from JWT

        jobService.applyForJob(jobId, userEmail);

        return ResponseEntity.ok("Applied successfully!");
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<Job> updateJob(@PathVariable Long jobId,
                                         @RequestBody Job jobDetails,
                                         Authentication authentication) {
        String recruiterEmail = authentication.getName();
        Job updatedJob = jobService.updateJob(jobId, jobDetails, recruiterEmail);
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping("/recruiters/my-jobs")
    public List<Job> getMyJobs(Authentication authentication) {
        String recruiterEmail = authentication.getName(); // from JWT
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return jobRepository.findByRecruiter(recruiter); // ✅ filter by recruiter
    }


    @GetMapping
    public List<Job> listJobs() { return jobService.listJobs(); }

    @GetMapping("/search")
    public List<Job> searchJobs(@RequestParam String title, @RequestParam String location) {
        return jobService.searchJobs(title, location);
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job, Authentication authentication) {
        // authentication.getName() = recruiter email from JWT
        String recruiterEmail = authentication.getName();

        Job savedJob = jobService.createJob(job, recruiterEmail);
        return ResponseEntity.ok(savedJob);
    }


//    @PutMapping("/{jobId}")
//    public ResponseEntity<Job> updateJob(
//            @PathVariable Long jobId,
//            @RequestBody Job updatedJob,
//            Authentication authentication) {
//
//        // Get recruiter email from JWT
//        String recruiterEmail = authentication.getName();
//
//        Job job = jobService.updateJob(jobId, updatedJob, recruiterEmail);
//
//        return ResponseEntity.ok(job);
//    }


}
