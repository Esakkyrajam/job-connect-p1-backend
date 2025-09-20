package com.jobconnect.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private SecurityFilterChain filterChain;

    @Test
    void contextLoads() {
        assertThat(securityConfig).isNotNull();
    }

    @Test
    void authenticationManagerIsConfigured() throws Exception {
        // Just verify the bean exists
        assertThat(authManager).isNotNull();
    }

    @Test
    void securityFilterChainIsConfigured() throws Exception {
        assertThat(filterChain).isNotNull();
    }
}
