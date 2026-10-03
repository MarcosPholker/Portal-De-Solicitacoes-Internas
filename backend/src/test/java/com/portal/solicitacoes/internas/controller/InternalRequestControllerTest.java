package com.portal.solicitacoes.internas.controller;

import com.portal.solicitacoes.internas.dto.InternalRequestDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestListDTO;
import com.portal.solicitacoes.internas.dto.UserResponseDTO;
import com.portal.solicitacoes.internas.enums.InternalRequestCategory;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import com.portal.solicitacoes.internas.service.InternalRequestService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.portal.solicitacoes.internas.security.TokenService;


@WebMvcTest(controllers = InternalRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalRequestControllerTest {

    @MockitoBean
    private TokenService tokenService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InternalRequestService internalRequestService;


    @Test
    void deveCriarSolicitacao() throws Exception {

        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        InternalRequestDTO response = new InternalRequestDTO(
                requestId,
                "Problema no computador",
                "Computador não liga",
                InternalRequestCategory.RH,
                LocalDateTime.now(),
                InternalRequestStatus.OPEN,
                new UserResponseDTO(
                        userId,
                        "Marcos"
                )
        );

        when(internalRequestService.createRequest(
                any(InternalRequestDTO.class)
        )).thenReturn(response);


        mockMvc.perform(
                        post("/internalrequest/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "title": "Problema no computador",
                                    "description": "Computador não liga",
                                    "internalRequestCategory": "TI"
                                }
                                """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Problema no computador"))
                .andExpect(jsonPath("$.description")
                        .value("Computador não liga"));


        verify(internalRequestService)
                .createRequest(any(InternalRequestDTO.class));
    }

    @Test
    void deveListarMeusPedidos() throws Exception {
        InternalRequestListDTO request = new InternalRequestListDTO(
                UUID.randomUUID(),
                "Meu pedido",
                InternalRequestCategory.TI,
                "Marcos",
                LocalDateTime.now(),
                InternalRequestStatus.OPEN
        );
        when(internalRequestService.findMyRequests(any()))
                .thenReturn(List.of(request));

        mockMvc.perform(get("/internalrequest/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Meu pedido"))
                .andExpect(jsonPath("$[0].username").value("Marcos"));

        verify(internalRequestService).findMyRequests(any());
    }
}