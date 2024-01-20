package com.tinexlab.svbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.tinexlab.svbackend.model.dto.request.AuthenticationRequest;
import com.tinexlab.svbackend.model.dto.request.RegistrationRequest;
import com.tinexlab.svbackend.model.dto.request.ResetPasswordEmailRequest;
import com.tinexlab.svbackend.model.dto.request.ResetPasswordRequest;
import com.tinexlab.svbackend.model.dto.request.SessionRequest;
import com.tinexlab.svbackend.model.dto.response.GenericResponse;
import com.tinexlab.svbackend.service.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@SuppressWarnings({"null"})
public class AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;

    @PreAuthorize("permitAll")
    @PostMapping("/authenticate")
    public ResponseEntity<GenericResponse<?>> login(@RequestBody AuthenticationRequest authRequest){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.login(authRequest)
                );
    }

    @PreAuthorize("permitAll")
    @PostMapping("/register")
    public ResponseEntity<GenericResponse<?>> register(@Valid @RequestBody RegistrationRequest registrationRequest, BindingResult result){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.register(registrationRequest, result)
                );
    }

    @PreAuthorize("permitAll")
    @PatchMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody SessionRequest request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.logout(request)
                );
    }

    @PreAuthorize("permitAll")
    @PostMapping("/send-reset-password-token")
    public ResponseEntity<?> sendResetPasswordToken(@RequestBody ResetPasswordEmailRequest request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.sendResetPasswordToken(request)
                );
    }

    @PreAuthorize("permitAll")
    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .header("custom-status", "OK")
                .contentType(MediaType.APPLICATION_JSON)
                .body(authenticationService.resetPassword(request)
                );
    }

    @PreAuthorize("permitAll")
    @GetMapping("/public-access")
    public String publicAccessEndpoint(){
        return "Este endpoint es público";
    }

}
