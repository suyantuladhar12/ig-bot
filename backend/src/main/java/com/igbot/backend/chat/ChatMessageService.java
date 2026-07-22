package com.igbot.backend.chat;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository){
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage save(String senderId, String text, ChatMessage.Role role){
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setText(text);
        message.setRole(role);
        return chatMessageRepository.save(message);
    };
    public List<ChatMessage> getHistory(String senderId){
        return chatMessageRepository.findBySenderIdOrderByCreatedAtAsc(senderId);
    }
}
