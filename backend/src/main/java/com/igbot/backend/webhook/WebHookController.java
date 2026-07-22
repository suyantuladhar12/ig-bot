package com.igbot.backend.webhook;

import com.igbot.backend.chat.ChatMessage;
import com.igbot.backend.chat.ChatMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

@RestController
public class WebHookController {
    @Value("${meta.verify-token}")
    private String verifyToken;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChatMessageService chatMessageService;


    public WebHookController(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, ChatMessageService chatMessageService){
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.chatMessageService = chatMessageService;
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge){
        if("subscribe".equals(mode) && verifyToken.equals(token)){
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Forbidden");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveMessage(@RequestBody MessengerPayload payload) throws Exception{
        if(!"page".equals(payload.getObject())){
            return ResponseEntity.ok("ignored");
        }

        for(MessengerPayload.Entry entry : payload.getEntry()){
            for (MessengerPayload.Messaging event: entry.getMessaging()){
                if(event.getMessage() != null){
                    String senderId = event.getSender().getId();
                    String text = event.getMessage().getText();

                    chatMessageService.save(senderId, text, ChatMessage.Role.USER);

                    String job = objectMapper.writeValueAsString(new HashMap<String, String>(){
                        {
                            put("senderId", senderId);
                            put("text", text);
                            put("platform", "messenger");
                        }
                    });
                    redisTemplate.opsForList().leftPush("incoming_messages",job);
                }
            }
        }
        return ResponseEntity.ok("EVENT_RECEIVED");
    }
}
