package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dev.rebelcraft.ai.spawn.chat.ChatListPage.*;
import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static j2html.TagCreator.*;

@Component
public class ChatDetailPage extends PageView {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("h:mm a");

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

        String chatId = (String) model.get("chatId");
        String currentUserId = (String) model.get("currentUserId");
        Participant otherParticipant = (Participant) model.get("otherParticipant");

        @SuppressWarnings("unchecked")
        List<Participant> participants = (List<Participant>) model.get("participants");

        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) model.get("messages");

        @SuppressWarnings("unchecked")
        List<Chat> chats = (List<Chat>) model.get("chats");

        String otherName = otherParticipant != null ? otherParticipant.getName() : "Chat";

        return createPage(
                otherName + " - Chat - Spawn",
                ACTIVATE_CHAT_NAV_LINK,
                div().withStyle("display:flex;height:calc(100vh - 80px);margin:-1.5rem -12px 0;").with(
                        sidebarWithActive(participants, chatId, chats),
                        div().withId("chat-content").withStyle("flex:1;display:flex;flex-direction:column;background:#fff;").with(
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
                        )
                )
        );
    }

    private DomContent sidebarWithActive(List<Participant> participants, String activeChatId, List<Chat> chats) {
        return div().withStyle("flex:0 0 260px;background-color:#3f0e40;display:flex;flex-direction:column;overflow-y:auto;").with(
                div().withStyle("padding:16px;color:#fff;font-weight:bold;font-size:1rem;border-bottom:1px solid #521653;").with(
                        text("Direct messages")
                ),
                participants != null ? each(participants, p -> participantItemWithActive(p, activeChatId, chats)) : text("")
        );
    }

    private DomContent participantItemWithActive(Participant participant, String activeChatId, List<Chat> chats) {
        String chatId = chats != null ? chats.stream()
                .filter(c -> c.getParticipantIds().contains(participant.getId()))
                .map(Chat::getId)
                .findFirst()
                .orElse(null) : null;
        boolean active = chatId != null && chatId.equals(activeChatId);
        String bg = active ? "background-color:#1164a3;" : "";
        if (chatId != null) {
            return a()
                    .attr("hx-get", "/chat/" + chatId + "/details")
                    .attr("hx-target", "#chat-content")
                    .attr("hx-swap", "outerHTML")
                    .withHref("/chat/" + chatId)
                    .withStyle("display:flex;align-items:center;padding:8px 16px;text-decoration:none;color:#fff;" + bg).with(
                            avatar(participant.getName(), 40),
                            span(participant.getName()).withStyle("margin-left:10px;")
                    );
        } else {
            return a().withHref("/chat/dm/" + participant.getId())
                    .withStyle("display:flex;align-items:center;padding:8px 16px;text-decoration:none;color:#fff;" + bg).with(
                            avatar(participant.getName(), 40),
                            span(participant.getName()).withStyle("margin-left:10px;")
                    );
        }
    }

    static List<List<Message>> groupMessages(List<Message> messages) {
        List<List<Message>> groups = new ArrayList<>();
        if (messages == null || messages.isEmpty()) return groups;
        List<Message> currentGroup = new ArrayList<>();
        String currentAuthorId = null;
        for (Message m : messages) {
            if (!Objects.equals(m.getAuthorId(), currentAuthorId)) {
                if (!currentGroup.isEmpty()) {
                    groups.add(currentGroup);
                }
                currentGroup = new ArrayList<>();
                currentAuthorId = m.getAuthorId();
            }
            currentGroup.add(m);
        }
        if (!currentGroup.isEmpty()) {
            groups.add(currentGroup);
        }
        return groups;
    }

    static DomContent messageGroup(List<Message> group, String currentUserId, Participant otherParticipant) {
        Message first = group.get(0);
        boolean isCurrentUser = first.getAuthorId().equals(currentUserId);
        String authorName = isCurrentUser ? "You"
                : (otherParticipant != null ? otherParticipant.getName() : "?");
        String timeStr = first.getTimestampSent() != null
                ? first.getTimestampSent().format(TIME_FMT) : "";
        return div().withStyle("display:flex;gap:12px;padding:4px 16px;margin-bottom:4px;").with(
                div().withStyle("flex:0 0 36px;").with(avatar(authorName, 36)),
                div().withStyle("flex:1;min-width:0;").with(
                        div().withStyle("display:flex;align-items:baseline;gap:8px;margin-bottom:2px;").with(
                                span(authorName).withStyle("font-weight:bold;font-size:0.9rem;color:#1d1c1d;"),
                                span(timeStr).withStyle("font-size:0.75rem;color:#888;")
                        ),
                        each(group, m -> div(text(m.getContent())).withStyle("font-size:0.95rem;color:#1d1c1d;padding:1px 0;"))
                )
        );
    }
}
