package com.tinexlab.svbackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.tinexlab.svbackend.config.filter.JwtAuthenticationFilter;
import com.tinexlab.svbackend.config.implementation.CustomDaoAuthenticationProvider;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class HttpSecurityConfig {

    @Autowired
    private CustomDaoAuthenticationProvider customDaoAuthenticationProvider;

    @Autowired
    private JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf( csrfConfig -> csrfConfig.disable() )
                .sessionManagement( sessionMangConfig ->
                        sessionMangConfig
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //.authenticationProvider(authenticationProvider)
                .authenticationProvider(customDaoAuthenticationProvider) // implementación con clases Custom
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // .authorizeHttpRequests(builderRequestMatchers())
        ;

        return http.build();
    }

    /*private static Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> builderRequestMatchers() {
        return authConfig -> {

            authConfig.requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll();
            authConfig.requestMatchers(HttpMethod.GET, "/auth/public-access").permitAll();
            authConfig.requestMatchers("/error").permitAll();

            authConfig.requestMatchers(HttpMethod.GET, "/products").hasAuthority(Permission.READ_ALL_PRODUCTS.name());
            authConfig.requestMatchers(HttpMethod.POST, "/products").hasAuthority(Permission.SAVE_ONE_PRODUCT.name());

            authConfig.anyRequest().denyAll();
        };
    }*/

}
