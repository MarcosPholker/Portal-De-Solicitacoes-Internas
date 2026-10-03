package com.portal.solicitacoes.internas.dto;

public record DashboardDTO(
        long total,
        long open,
        long inProgress,
        long completed
) {
}