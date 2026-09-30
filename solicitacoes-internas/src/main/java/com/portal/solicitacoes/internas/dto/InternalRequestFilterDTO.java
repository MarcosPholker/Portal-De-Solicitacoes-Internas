package com.portal.solicitacoes.internas.dto;

import com.portal.solicitacoes.internas.enuns.InternalRequestCategory;
import com.portal.solicitacoes.internas.enuns.InternalRequestStatus;

import java.time.LocalDate;

public record InternalRequestFilterDTO(
        String title,
        InternalRequestCategory category,
        InternalRequestStatus status,
        LocalDate startDate,
        LocalDate endDate
) {
}