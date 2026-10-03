package com.portal.solicitacoes.internas.dto;

import com.portal.solicitacoes.internas.enums.InternalRequestCategory;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record InternalRequestListDTO(
        UUID id,
        String title,
        InternalRequestCategory internalRequestCategory,
        String username,
        LocalDateTime creationDate,
        InternalRequestStatus internalRequestStatus
) {
}