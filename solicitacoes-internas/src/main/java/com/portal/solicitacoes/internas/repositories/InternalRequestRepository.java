package com.portal.solicitacoes.internas.repositories;

import com.portal.solicitacoes.internas.entity.InternalRequest;
import com.portal.solicitacoes.internas.enuns.InternalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InternalRequestRepository extends JpaRepository<InternalRequest, UUID> {
    long countByInternalRequestStatus(
            InternalRequestStatus status);
}
