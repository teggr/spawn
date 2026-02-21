package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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
                        div().withStyle("flex:1;display:flex;flex-direction:column;background:#fff;").with(
                                // Header
                                div().withStyle("padding:16px;border-bottom:1px solid #e0e0e0;font-weight:bold;font-size:1.1rem;").with(
                                        otherParticipant != null
                                                ? each(avatar(otherName, 32),
                                                       span(otherName).withStyle("margin-left:8px;vertical-align:middle;"))
                                                : text("Chat")
                                ),
                                // Messages area
                                div().withStyle("flex:1;overflow-y:auto;padding:16px;display:flex;flex-direction:column;gap:12px;").with(
                                        messages != null ? each(messages, m -> messageItem(m, currentUserId, otherParticipant)) : text("")
                                ),
                                // Input area
                                div().withStyle("flex:0 0 auto;padding:12px 16px;border-top:1px solid #e0e0e0;").with(
                                        form().attr("method", "post").attr("action", "/chat/" + chatId + "/messages").with(
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
        boolean active = chats != null && chats.stream().anyMatch(c ->
                c.getId().equals(activeChatId) && c.getParticipantIds().contains(participant.getId()));
        String bg = active ? "background-color:#1164a3;" : "";
        return a().withHref("/chat/dm/" + participant.getId())
                .withStyle("display:flex;align-items:center;padding:8px 16px;text-decoration:none;color:#fff;" + bg).with(
                        avatar(participant.getName(), 40),
                        span(participant.getName()).withStyle("margin-left:10px;")
                );
    }

    private DomContent messageItem(Message message, String currentUserId, Participant otherParticipant) {
        boolean isCurrentUser = message.getAuthorId().equals(currentUserId);
        String timeStr = message.getTimestampSent() != null
                ? message.getTimestampSent().format(TIME_FMT) : "";
        String align = isCurrentUser ? "align-items:flex-end;" : "align-items:flex-start;";
        String bubbleBg = isCurrentUser ? "background:#007a5a;color:#fff;" : "background:#f0f0f0;color:#222;";
        String authorName = isCurrentUser ? "You"
                : (otherParticipant != null ? otherParticipant.getName() : "?");

        return div().withStyle("display:flex;flex-direction:column;" + align).with(
                div().withStyle("display:flex;align-items:center;gap:6px;margin-bottom:2px;").with(
                        isCurrentUser ? text("") : avatar(authorName, 30),
                        small(timeStr).withStyle("color:#888;font-size:0.78rem;")
                ),
                div().withStyle("max-width:60%;padding:8px 12px;border-radius:8px;" + bubbleBg + "font-size:0.95rem;").with(
                        text(message.getContent())
                )
        );
    }
}
