package com.jobconnect.service;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AuthService {

    @Autowired
    private JobseekerRepository jobseekerRepo;
    @Autowired private RecruiterRepository recruiterRepo;
    @Autowired private FileStorageService fileStorageService;
    @Autowired private SmsService smsService;

    public Jobseeker registerJobseeker(String name, String email, String password, String phone, String skills, MultipartFile resume) throws IOException {
        String resumePath = fileStorageService.saveFile(resume);

        Jobseeker js = new Jobseeker();
        js.setName(name); js.setEmail(email); js.setPassword(password);
        js.setPhoneNumber(phone); js.setSkills(skills); js.setResumeUrl(resumePath);

        Jobseeker savedJs = jobseekerRepo.save(js);

        // Send SMS
        smsService.sendSms(phone, "Welcome " + name + "! Registration successful.");

        return savedJs;
    }

    public Recruiter registerRecruiter(Recruiter r) {
        Recruiter savedR = recruiterRepo.save(r);
        smsService.sendSms(r.getPhoneNumber(), "Welcome " + r.getName() + "! Registration successful.");
        return savedR;
    }
}
