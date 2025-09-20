package com.jobconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Jobseeker jobseeker;

    @ManyToOne
    private Job job;

    private String status; // Applied, Shortlisted, Rejected, Hired


}
