package com.igbot.backend.reply;

import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReplyWorker {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final JDA jda;

    public ReplyWorker(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper, JDA jda ){
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.jda = jda;
    }

    @PostConstruct
    public void startListening(){
        Thread worker = new Thread(this::listenLoop);
        worker.setDaemon(true);
        worker.start();
    }

    private void listenLoop(){
        System.out.println("Reply worker started, waiting for outgoing messages...");
        while(true){
            try{
                var result = redisTemplate.opsForList().rightPop("outgoing_messages", Duration.ofSeconds(5));
                if(result == null) continue;

                JsonNode job = objectMapper.readTree(result);
                String senderId = job.get("senderId").asText();
                String reply = job.get("reply").asText();
                String platform = job.get("platform").asText();

                if("discord".equals(platform)){
                    sendDiscordReply(senderId, reply);
                } else{
                    System.out.println("Unknown platform, skipping..." + platform);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void sendDiscordReply(String userId, String text) {
        jda.retrieveUserById(userId).queue(user -> {
            user.openPrivateChannel().queue(channel -> {
                for (String chunk : splitMessage(text, 2000)) {
                    channel.sendMessage(chunk).queue();
                }
            });
        });
    }

    private List<String> splitMessage(String text, int limit) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + limit, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }
        return chunks;
    }
}
