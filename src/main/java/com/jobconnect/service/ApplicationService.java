//package com.jobconnect.service;
//
//import com.jobconnect.entity.Application;
//import com.jobconnect.entity.Job;
//import com.jobconnect.entity.Jobseeker;
//import com.jobconnect.repository.ApplicationRepository;
//import com.jobconnect.repository.JobRepository;
//import com.jobconnect.repository.JobseekerRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ApplicationService {
//
//    @Autowired
//    private ApplicationRepository appRepo;
//    @Autowired private JobRepository jobRepo;
//    @Autowired private JobseekerRepository jsRepo;
////    @Autowired private SmsService smsService;
////
////    public Application applyToJob(Long jobId, Long jobseekerId) {
////        Job job = jobRepo.findById(jobId).orElseThrow();
////        Jobseeker js = jsRepo.findById(jobseekerId).orElseThrow();
////
////        Application app = new Application();
////        app.setJob(job); app.setJobseeker(js); app.setStatus("Applied");
////        Application savedApp = appRepo.save(app);
//
//
//    public Application applyToJob(Long jobId, String email) {
//        Job job = jobRepo.findById(jobId).orElseThrow();
//        Jobseeker js = jsRepo.findByEmail(email).orElseThrow();
//
//        // Prevent duplicate applications
//        appRepo.findByJobIdAndJobseekerId(jobId, js.getId()).ifPresent(a -> {
//            throw new RuntimeException("Already applied for this job");
//        });
//
//        Application app = new Application();
//        app.setJob(job);
//        app.setJobseeker(js);
//        app.setStatus("Applied");
//
//        return appRepo.save(app);
//    }
//
//
//
////        // SMS to jobseeker
////        smsService.sendSms(js.getPhoneNumber(), "Applied for " + job.getTitle() + " at " + job.getLocation());
////
////        // SMS to recruiter
////        smsService.sendSms(job.getRecruiter().getPhoneNumber(), js.getName() + " applied for " + job.getTitle());
//
////        return savedApp;
////    }
//
//    public List<Application> getJobseekerApplications(Long jobseekerId) {
//        return appRepo.findByJobseekerId(jobseekerId);
//    }
//
//    public List<Application> getJobApplications(Long jobId) {
//        return appRepo.findByJobId(jobId);
//    }
//}
package com.jobconnect.service;

import com.jobconnect.entity.Application;
import com.jobconnect.entity.Job;
import com.jobconnect.entity.Jobseeker;
import com.jobconnect.repository.JobRepository;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository appRepo;
    @Autowired
    private JobRepository jobRepo;
    @Autowired
    private JobseekerRepository jsRepo;
    @Autowired
    private SmsService smsService;

    // ✅ Apply to job by email from JWT
    public Application applyToJob(Long jobId, String email) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        Jobseeker js = jsRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jobseeker not found"));

        // Prevent duplicate applications
        appRepo.findByJobIdAndJobseekerId(jobId, js.getId())
                .ifPresent(a -> { throw new RuntimeException("Already applied for this job"); });

        Application app = new Application();
        app.setJob(job);
        app.setJobseeker(js);
        app.setStatus("Applied");

        return appRepo.save(app);
    }

    // ✅ Get applications for jobseeker by email
    public List<Application> getJobseekerApplicationsByEmail(String email) {
        Jobseeker js = jsRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Jobseeker not found"));
        return appRepo.findByJobseekerId(js.getId());
    }

    // ✅ Get applications for a job
    public List<Application> getJobApplications(Long jobId) {
        return appRepo.findByJobId(jobId);
    }

    public void approveApplication(Long id) {
        Application application = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus("APPROVED");

        appRepo.save(application);
        // Send SMS notification
        String message = "Hello " + application.getJobseeker().getName() + ", your application has been APPROVED.";
        String phone = application.getJobseeker().getPhoneNumber();
        smsService.sendSms(phone, message);
    }

    public void rejectApplication(Long id) {
        Application application = appRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus("REJECTED");
        appRepo.save(application);
        // Send SMS notification
        String message = "Hello " + application.getJobseeker().getName() + ", your application has been REJECTED.";
        String phone = application.getJobseeker().getPhoneNumber();
        smsService.sendSms(phone, message);
    }
}
