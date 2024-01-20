package com.tinexlab.svbackend.model.dto.request;

import com.tinexlab.svbackend.util.enums.Role;
import com.tinexlab.svbackend.util.enums.UserStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "El nombre de usuario debe tener como longitud entre 8 y 20 caracteres, tener al menos un número y no tener caracteres especiales.")
    private String username;
    @Pattern(regexp = "\\w[a-z0-9_.]+@[a-z0-9_.]+.[a-z]{2,3}.[a-z]{2,3}", message = "Ingresa un correo válido, por favor")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z]).{8,30}$", message = "La contraseña debe tener al menos un número, al menos una letra mayúscula, mínimo 8 caracteres y máximo 30 caracteres.")
    private String password;
    private String name;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String userCreator;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
}
