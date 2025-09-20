package com.jobconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000) // longer descriptions allowed
    private String description;

    private String location;

    private String skillsRequired;

    private Double salary;

    @ManyToOne
    @JoinColumn(name = "recruiter_id", nullable = false)
    @JsonIgnoreProperties({"password"})  // ✅ hide recruiter password in JSON
    private Recruiter recruiter;
}
