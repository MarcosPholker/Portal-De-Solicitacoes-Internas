package com.portal.solicitacoes.internas.repositories;

import com.portal.solicitacoes.internas.entity.InternalRequest;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InternalRequestRepository extends JpaRepository<InternalRequest, UUID> {
    long countByInternalRequestStatus(
            InternalRequestStatus status);
    List<InternalRequest> findAllByUser(User user);
}
