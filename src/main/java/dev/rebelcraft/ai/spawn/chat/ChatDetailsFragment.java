package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.rebelcraft.ai.spawn.chat.ChatDetailPage.groupMessages;
import static dev.rebelcraft.ai.spawn.chat.ChatDetailPage.messageGroup;
import static dev.rebelcraft.ai.spawn.chat.ChatListPage.avatar;
import static j2html.TagCreator.*;

@Component
public class ChatDetailsFragment extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        String chatId = (String) model.get("chatId");
        String currentUserId = (String) model.get("currentUserId");
        Participant otherParticipant = (Participant) model.get("otherParticipant");

        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) model.get("messages");

        String otherName = otherParticipant != null ? otherParticipant.getName() : "Chat";

        return div().withId("chat-content").withStyle("flex:1;display:flex;flex-direction:column;background:#fff;").with(
                // Header
                div().withStyle("padding:16px;border-bottom:1px solid #e0e0e0;font-weight:bold;font-size:1.1rem;").with(
                        otherParticipant != null
                                ? each(avatar(otherName, 32),
                                       span(otherName).withStyle("margin-left:8px;vertical-align:middle;"))
                                : text("Chat")
                ),
                // Messages area
                div().withId("messages-container").withStyle("flex:1;overflow-y:auto;padding:16px 0;display:flex;flex-direction:column;").with(
                        messages != null ? each(groupMessages(messages), g -> messageGroup(g, currentUserId, otherParticipant)) : text("")
                ),
                // Input area
                div().withStyle("flex:0 0 auto;padding:12px 16px;border-top:1px solid #e0e0e0;").with(
                        form().attr("method", "post").attr("action", "/chat/" + chatId + "/messages")
                                .attr("hx-post", "/chat/" + chatId + "/messages")
                                .attr("hx-target", "#messages-container")
                                .attr("hx-swap", "outerHTML")
                                .attr("hx-on::after-request", "this.reset()").with(
                                div().withStyle("display:flex;gap:8px;").with(
                                        textarea()
                                                .attr("name", "content")
                                                .attr("placeholder", "Type a message...")
                                                .attr("required", "required")
                                                .attr("rows", "2")
                                                .withStyle("flex:1;resize:none;border:1px solid #ccc;border-radius:4px;padding:8px;font-size:0.95rem;"),
                                        button("Send")
                                                .attr("type", "submit")
                                                .withStyle("background:#007a5a;color:#fff;border:none;border-radius:4px;padding:8px 16px;cursor:pointer;font-size:0.95rem;")
                                )
                        )
                )
        );
    }
}
