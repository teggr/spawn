package dev.rebelcraft.ai.spawn.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ChatManagementService chatManagementService;

    @Autowired
    private MessagingService messagingService;

    private Chat createChatWithParticipants() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Participant bob = participantService.createParticipant("Bob", null, null);
        return chatManagementService.createChat(List.of(alice, bob));
    }

    @Test
    void shouldReturnChatDetailsFragment() throws Exception {
        Chat chat = createChatWithParticipants();

        mockMvc.perform(get("/chat/" + chat.getId() + "/details"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"chat-content\"")))
                .andExpect(content().string(containsString("id=\"messages-container\"")))
                .andExpect(content().string(not(containsString("<html"))));
    }

    @Test
    void shouldReturnChatMessagesFragment() throws Exception {
        Chat chat = createChatWithParticipants();

        mockMvc.perform(get("/chat/" + chat.getId() + "/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"messages-container\"")))
                .andExpect(content().string(not(containsString("<html"))));
    }

    @Test
    void shouldRedirectWhenDetailsRequestedForUnknownChat() throws Exception {
        mockMvc.perform(get("/chat/nonexistent-id/details"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat"));
    }

    @Test
    void shouldRedirectWhenMessagesRequestedForUnknownChat() throws Exception {
        mockMvc.perform(get("/chat/nonexistent-id/messages"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat"));
    }

    @Test
    void shouldReturnMessagesFragmentWhenSendMessageViaHtmx() throws Exception {
        Chat chat = createChatWithParticipants();

        mockMvc.perform(post("/chat/" + chat.getId() + "/messages")
                        .header("HX-Request", "true")
                        .param("content", "Hello HTMX!"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("id=\"messages-container\"")))
                .andExpect(content().string(containsString("Hello HTMX!")))
                .andExpect(content().string(not(containsString("<html"))));
    }

    @Test
    void shouldRedirectWhenSendMessageWithoutHtmx() throws Exception {
        Chat chat = createChatWithParticipants();

        mockMvc.perform(post("/chat/" + chat.getId() + "/messages")
                        .param("content", "Hello!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chat/" + chat.getId()));
    }

    @Test
    void shouldIncludeHtmxScriptInChatDetailPage() throws Exception {
        Chat chat = createChatWithParticipants();

        mockMvc.perform(get("/chat/" + chat.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("htmx.org@1.9.10")))
                .andExpect(content().string(containsString("id=\"chat-content\"")))
                .andExpect(content().string(containsString("id=\"messages-container\"")));
    }

    @Test
    void shouldIncludeHxBoostOnNavbarLinks() throws Exception {
        mockMvc.perform(get("/chat"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("hx-boost=\"true\"")));
    }
}
