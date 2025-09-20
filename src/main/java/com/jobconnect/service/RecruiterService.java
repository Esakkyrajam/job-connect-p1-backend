package com.jobconnect.service;

import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.RecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterService {

    @Autowired
    private RecruiterRepository recruiterRepo;

    public Recruiter updateRecruiter(Long id, Recruiter details) {
        Recruiter recruiter = recruiterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        recruiter.setName(details.getName());
        recruiter.setEmail(details.getEmail());
        recruiter.setCompanyName(details.getCompanyName());

        return recruiterRepo.save(recruiter);
    }

    public void deleteRecruiter(Long id) {
        recruiterRepo.deleteById(id);
    }

    public Optional<Recruiter> getRecruiterById(Long id) {
        return recruiterRepo.findById(id);
    }
}
