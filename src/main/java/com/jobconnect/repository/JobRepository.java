package com.jobconnect.repository;


import com.jobconnect.entity.Job;
import com.jobconnect.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByTitleContainingAndLocationContaining(String title, String location);
    List<Job> findByRecruiter(Recruiter recruiter);
}
