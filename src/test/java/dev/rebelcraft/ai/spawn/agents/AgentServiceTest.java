package dev.rebelcraft.ai.spawn.agents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AgentServiceTest {

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentRepository agentRepository;

    @Test
    void shouldCreateAgentAndMarkUnmatchedMcpNames() {
        AgentRequest request = new AgentRequest();
        request.setName("svc-test-agent");
        request.setSystemPrompt("You are a test agent.");
        request.setMcpServerNames(List.of("GitHub", "UnknownMCP"));

        AgentResponse response = agentService.createAgent(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("svc-test-agent");
        assertThat(response.getMcpServerNames()).containsExactlyInAnyOrder("GitHub", "UnknownMCP");

        // Unmatched should contain UnknownMCP (GitHub exists in CSV)
        Set<String> unmatched = response.getUnmatchedMcpNames();
        assertThat(unmatched).contains("UnknownMCP");
        assertThat(unmatched).doesNotContain("GitHub");

        // Verify persisted
        assertThat(agentRepository.existsById(response.getId())).isTrue();
    }

    @Test
    void shouldUpdateAgentReplaceMcpNames() {
        AgentRequest createReq = new AgentRequest();
        createReq.setName("up-test-agent");
        createReq.setSystemPrompt("Create prompt");
        createReq.setMcpServerNames(List.of("GitHub"));

        AgentResponse created = agentService.createAgent(createReq);

        AgentRequest updateReq = new AgentRequest();
        updateReq.setName("updated-agent");
        updateReq.setSystemPrompt("Updated prompt");
        updateReq.setMcpServerNames(List.of("NonExistentMCP"));

        AgentResponse updated = agentService.updateAgent(created.getId(), updateReq);

        assertThat(updated.getName()).isEqualTo("updated-agent");
        assertThat(updated.getMcpServerNames()).containsExactly("NonExistentMCP");
        assertThat(updated.getUnmatchedMcpNames()).contains("NonExistentMCP");
    }

    @Test
    void shouldDeleteAgent() {
        AgentRequest request = new AgentRequest();
        request.setName("del-test-agent");
        request.setSystemPrompt("To be deleted");

        AgentResponse response = agentService.createAgent(request);
        Long id = response.getId();
        assertThat(agentRepository.existsById(id)).isTrue();

        agentService.deleteAgent(id);
        assertThat(agentRepository.existsById(id)).isFalse();
    }
}

