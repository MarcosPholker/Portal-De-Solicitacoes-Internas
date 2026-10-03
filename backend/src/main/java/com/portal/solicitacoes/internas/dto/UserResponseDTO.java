package com.portal.solicitacoes.internas.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String username
) {
}
