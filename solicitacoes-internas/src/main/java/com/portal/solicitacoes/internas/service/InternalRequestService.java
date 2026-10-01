package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.*;
import com.portal.solicitacoes.internas.entity.InternalRequest;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import com.portal.solicitacoes.internas.exception.NotFoundRequestException;
import com.portal.solicitacoes.internas.exception.UserNotFoundException;
import com.portal.solicitacoes.internas.exception.UserNotLoggedInException;
import com.portal.solicitacoes.internas.repositories.InternalRequestRepository;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InternalRequestService {
    private final InternalRequestRepository internalRequestRepository;
    private final UserRepository userRepository;

    public InternalRequestService(InternalRequestRepository internalRequestRepository, UserRepository userRepository) {
        this.internalRequestRepository = internalRequestRepository;
        this.userRepository = userRepository;
    }

    public InternalRequestDTO createRequest(InternalRequestDTO internalRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Usuario não encontrado!"));

        InternalRequest internalRequest = new InternalRequest();
        internalRequest.setTitle(internalRequestDTO.title());
        internalRequest.setDescription(internalRequestDTO.description());
        internalRequest.setInternalRequestCategory(internalRequestDTO.internalRequestCategory());
        internalRequest.setCreationDate(LocalDateTime.now());
        internalRequest.setInternalRequestStatus(InternalRequestStatus.OPEN);
        internalRequest.setUser(user);

        UserResponseDTO userResponseDTO = new UserResponseDTO(
                internalRequest.getUser().getId(),
                internalRequest.getUser().getUsername()
        );

        internalRequestRepository.save(internalRequest);

        return new InternalRequestDTO(
                internalRequest.getId(),
                internalRequest.getTitle(),
                internalRequest.getDescription(),
                internalRequest.getInternalRequestCategory(),
                internalRequest.getCreationDate(),
                internalRequest.getInternalRequestStatus(),
                userResponseDTO
        );
    }

    public InternalRequest updateRequest(UUID id, InternalRequestDTO internalRequestDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotLoggedInException("Usuario não esta authenticado"));

        InternalRequest internalRequest = internalRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundRequestException("Solicitação não encontrada"));

        if (!internalRequest.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Você não pode alterar uma solicitação de outro usuário"
            );
        }


        internalRequest.setTitle(internalRequestDTO.title());
        internalRequest.setDescription(internalRequestDTO.description());
        internalRequest.setInternalRequestCategory(internalRequestDTO.internalRequestCategory());

        return internalRequestRepository.save(internalRequest);
    }

    public void delete(UUID id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotLoggedInException("Usuario não esta authenticado"));
        InternalRequest internalRequest = internalRequestRepository.findById(id).orElseThrow(() -> new NotFoundRequestException("Solicitação nao encontrada"));

        if (!internalRequest.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Você não pode excluir uma solicitação de outro usuário");
        }

        internalRequestRepository.deleteById(id);
    }


    public InternalRequest changeStatus(UUID id, InternalRequestStatus status) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotLoggedInException("Usuario não está autenticado"));

        InternalRequest internalRequest = internalRequestRepository.findById(id).orElseThrow(() -> new NotFoundRequestException("Solicitação não encontrada"));

        if (!internalRequest.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "Você não pode alterar o status de uma solicitação de outro usuário"
            );
        }

        internalRequest.setInternalRequestStatus(status);

        return internalRequestRepository.save(internalRequest);
    }

    public InternalRequestDTO internalRequestDetails(UUID id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        userRepository.findByEmail(email).orElseThrow(() -> new UserNotLoggedInException("Usuario não esta authenticado"));

        InternalRequest ir = internalRequestRepository.findById(id).orElseThrow(() -> new NotFoundRequestException("solicitação não encontrada"));

        UserResponseDTO userResponseDTO = new UserResponseDTO(ir.getUser().getId(),ir.getUser().getUsername());

        return new InternalRequestDTO(ir.getId()
                , ir.getTitle()
                , ir.getDescription()
                , ir.getInternalRequestCategory()
                , ir.getCreationDate()
                , ir.getInternalRequestStatus()
                , userResponseDTO);
    }

    public List<InternalRequestListDTO> findAll(
            InternalRequestFilterDTO filter) {

        List<InternalRequest> requests =
                internalRequestRepository.findAll();

        return requests.stream()
                .filter(request ->
                        filter.title() == null ||
                                request.getTitle()
                                        .toLowerCase()
                                        .contains(filter.title().toLowerCase())
                )
                .filter(request ->
                        filter.category() == null ||
                                request.getInternalRequestCategory()
                                        .equals(filter.category())
                )
                .filter(request ->
                        filter.status() == null ||
                                request.getInternalRequestStatus()
                                        .equals(filter.status())
                )
                .filter(request ->
                        filter.startDate() == null ||
                                !request.getCreationDate()
                                        .toLocalDate()
                                        .isBefore(filter.startDate())
                )
                .filter(request ->
                        filter.endDate() == null ||
                                !request.getCreationDate()
                                        .toLocalDate()
                                        .isAfter(filter.endDate())
                )
                .map(request -> new InternalRequestListDTO(
                        request.getId(),
                        request.getTitle(),
                        request.getInternalRequestCategory(),
                        request.getUser().getUsername(),
                        request.getCreationDate(),
                        request.getInternalRequestStatus()
                ))
                .toList();
    }

    public DashboardDTO getDashboard() {

        long total = internalRequestRepository.count();

        long open =
                internalRequestRepository.countByInternalRequestStatus(
                        InternalRequestStatus.OPEN
                );

        long inProgress =
                internalRequestRepository.countByInternalRequestStatus(
                        InternalRequestStatus.IN_PROGRESS
                );

        long completed =
                internalRequestRepository.countByInternalRequestStatus(
                        InternalRequestStatus.COMPLETED
                );

        return new DashboardDTO(
                total,
                open,
                inProgress,
                completed
        );
    }

}
