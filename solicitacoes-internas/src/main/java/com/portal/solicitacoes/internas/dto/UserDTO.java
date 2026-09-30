package com.portal.solicitacoes.internas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank(message = "nome não pode ser nulo")
        String username,
        @NotBlank(message = "email não pode ser nulo")
        @Email
        String email,
        @NotBlank(message = "senha não pode ser nula")
        @Size(min = 8, max = 20)
        String password) {
}
