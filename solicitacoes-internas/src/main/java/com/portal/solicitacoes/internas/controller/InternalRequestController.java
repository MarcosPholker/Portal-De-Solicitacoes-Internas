package com.portal.solicitacoes.internas.controller;

import com.portal.solicitacoes.internas.dto.DashboardDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestFilterDTO;
import com.portal.solicitacoes.internas.dto.InternalRequestListDTO;
import com.portal.solicitacoes.internas.enums.InternalRequestCategory;
import com.portal.solicitacoes.internas.enums.InternalRequestStatus;
import com.portal.solicitacoes.internas.service.InternalRequestService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/internalrequest")
public class InternalRequestController {

    private final InternalRequestService internalRequestService;

    public InternalRequestController(InternalRequestService internalRequestService) {
        this.internalRequestService = internalRequestService;
    }

    @PostMapping("/create")
    public ResponseEntity<InternalRequestDTO> create(@RequestBody InternalRequestDTO internalRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(internalRequestService.createRequest(internalRequestDTO));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InternalRequestDTO> updateRequest(@PathVariable UUID id, @RequestBody InternalRequestDTO internalRequestDTO){
        return ResponseEntity.ok(internalRequestService.updateRequest(id, internalRequestDTO));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        internalRequestService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<InternalRequestListDTO>> findAll(

            @RequestParam(required = false) String title,

            @RequestParam(required = false)
            InternalRequestCategory category,

            @RequestParam(required = false)
            InternalRequestStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        InternalRequestFilterDTO filter =
                new InternalRequestFilterDTO(
                        title,
                        category,
                        status,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                internalRequestService.findAll(filter)
        );

    }

    @GetMapping("/mine")
    public ResponseEntity<List<InternalRequestListDTO>> findMyRequests(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) InternalRequestCategory category,
            @RequestParam(required = false) InternalRequestStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        InternalRequestFilterDTO filter = new InternalRequestFilterDTO(
                title,
                category,
                status,
                startDate,
                endDate
        );

        return ResponseEntity.ok(internalRequestService.findMyRequests(filter));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboard() {

        return ResponseEntity.ok(
                internalRequestService.getDashboard()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternalRequestDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(internalRequestService.internalRequestDetails(id));
    }
}
