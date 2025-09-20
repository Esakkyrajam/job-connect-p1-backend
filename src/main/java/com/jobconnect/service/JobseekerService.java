package com.jobconnect.service;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.JobseekerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobseekerService {

    @Autowired
    private JobseekerRepository jobSeekerRepo;

    public Jobseeker updateJobSeeker(Long id, Jobseeker details) {
        Jobseeker jobSeeker = jobSeekerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("JobSeeker not found"));

        if (details.getName() != null) jobSeeker.setName(details.getName());
        if (details.getEmail() != null) jobSeeker.setEmail(details.getEmail());
        if (details.getSkills() != null) jobSeeker.setSkills(details.getSkills());
        if (details.getPhoneNumber() != null) jobSeeker.setPhoneNumber(details.getPhoneNumber());
        if (details.getResumeUrl() != null) jobSeeker.setResumeUrl(details.getResumeUrl());

        return jobSeekerRepo.save(jobSeeker);
    }




    public void deleteJobSeeker(Long id) {
        jobSeekerRepo.deleteById(id);
    }

    public Optional<Jobseeker> getJobSeekerById(Long id) {
        return jobSeekerRepo.findById(id);
    }
}
