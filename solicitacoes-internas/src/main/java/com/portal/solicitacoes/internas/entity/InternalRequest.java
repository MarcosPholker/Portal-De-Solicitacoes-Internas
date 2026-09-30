package com.portal.solicitacoes.internas.entity;

import com.portal.solicitacoes.internas.enuns.InternalRequestCategory;
import com.portal.solicitacoes.internas.enuns.InternalRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternalRequest {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private InternalRequestCategory internalRequestCategory;
    private LocalDateTime creationDate;
    private InternalRequestStatus internalRequestStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
