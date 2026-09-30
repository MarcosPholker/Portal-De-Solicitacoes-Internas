package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.RequestDTO;
import com.portal.solicitacoes.internas.entity.Request;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.enuns.RequestStatus;
import com.portal.solicitacoes.internas.repositories.RequestRepository;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
    import java.util.Optional;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;


    public RequestService(RequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    public Request createRequest(RequestDTO requestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("usuario nao esta logado"));
            Request request = new Request();
            request.setTitle(requestDTO.title());
            request.setDescription(requestDTO.description());
            request.setRequestCategory(requestDTO.requestCategory());
            request.setCreationDate(LocalDateTime.now());
            request.setRequestStatus(RequestStatus.OPEN);
            request.setUser(user);
            return requestRepository.save(request);
    }
}
