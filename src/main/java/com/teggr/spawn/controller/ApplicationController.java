package com.teggr.spawn.controller;

import com.teggr.spawn.dto.ApplicationRequest;
import com.teggr.spawn.dto.ApplicationResponse;
import com.teggr.spawn.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(@Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(@PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.updateApplication(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{applicationId}/mcp-servers/{mcpServerId}")
    public ResponseEntity<ApplicationResponse> addMcpServer(
            @PathVariable Long applicationId,
            @PathVariable Long mcpServerId) {
        ApplicationResponse response = applicationService.addMcpServerToApplication(applicationId, mcpServerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{applicationId}/mcp-servers/{mcpServerId}")
    public ResponseEntity<ApplicationResponse> removeMcpServer(
            @PathVariable Long applicationId,
            @PathVariable Long mcpServerId) {
        ApplicationResponse response = applicationService.removeMcpServerFromApplication(applicationId, mcpServerId);
        return ResponseEntity.ok(response);
    }
}
