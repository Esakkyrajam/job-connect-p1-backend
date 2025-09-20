package com.jobconnect.controller;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.payload.JwtResponse;
import com.jobconnect.payload.LoginRequest;
import com.jobconnect.payload.SignupRequest;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import com.jobconnect.service.AuthService;
import com.jobconnect.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private  final RecruiterRepository recruiterRepository;

    private final JobseekerRepository jobSeekerRepository;




    // ----------------- Jobseeker Registration -----------------
    @PostMapping("/register/jobseeker")
    public ResponseEntity<?> registerJobseeker(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String skills,
            @RequestParam("resume") MultipartFile resume) {

        try {
            Jobseeker js = authService.registerJobseeker(name, email, password, phoneNumber, skills, resume);
            return ResponseEntity.ok(js);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Resume upload failed");
        }
    }

    // ----------------- Recruiter Registration -----------------
    @PostMapping("/register/recruiter")
    public ResponseEntity<?> registerRecruiter(@RequestBody SignupRequest signupRequest) {

        Recruiter recruiter = new Recruiter();
        recruiter.setName(signupRequest.getName());
        recruiter.setEmail(signupRequest.getEmail());
        recruiter.setPassword(signupRequest.getPassword());
        recruiter.setPhoneNumber(signupRequest.getPhoneNumber());
        recruiter.setCompanyName(signupRequest.getCompanyName());

        Recruiter savedR = authService.registerRecruiter(recruiter);
        return ResponseEntity.ok(savedR);
    }

    // ----------------- Login -----------------
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String jwt = jwtService.generateToken((UserDetails) authentication.getPrincipal());
//        return ResponseEntity.ok(new JwtResponse(jwt));
    //}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role;
        String companyName = null; // default null

        // ✅ Check Jobseeker
        if (jobSeekerRepository.findByEmail(request.getEmail()).isPresent()) {
            role = "JOBSEEKER";
        }
        // ✅ Check Recruiter
        else if (recruiterRepository.findByEmail(request.getEmail()).isPresent()) {
            role = "RECRUITER";
            Recruiter recruiter = recruiterRepository.findByEmail(request.getEmail()).get();
            companyName = recruiter.getCompanyName(); // ✅ fetch company name
        }
        else {
            return ResponseEntity.badRequest().body("Invalid user");
        }

        // ✅ Generate token with role + companyName
        String jwt = jwtService.generateToken(userDetails, role, companyName);

        // ✅ Return both in response
        return ResponseEntity.ok(new JwtResponse(jwt, role, companyName));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // Check if Jobseeker
        Optional<Jobseeker> jsOpt = jobSeekerRepository.findByEmail(userDetails.getUsername());
        if (jsOpt.isPresent()) return ResponseEntity.ok(jsOpt.get());

        // Check if Recruiter
        Optional<Recruiter> rOpt = recruiterRepository.findByEmail(userDetails.getUsername());
        if (rOpt.isPresent()) return ResponseEntity.ok(rOpt.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

}





