package com.jobconnect.service;

import com.jobconnect.entity.Job;
import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    @Autowired
    private JobseekerRepository jobseekerRepository;



    public Job postJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> listJobs() {
        return jobRepository.findAll();
    }

    public List<Job> searchJobs(String title, String location) {
        return jobRepository.findByTitleContainingAndLocationContaining(title, location);
    }

    public Job createJob(Job job, String recruiterEmail) {
        // find recruiter by email (from JWT)
        Recruiter recruiter = recruiterRepository.findByEmail(recruiterEmail)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        // set recruiter to job
        job.setRecruiter(recruiter);

        return jobRepository.save(job);
    }

    public void applyForJob(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Jobseeker jobseeker = jobseekerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jobseeker not found"));

        // You can create a JobApplication entity, or just log for now
        System.out.println(jobseeker.getName() + " applied for " + job.getTitle());
    }

//    public Job updateJob(Long jobId, Job updatedJob, String recruiterEmail) {
//        // Fetch existing job
//        Job existingJob = jobRepository.findById(jobId)
//                .orElseThrow(() -> new RuntimeException("Job not found"));
//
//        // Check if the logged-in recruiter is the owner
//        if (!existingJob.getRecruiter().getEmail().equals(recruiterEmail)) {
//            throw new RuntimeException("You are not authorized to update this job");
//        }
//
//        // Update fields
//        existingJob.setTitle(updatedJob.getTitle());
//        existingJob.setDescription(updatedJob.getDescription());
//        existingJob.setLocation(updatedJob.getLocation());
//        existingJob.setSkillsRequired(updatedJob.getSkillsRequired());
//        existingJob.setSalary(updatedJob.getSalary());
//
//        return jobRepository.save(existingJob);
//    }


    public Job updateJob(Long jobId, Job jobDetails, String recruiterEmail) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        // Check if the logged-in recruiter owns this job
        if (!job.getRecruiter().getEmail().equals(recruiterEmail)) {
            throw new RuntimeException("Unauthorized: You can only update your own jobs");
        }

        job.setTitle(jobDetails.getTitle());
        job.setLocation(jobDetails.getLocation());
        job.setSkillsRequired(jobDetails.getSkillsRequired());
        job.setSalary(jobDetails.getSalary());
        job.setDescription(jobDetails.getDescription());

        return jobRepository.save(job);
    }
    public List<Job> listJobsByRecruiter(String email) {
        Recruiter recruiter = recruiterRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));
        return jobRepository.findByRecruiter(recruiter);
    }



}
