package com.tinexlab.svbackend.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
public class ResetPasswordEmailRequest {
    @Email
    private String email;
}
