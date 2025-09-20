package com.jobconnect.controller;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.service.FileStorageService;
import com.jobconnect.service.JobseekerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/jobseekers")
public class JobseekerController {

    @Autowired
    private JobseekerService jobSeekerService;

    @Autowired
    private JobseekerRepository jobSeekerRepository;

    @Autowired
    FileStorageService fileStorageService;



    @PutMapping("/jobseeker/update")
    public ResponseEntity<?> updateJobseeker(@RequestParam Long id,
                                             @RequestParam String name,
                                             @RequestParam String phoneNumber,
                                             @RequestParam(required = false) String skills,
                                             @RequestParam(value = "resume", required = false) MultipartFile resume) throws IOException {
        Jobseeker js = jobSeekerRepository.findById(id).orElseThrow(() -> new RuntimeException("Jobseeker not found"));

        js.setName(name);
        js.setPhoneNumber(phoneNumber);
        if (skills != null) js.setSkills(skills);

        if (resume != null) {
            String resumePath = fileStorageService.saveFile(resume);
            js.setResumeUrl(resumePath);
        }

        jobSeekerRepository.save(js);
        return ResponseEntity.ok(js);
    }

    // ✅ Delete JobSeeker
    @DeleteMapping("/{id}")
    public String deleteJobSeeker(@PathVariable Long id) {
        jobSeekerService.deleteJobSeeker(id);
        return "JobSeeker deleted successfully!";
    }

    // (Optional: Get by ID if needed)
    @GetMapping("/{id}")
    public Optional<Jobseeker> getJobSeeker(@PathVariable Long id) {
        return jobSeekerService.getJobSeekerById(id);
    }
}
