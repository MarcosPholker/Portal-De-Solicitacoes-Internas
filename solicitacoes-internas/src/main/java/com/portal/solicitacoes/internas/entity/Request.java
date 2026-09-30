package com.portal.solicitacoes.internas.entity;

import com.portal.solicitacoes.internas.enuns.RequestCategory;
import com.portal.solicitacoes.internas.enuns.RequestStatus;
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
public class Request {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;
    private RequestCategory requestCategory;
    private LocalDateTime creationDate;
    private RequestStatus requestStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
