package com.jobconnect.repository;

import com.jobconnect.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByJobseekerId(Long jobseekerId);

    List<Application> findByJobId(Long jobId);

    Optional<Application> findByJobIdAndJobseekerId(Long jobId, Long jobseekerId);

    @Query("SELECT a FROM Application a WHERE a.job.recruiter.companyName = :companyName")
    List<Application> findByCompanyName(@Param("companyName") String companyName);
}
