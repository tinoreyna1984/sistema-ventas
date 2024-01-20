package com.tinexlab.svbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.tinexlab.svbackend.config.implementation.CustomDaoAuthenticationProvider;
import com.tinexlab.svbackend.config.implementation.CustomUserDetailsService;
import com.tinexlab.svbackend.repository.UserRepository;

@Component
public class SecurityBeansInjector {

    @Autowired
    private UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // implementación con clases Custom
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(userRepository);
            return customUserDetailsService.loadUserByUsername(username);
        };
    }

    // implementación con clases Custom
    @Bean
    public CustomDaoAuthenticationProvider authenticationProvider(){
        CustomDaoAuthenticationProvider provider = new CustomDaoAuthenticationProvider(userRepository);
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

}
