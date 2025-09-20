package com.jobconnect.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    private JwtService jwtService;
    private String secretKey = "Hh2f8G4k5Jk9Lw3M1Zq7R8xT9vU0yP6aB4cD3eF1gH2jK3lM4nO5pQ6rS7tU8vW9"; // 64 chars = 512 bits

    @BeforeEach
    public void setup() throws Exception {
        jwtService = new JwtService();

        // Set private field 'secret' using reflection
        Field secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtService, secretKey);

        // Set private field 'expiration' using reflection
        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1000 * 60 * 60L); // 1 hour expiration
    }

    private UserDetails createTestUser(String username) {
        return new UserDetails() {
            @Override public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptyList();
            }
            @Override public String getPassword() { return null; }
            @Override public String getUsername() { return username; }
            @Override public boolean isAccountNonExpired() { return true; }
            @Override public boolean isAccountNonLocked() { return true; }
            @Override public boolean isCredentialsNonExpired() { return true; }
            @Override public boolean isEnabled() { return true; }
        };
    }

    @Test
    public void testGenerateAndValidateToken() {
        UserDetails userDetails = createTestUser("test@example.com");

        String token = jwtService.generateToken(userDetails, "RECRUITER", "TestCompany");

        assertNotNull(token);
        assertEquals("test@example.com", jwtService.extractUsername(token));
        assertEquals("RECRUITER", jwtService.extractRole(token));
        assertEquals("TestCompany", jwtService.extractCompanyName(token));
        assertTrue(jwtService.validateToken(token, userDetails));
    }

    @Test
    public void testTokenExpiration() throws Exception {
        // Set expiration to 1 second for this test
        Field expirationField = JwtService.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtService, 1000L); // 1 second expiration

        UserDetails userDetails = createTestUser("test@example.com");

        String token = jwtService.generateToken(userDetails, "JOBSEEKER", null);
        assertNotNull(token);

        // Wait for the token to expire
        Thread.sleep(1500);

        // Validate that an ExpiredJwtException is thrown
        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.validateToken(token, userDetails);
        });
    }

    @Test
    public void testExtractClaims() throws Exception {
        UserDetails userDetails = createTestUser("user@example.com");

        String token = jwtService.generateToken(userDetails, "ADMIN", "AdminCorp");

        // ✅ Use the same secret key as jwtService for parsing
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("user@example.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
        assertEquals("AdminCorp", claims.get("companyName"));
        assertTrue(claims.getExpiration().after(new Date()));
    }
}
