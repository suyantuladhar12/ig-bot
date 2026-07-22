package com.igbot.backend.discord;

import com.igbot.backend.chat.ChatMessage;
import com.igbot.backend.chat.ChatMessageService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

@Component
public class DiscordListener extends ListenerAdapter {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;

    public DiscordListener(
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper,
            ChatMessageService chatMessageService){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()){
            return;
        }
        if (event.isFromGuild()) {
            return;
        }

        String senderId = event.getAuthor().getId();
        String senderName = event.getAuthor().getName();
        String text = event.getMessage().getContentDisplay();

        System.out.println("Discord message from " + senderId + senderName + ": " + text);

        chatMessageService.save(senderId, text, ChatMessage.Role.USER);

        try{
            String job = objectMapper.writeValueAsString(new HashMap<String, String>(){
                {
                    put("senderId", senderId);
                    put("text", text);
                    put("platform","discord");
                }
            });
            redisTemplate.opsForList().leftPush("incoming_messages",job);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
