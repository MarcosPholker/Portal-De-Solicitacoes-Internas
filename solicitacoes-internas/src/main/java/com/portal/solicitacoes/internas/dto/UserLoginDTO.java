package com.portal.solicitacoes.internas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginDTO(
        @Email
        @NotBlank(message = "email nao pode ser nulo!")
        String email,
        @NotBlank(message = "campo de senha nao pode ser nulo!")
        String password) {
}
