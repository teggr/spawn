package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.web.view.PageView;
import j2html.tags.DomContent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static dev.rebelcraft.ai.spawn.web.view.DefaultPageLayout.*;
import static j2html.TagCreator.*;

@Component
public class ChatListPage extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {

        @SuppressWarnings("unchecked")
        List<Participant> participants = (List<Participant>) model.get("participants");

        return createPage(
                "Chat - Spawn",
                ACTIVATE_CHAT_NAV_LINK,
                div().withStyle("display:flex;height:calc(100vh - 80px);margin:-1.5rem -12px 0;").with(
                        sidebar(participants, null),
                        div().withStyle("flex:1;display:flex;align-items:center;justify-content:center;background:#fff;").with(
                                p("Select a participant to start chatting").withStyle("color:#888;font-size:1.1rem;")
                        )
                )
        );
    }

    static DomContent sidebar(List<Participant> participants, String activeChatId) {
        return div().withStyle("flex:0 0 260px;background-color:#3f0e40;display:flex;flex-direction:column;overflow-y:auto;").with(
                div().withStyle("padding:16px;color:#fff;font-weight:bold;font-size:1rem;border-bottom:1px solid #521653;").with(
                        text("Direct messages")
                ),
                participants != null ? each(participants, p -> participantItem(p, activeChatId)) : text("")
        );
    }

    static DomContent participantItem(Participant participant, String activeChatId) {
        return a().withHref("/chat/dm/" + participant.getId())
                .withStyle("display:flex;align-items:center;padding:8px 16px;text-decoration:none;color:#fff;").with(
                        avatar(participant.getName(), 40),
                        span(participant.getName()).withStyle("margin-left:10px;")
                );
    }

    static DomContent avatar(String name, int size) {
        String trimmed = name != null ? name.trim() : "";
        String initial = !trimmed.isEmpty() ? trimmed.substring(0, 1).toUpperCase() : "?";
        String color = avatarColor(name);
        return div().withStyle("width:" + size + "px;height:" + size + "px;background-color:" + color +
                ";display:flex;align-items:center;justify-content:center;" +
                "color:#fff;font-weight:bold;font-size:" + (size / 2) + "px;flex-shrink:0;border-radius:4px;").with(
                text(initial)
        );
    }

    static String avatarColor(String name) {
        String[] colors = {"#6b46c1", "#dc2626", "#16a34a", "#ca8a04", "#0891b2", "#db2777"};
        if (name == null || name.isEmpty()) return colors[0];
        int idx = Math.abs(name.hashCode()) % colors.length;
        return colors[idx];
    }
}
