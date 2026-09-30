package com.portal.solicitacoes.internas.dto;

import com.portal.solicitacoes.internas.enuns.InternalRequestCategory;
import com.portal.solicitacoes.internas.enuns.InternalRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record InternalRequestDTO(

        UUID id,

        @NotBlank(message = "Título não pode ser vazio!")
        @Size(max = 100, message = "título deve ter no máximo 100 caracteres")
        String title,

        @NotBlank(message = "Descrição não pode ser vazia!")
        @Size(max = 500, message = "descrição deve ter no máximo 500 caracteres")
        String description,

        @NotBlank(message = "A categoria não pode estar vazia!")
        @NotNull(message = "categoria da solicitação não pode ser nula!")
        InternalRequestCategory internalRequestCategory,

        LocalDateTime creationDate,

        InternalRequestStatus internalRequestStatus,
        
        UserResponseDTO userResponseDTO

) {
}
