package com.portal.solicitacoes.internas.dto;

import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.enuns.RequestCategory;
import com.portal.solicitacoes.internas.enuns.RequestStatus;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;
import java.util.UUID;

public record RequestDTO(String title,
                         String description,
                         RequestCategory requestCategory,
                         LocalDateTime creationDate,
                         RequestStatus requestStatus,
                         User user) {
}
