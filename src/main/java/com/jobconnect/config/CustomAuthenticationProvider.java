package com.jobconnect.config;

import com.jobconnect.entity.Jobseeker;
import com.jobconnect.entity.Recruiter;
import com.jobconnect.repository.JobseekerRepository;
import com.jobconnect.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final JobseekerRepository jobseekerRepo;
    private final RecruiterRepository recruiterRepo;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Check Jobseeker
        Optional<Jobseeker> jsOpt = jobseekerRepo.findByEmail(email);
        if(jsOpt.isPresent() && jsOpt.get().getPassword().equals(password)) {
            UserDetails user = new User(email, password, new ArrayList<>());
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        // Check Recruiter
        Optional<Recruiter> rOpt = recruiterRepo.findByEmail(email);
        if(rOpt.isPresent() && rOpt.get().getPassword().equals(password)) {
            UserDetails user = new User(email, password, new ArrayList<>());
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
