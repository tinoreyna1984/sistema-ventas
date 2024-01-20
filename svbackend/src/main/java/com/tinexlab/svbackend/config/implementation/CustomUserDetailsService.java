package com.tinexlab.svbackend.config.implementation;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tinexlab.svbackend.model.entity.User;
import com.tinexlab.svbackend.repository.UserRepository;

// implementación con clases Custom
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    //!! No usar @Autowired, generar constructor con userRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).get();
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new CustomUserDetails(user); // implementación con clases Custom
    }
}
