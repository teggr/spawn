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
import static j2html.TagCreator.*;

@Component
public class ChatMessagesFragment extends PageView {

    @Override
    protected DomContent renderPage(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
        String currentUserId = (String) model.get("currentUserId");
        Participant otherParticipant = (Participant) model.get("otherParticipant");

        @SuppressWarnings("unchecked")
        List<Message> messages = (List<Message>) model.get("messages");

        return div().withId("messages-container").withStyle("flex:1;overflow-y:auto;padding:16px 0;display:flex;flex-direction:column;").with(
                messages != null ? each(groupMessages(messages), g -> messageGroup(g, currentUserId, otherParticipant)) : text("")
        );
    }
}
