package com.portal.solicitacoes.internas.service;

import com.portal.solicitacoes.internas.dto.DashboardDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestFilterDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestListDTO;
import com.portal.solicitacoes.internas.entity.InternalRequest;
import com.portal.solicitacoes.internas.entity.User;
import com.portal.solicitacoes.internas.enums.InternalRequestCategory;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import com.portal.solicitacoes.internas.exception.NotFoundRequestException;
import com.portal.solicitacoes.internas.exception.UserNotLoggedInException;
import com.portal.solicitacoes.internas.repositories.InternalRequestRepository;
import com.portal.solicitacoes.internas.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalRequestServiceTest {

    @Mock
    private InternalRequestRepository internalRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private InternalRequestService internalRequestService;

    @Test
    void deveCriarSolicitacao() {

        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("Marcos");
        user.setEmail("marcos@email.com");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(user));

        InternalRequestDTO dto = new InternalRequestDTO(
                null,
                "Problema no computador",
                "Computador não liga",
                InternalRequestCategory.TI,
                null,
                null,
                null
        );

        InternalRequest savedRequest = new InternalRequest();

        savedRequest.setId(UUID.randomUUID());
        savedRequest.setTitle("Problema no computador");
        savedRequest.setDescription("Computador não liga");
        savedRequest.setInternalRequestCategory(
                InternalRequestCategory.TI
        );
        savedRequest.setInternalRequestStatus(
                InternalRequestStatus.OPEN
        );
        savedRequest.setUser(user);

        when(internalRequestRepository.save(any(InternalRequest.class)))
                .thenAnswer(invocation -> {

                    InternalRequest request = invocation.getArgument(0);

                    request.setId(savedRequest.getId());

                    return request;
                });

        InternalRequestDTO result =
                internalRequestService.createRequest(dto);

        assertNotNull(result);

        assertEquals(
                "Problema no computador",
                result.title()
        );

        assertEquals(
                "Computador não liga",
                result.description()
        );

        assertEquals(
                InternalRequestCategory.TI,
                result.internalRequestCategory()
        );

        assertEquals(
                InternalRequestStatus.OPEN,
                result.internalRequestStatus()
        );

        assertNotNull(result.id());

        verify(userRepository)
                .findByEmail("marcos@email.com");

        verify(internalRequestRepository)
                .save(any(InternalRequest.class));
    }

    @Test
    void deveAtualizarStatusAoEditarSolicitacao() {
        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("Marcos");
        user.setEmail("marcos@email.com");

        InternalRequest request = new InternalRequest();
        request.setId(requestId);
        request.setTitle("Solicitação antiga");
        request.setDescription("Descrição antiga");
        request.setInternalRequestCategory(InternalRequestCategory.TI);
        request.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request.setUser(user);

        when(authentication.getName()).thenReturn("marcos@email.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("marcos@email.com")).thenReturn(Optional.of(user));
        when(internalRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(internalRequestRepository.save(request)).thenReturn(request);

        InternalRequestDTO dto = new InternalRequestDTO(
                requestId,
                "Solicitação atualizada",
                "Descrição atualizada",
                InternalRequestCategory.RH,
                null,
                InternalRequestStatus.IN_PROGRESS,
                null
        );

        InternalRequestDTO result = internalRequestService.updateRequest(requestId, dto);

        assertEquals(InternalRequestStatus.IN_PROGRESS, request.getInternalRequestStatus());
        assertEquals(InternalRequestStatus.IN_PROGRESS, result.internalRequestStatus());
        verify(internalRequestRepository).save(request);
    }

    @Test
    void deveManterStatusAoEditarQuandoStatusNaoInformado() {
        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("Marcos");
        user.setEmail("marcos@email.com");

        InternalRequest request = new InternalRequest();
        request.setId(requestId);
        request.setInternalRequestStatus(InternalRequestStatus.IN_PROGRESS);
        request.setUser(user);

        when(authentication.getName()).thenReturn("marcos@email.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("marcos@email.com")).thenReturn(Optional.of(user));
        when(internalRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(internalRequestRepository.save(request)).thenReturn(request);

        InternalRequestDTO dto = new InternalRequestDTO(
                requestId,
                "Solicitação atualizada",
                "Descrição atualizada",
                InternalRequestCategory.TI,
                null,
                null,
                null
        );

        InternalRequestDTO result = internalRequestService.updateRequest(requestId, dto);

        assertEquals(InternalRequestStatus.IN_PROGRESS, request.getInternalRequestStatus());
        assertEquals(InternalRequestStatus.IN_PROGRESS, result.internalRequestStatus());
    }

    @Test
    void naoDevePermitirAlterarSolicitacaoDeOutroUsuario() {

        UUID usuarioLogadoId = UUID.randomUUID();
        UUID donoDaSolicitacaoId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        User usuarioLogado = new User();
        usuarioLogado.setId(usuarioLogadoId);
        usuarioLogado.setEmail("marcos@email.com");

        User donoDaSolicitacao = new User();
        donoDaSolicitacao.setId(donoDaSolicitacaoId);
        donoDaSolicitacao.setEmail("outro@email.com");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(usuarioLogado));

        InternalRequest request = new InternalRequest();

        request.setId(requestId);
        request.setTitle("Solicitação antiga");
        request.setDescription("Descrição antiga");
        request.setUser(donoDaSolicitacao);

        when(internalRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        InternalRequestDTO dto = new InternalRequestDTO(
                requestId,
                "Tentativa de alteração",
                "Tentativa de alteração",
                InternalRequestCategory.TI,
                null,
                null,
                null
        );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> internalRequestService.updateRequest(requestId, dto)
        );

        assertEquals(
                "Você não pode alterar uma solicitação de outro usuário",
                exception.getMessage()
        );

        assertThrows(
                AccessDeniedException.class,
                () -> internalRequestService.updateRequest(
                        request.getId(),
                        new InternalRequestDTO(
                                null,
                                "Novo título",
                                "Nova descrição",
                                InternalRequestCategory.TI,
                                null,
                                null,
                                null
                        )
                )
        );

        verify(internalRequestRepository, never()).save(any(InternalRequest.class));

        verify(internalRequestRepository, never())
                .save(any(InternalRequest.class));
    }

    @Test
    void deveLancarExcecaoQuandoSolicitacaoNaoExistir() {

        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("marcos@email.com");
        user.setUsername("Marcos");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(user));

        when(internalRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundRequestException exception = assertThrows(
                NotFoundRequestException.class,
                () -> internalRequestService.updateRequest(
                        requestId,
                        null
                )
        );

        assertEquals(
                "Solicitação não encontrada",
                exception.getMessage()
        );

        verify(internalRequestRepository, never())
                .save(any(InternalRequest.class));
    }

    @Test
    void naoDevePermitirExcluirSolicitacaoDeOutroUsuario() {

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setUsername("Dono");

        User loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());
        loggedUser.setUsername("Outro usuário");

        InternalRequest request = new InternalRequest();
        request.setId(UUID.randomUUID());
        request.setUser(owner);

        when(userRepository.findByEmail("outro@email.com"))
                .thenReturn(Optional.of(loggedUser));

        when(internalRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "outro@email.com",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThrows(
                AccessDeniedException.class,
                () -> internalRequestService.delete(request.getId())
        );

        verify(internalRequestRepository, never())
                .deleteById(request.getId());
    }

    @Test
    void deveExcluirSolicitacaoDoProprioUsuario() {

        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("marcos@email.com");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(user));

        InternalRequest request = new InternalRequest();
        request.setId(requestId);
        request.setTitle("Minha solicitação");
        request.setUser(user);

        when(internalRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        internalRequestService.delete(requestId);

        verify(internalRequestRepository)
                .deleteById(requestId);
    }

    @Test
    void deveAlterarStatusDaPropriaSolicitacao() {

        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setEmail("marcos@email.com");
        user.setUsername("Marcos");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(user));

        InternalRequest request = new InternalRequest();
        request.setId(requestId);
        request.setTitle("Problema no computador");
        request.setUser(user);
        request.setInternalRequestStatus(InternalRequestStatus.OPEN);

        when(internalRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        when(internalRequestRepository.save(any(InternalRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InternalRequest result =
                internalRequestService.changeStatus(
                        requestId,
                        InternalRequestStatus.COMPLETED
                );

        assertNotNull(result);

        assertEquals(
                InternalRequestStatus.COMPLETED,
                result.getInternalRequestStatus()
        );

        verify(internalRequestRepository)
                .save(request);
    }

    @Test
    void naoDeveAlterarStatusDeSolicitacaoDeOutroUsuario() {

        User owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setUsername("Dono");

        User loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());
        loggedUser.setUsername("Outro usuário");

        InternalRequest request = new InternalRequest();
        request.setId(UUID.randomUUID());
        request.setUser(owner);
        request.setInternalRequestStatus(InternalRequestStatus.OPEN);

        when(userRepository.findByEmail("outro@email.com"))
                .thenReturn(Optional.of(loggedUser));

        when(internalRequestRepository.findById(request.getId()))
                .thenReturn(Optional.of(request));

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "outro@email.com",
                        null
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThrows(
                AccessDeniedException.class,
                () -> internalRequestService.changeStatus(
                        request.getId(),
                        InternalRequestStatus.COMPLETED
                )
        );

        verify(internalRequestRepository, never())
                .save(any(InternalRequest.class));
    }

    @Test
    void deveBuscarDetalhesDaSolicitacao() {

        UUID userId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        user.setUsername("Marcos");
        user.setEmail("marcos@email.com");

        when(authentication.getName())
                .thenReturn("marcos@email.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(user));

        InternalRequest request = new InternalRequest();

        request.setId(requestId);
        request.setTitle("Problema no computador");
        request.setDescription("Computador não liga");
        request.setInternalRequestCategory(
                InternalRequestCategory.TI
        );
        request.setInternalRequestStatus(
                InternalRequestStatus.OPEN
        );
        request.setUser(user);

        when(internalRequestRepository.findById(requestId))
                .thenReturn(Optional.of(request));

        InternalRequestDTO result =
                internalRequestService.internalRequestDetails(requestId);

        assertNotNull(result);

        assertEquals(
                requestId,
                result.id()
        );

        assertEquals(
                "Problema no computador",
                result.title()
        );

        assertEquals(
                "Computador não liga",
                result.description()
        );

        assertEquals(
                InternalRequestCategory.TI,
                result.internalRequestCategory()
        );

        assertEquals(
                InternalRequestStatus.OPEN,
                result.internalRequestStatus()
        );

        assertNotNull(result.userResponseDTO());

        assertEquals(
                userId,
                result.userResponseDTO().id()
        );

        assertEquals(
                "Marcos",
                result.userResponseDTO().username()
        );

        verify(internalRequestRepository)
                .findById(requestId);
    }

    @Test
    void deveRetornarDadosDoDashboard() {

        when(internalRequestRepository.count())
                .thenReturn(10L);

        when(internalRequestRepository.countByInternalRequestStatus(
                InternalRequestStatus.OPEN))
                .thenReturn(4L);

        when(internalRequestRepository.countByInternalRequestStatus(
                InternalRequestStatus.IN_PROGRESS))
                .thenReturn(3L);

        when(internalRequestRepository.countByInternalRequestStatus(
                InternalRequestStatus.COMPLETED))
                .thenReturn(3L);

        DashboardDTO result =
                internalRequestService.getDashboard();

        assertNotNull(result);

        assertEquals(10L, result.total());
        assertEquals(4L, result.open());
        assertEquals(3L, result.inProgress());
        assertEquals(3L, result.completed());

        verify(internalRequestRepository)
                .count();

        verify(internalRequestRepository)
                .countByInternalRequestStatus(InternalRequestStatus.OPEN);

        verify(internalRequestRepository)
                .countByInternalRequestStatus(InternalRequestStatus.IN_PROGRESS);

        verify(internalRequestRepository)
                .countByInternalRequestStatus(InternalRequestStatus.COMPLETED);
    }

    @Test
    void deveListarTodasAsSolicitacoesSemFiltros() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Marcos");

        InternalRequest request1 = new InternalRequest();
        request1.setId(UUID.randomUUID());
        request1.setTitle("Problema no computador");
        request1.setInternalRequestCategory(InternalRequestCategory.TI);
        request1.setCreationDate(LocalDateTime.now());
        request1.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request1.setUser(user);

        InternalRequest request2 = new InternalRequest();
        request2.setId(UUID.randomUUID());
        request2.setTitle("Solicitação de acesso");
        request2.setInternalRequestCategory(InternalRequestCategory.TI);
        request2.setCreationDate(LocalDateTime.now());
        request2.setInternalRequestStatus(InternalRequestStatus.COMPLETED);
        request2.setUser(user);

        when(internalRequestRepository.findAll())
                .thenReturn(List.of(request1, request2));

        InternalRequestFilterDTO filter =
                new InternalRequestFilterDTO(
                        null,
                        null,
                        null,
                        null,
                        null
                );

        List<InternalRequestListDTO> result =
                internalRequestService.findAll(filter);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Problema no computador", result.get(0).title());
        assertEquals("Solicitação de acesso", result.get(1).title());

        assertEquals("Marcos", result.get(0).username());
        assertEquals("Marcos", result.get(1).username());

        verify(internalRequestRepository).findAll();
    }

    @Test
    void deveListarSomenteSolicitacoesDoUsuarioAutenticado() {
        User loggedUser = new User();
        loggedUser.setId(UUID.randomUUID());
        loggedUser.setEmail("marcos@email.com");
        loggedUser.setUsername("Marcos");

        InternalRequest ownRequest = new InternalRequest();
        ownRequest.setId(UUID.randomUUID());
        ownRequest.setTitle("Meu pedido");
        ownRequest.setInternalRequestCategory(InternalRequestCategory.TI);
        ownRequest.setCreationDate(LocalDateTime.now());
        ownRequest.setInternalRequestStatus(InternalRequestStatus.OPEN);
        ownRequest.setUser(loggedUser);

        when(authentication.getName()).thenReturn("marcos@email.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(userRepository.findByEmail("marcos@email.com"))
                .thenReturn(Optional.of(loggedUser));
        when(internalRequestRepository.findAllByUser(loggedUser))
                .thenReturn(List.of(ownRequest));

        InternalRequestFilterDTO filter = new InternalRequestFilterDTO(
                null, null, null, null, null
        );

        List<InternalRequestListDTO> result =
                internalRequestService.findMyRequests(filter);

        assertEquals(1, result.size());
        assertEquals("Meu pedido", result.get(0).title());
        assertEquals("Marcos", result.get(0).username());
        verify(internalRequestRepository).findAllByUser(loggedUser);
        verify(internalRequestRepository, never()).findAll();
    }

    @Test
    void deveFiltrarSolicitacoesPorTitulo() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Marcos");

        InternalRequest request1 = new InternalRequest();
        request1.setId(UUID.randomUUID());
        request1.setTitle("Problema no computador");
        request1.setInternalRequestCategory(InternalRequestCategory.FINANCIAL);
        request1.setCreationDate(LocalDateTime.now());
        request1.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request1.setUser(user);

        InternalRequest request2 = new InternalRequest();
        request2.setId(UUID.randomUUID());
        request2.setTitle("Solicitação de acesso");
        request2.setInternalRequestCategory(InternalRequestCategory.FINANCIAL);
        request2.setCreationDate(LocalDateTime.now());
        request2.setInternalRequestStatus(InternalRequestStatus.COMPLETED);
        request2.setUser(user);

        when(internalRequestRepository.findAll())
                .thenReturn(List.of(request1, request2));

        InternalRequestFilterDTO filter =
                new InternalRequestFilterDTO(
                        "computador",
                        null,
                        null,
                        null,
                        null
                );

        List<InternalRequestListDTO> result =
                internalRequestService.findAll(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "Problema no computador",
                result.get(0).title()
        );

        verify(internalRequestRepository).findAll();
    }

    @Test
    void deveFiltrarSolicitacoesPorCategoriaEStatus() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Marcos");

        InternalRequest request1 = new InternalRequest();
        request1.setId(UUID.randomUUID());
        request1.setTitle("Problema no sistema");
        request1.setInternalRequestCategory(InternalRequestCategory.INFRASTRUCTURE);
        request1.setCreationDate(LocalDateTime.now());
        request1.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request1.setUser(user);

        InternalRequest request2 = new InternalRequest();
        request2.setId(UUID.randomUUID());
        request2.setTitle("Solicitação de acesso");
        request2.setInternalRequestCategory(InternalRequestCategory.INFRASTRUCTURE);
        request2.setCreationDate(LocalDateTime.now());
        request2.setInternalRequestStatus(InternalRequestStatus.COMPLETED);
        request2.setUser(user);

        when(internalRequestRepository.findAll())
                .thenReturn(List.of(request1, request2));

        InternalRequestFilterDTO filter =
                new InternalRequestFilterDTO(
                        null,
                        InternalRequestCategory.INFRASTRUCTURE,
                        InternalRequestStatus.OPEN,
                        null,
                        null
                );

        List<InternalRequestListDTO> result =
                internalRequestService.findAll(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals("Problema no sistema", result.get(0).title());
        assertEquals(InternalRequestStatus.OPEN,
                result.get(0).internalRequestStatus());

        verify(internalRequestRepository).findAll();
    }

    @Test
    void deveFiltrarSolicitacoesPorPeriodo() {

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("Marcos");

        InternalRequest request1 = new InternalRequest();
        request1.setId(UUID.randomUUID());
        request1.setTitle("Solicitação dentro do período");
        request1.setInternalRequestCategory(InternalRequestCategory.RH);
        request1.setCreationDate(LocalDateTime.of(2026, 9, 15, 10, 0));
        request1.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request1.setUser(user);

        InternalRequest request2 = new InternalRequest();
        request2.setId(UUID.randomUUID());
        request2.setTitle("Solicitação fora do período");
        request2.setInternalRequestCategory(InternalRequestCategory.RH);
        request2.setCreationDate(LocalDateTime.of(2026, 8, 15, 10, 0));
        request2.setInternalRequestStatus(InternalRequestStatus.OPEN);
        request2.setUser(user);

        when(internalRequestRepository.findAll())
                .thenReturn(List.of(request1, request2));

        InternalRequestFilterDTO filter =
                new InternalRequestFilterDTO(
                        null,
                        null,
                        null,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 30)
                );

        List<InternalRequestListDTO> result =
                internalRequestService.findAll(filter);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(
                "Solicitação dentro do período",
                result.get(0).title()
        );

        verify(internalRequestRepository).findAll();
    }
}