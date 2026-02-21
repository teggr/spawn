package dev.rebelcraft.ai.spawn.chat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatManagementService chatManagementService;
    private final MessagingService messagingService;
    private final ParticipantService participantService;

    public ChatController(ChatManagementService chatManagementService,
                          MessagingService messagingService,
                          ParticipantService participantService) {
        this.chatManagementService = chatManagementService;
        this.messagingService = messagingService;
        this.participantService = participantService;
    }

    @GetMapping
    public String listChats(Model model) {
        String currentUserId = JpaParticipantService.CURRENT_USER_ID;
        List<Participant> allParticipants = participantService.getAllParticipants();
        List<Participant> others = allParticipants.stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .collect(Collectors.toList());
        List<Chat> allChats = chatManagementService.getAllChats().stream()
                .filter(c -> c.getParticipantIds().contains(currentUserId))
                .collect(Collectors.toList());
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("participants", others);
        model.addAttribute("chats", allChats);
        return "chatListPage";
    }

    @GetMapping("/dm/{participantId}")
    public String directMessage(@PathVariable String participantId) {
        String currentUserId = JpaParticipantService.CURRENT_USER_ID;
        List<String> ids = new ArrayList<>();
        ids.add(currentUserId);
        ids.add(participantId);
        ids.sort(String::compareTo);
        Optional<Chat> existing = chatManagementService.findChatByParticipants(ids);
        Chat chat;
        if (existing.isPresent()) {
            chat = existing.get();
        } else {
            Participant currentUser = participantService.getParticipant(currentUserId);
            Participant other = participantService.getParticipant(participantId);
            chat = chatManagementService.createChat(List.of(currentUser, other));
        }
        return "redirect:/chat/" + chat.getId();
    }

    @GetMapping("/{chatId}")
    public String chatDetail(@PathVariable String chatId, Model model) {
        String currentUserId = JpaParticipantService.CURRENT_USER_ID;
        Optional<Chat> chatOpt = chatManagementService.getChat(chatId);
        if (chatOpt.isEmpty()) {
            return "redirect:/chat";
        }
        Chat chat = chatOpt.get();
        List<Message> messages = messagingService.getMessages(chatId, null);
        Participant otherParticipant = chat.getParticipantIds().stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .map(participantService::getParticipant)
                .orElse(null);

        List<Participant> allParticipants = participantService.getAllParticipants();
        List<Participant> others = allParticipants.stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .collect(Collectors.toList());
        List<Chat> allChats = chatManagementService.getAllChats().stream()
                .filter(c -> c.getParticipantIds().contains(currentUserId))
                .collect(Collectors.toList());

        model.addAttribute("chatId", chatId);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("otherParticipant", otherParticipant);
        model.addAttribute("participants", others);
        model.addAttribute("chats", allChats);
        return "chatDetailPage";
    }

    @PostMapping("/{chatId}/messages")
    public String sendMessage(@PathVariable String chatId,
                              @RequestParam String content) {
        String currentUserId = JpaParticipantService.CURRENT_USER_ID;
        Message message = new Message(null, chatId, currentUserId, content, LocalDateTime.now());
        messagingService.sendMessage(chatId, message);
        return "redirect:/chat/" + chatId;
    }
}
