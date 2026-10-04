package com.portal.solicitacoes.internas.dto;

import com.portal.solicitacoes.internas.entity.InternalRequest;
import com.portal.solicitacoes.internas.enums.InternalRequestCategory;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record InternalRequestDTO(

        UUID id,

        @NotBlank(message = "Título não pode ser vazio!")
        @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
        String title,

        @NotBlank(message = "Descrição não pode ser vazia!")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String description,

        @NotNull(message = "Categoria da solicitação não pode ser nula!")
        InternalRequestCategory internalRequestCategory,

        LocalDateTime creationDate,

        InternalRequestStatus internalRequestStatus,

        UserResponseDTO userResponseDTO

) {

        public InternalRequestDTO(InternalRequest internalRequest) {

                this(
                        internalRequest.getId(),
                        internalRequest.getTitle(),
                        internalRequest.getDescription(),
                        internalRequest.getInternalRequestCategory(),
                        internalRequest.getCreationDate(),
                        internalRequest.getInternalRequestStatus(),
                        new UserResponseDTO(
                                internalRequest.getUser().getId(),
                                internalRequest.getUser().getUsername(), 
                                internalRequest.getUser().getEmail())
                );

        }
}
