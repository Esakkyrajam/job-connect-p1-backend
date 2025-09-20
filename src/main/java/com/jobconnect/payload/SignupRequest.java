package com.jobconnect.payload;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    // For Jobseeker
    private String skills;       // optional
    private String resumeUrl;    // optional, for file upload

    // For Recruiter
    private String companyName;  // optional
}
