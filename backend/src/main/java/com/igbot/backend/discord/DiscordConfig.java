package com.igbot.backend.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Value("${discord.bot-token}")
    private String botToken;

    @Bean
    public JDA jda(DiscordListener discordListener) throws Exception {
        JDA jda = JDABuilder.createDefault(botToken)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(discordListener)
                .build();
        jda.awaitReady();
        System.out.println("Discord bot connected.");
        return jda;
    }
}