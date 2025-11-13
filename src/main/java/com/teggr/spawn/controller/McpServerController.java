package com.teggr.spawn.controller;

import com.teggr.spawn.dto.McpServerRequest;
import com.teggr.spawn.dto.McpServerResponse;
import com.teggr.spawn.service.McpServerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mcp-servers")
public class McpServerController {

    private final McpServerService mcpServerService;

    public McpServerController(McpServerService mcpServerService) {
        this.mcpServerService = mcpServerService;
    }

    @PostMapping
    public ResponseEntity<McpServerResponse> createMcpServer(@Valid @RequestBody McpServerRequest request) {
        McpServerResponse response = mcpServerService.createMcpServer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<McpServerResponse>> getAllMcpServers() {
        List<McpServerResponse> servers = mcpServerService.getAllMcpServers();
        return ResponseEntity.ok(servers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<McpServerResponse> getMcpServerById(@PathVariable Long id) {
        McpServerResponse response = mcpServerService.getMcpServerById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<McpServerResponse> updateMcpServer(
            @PathVariable Long id,
            @Valid @RequestBody McpServerRequest request) {
        McpServerResponse response = mcpServerService.updateMcpServer(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMcpServer(@PathVariable Long id) {
        mcpServerService.deleteMcpServer(id);
        return ResponseEntity.noContent().build();
    }
}
