package com.portal.solicitacoes.internas.exception;

import java.util.Map;

public record ErrorResponse(
        int status,
        String message,
        Map<String, String> errors
) {
}