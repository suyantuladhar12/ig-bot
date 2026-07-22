package com.igbot.backend.chat;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="chat_messages")
public class ChatMessage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String senderId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Instant createdAt = Instant.now();

    public enum Role{
        USER, ASSISTANT
    }

    public long getId() {return id;};
    public void setId(long id) {this.id = id;};

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) {this.senderId = senderId;}

    public String getText() {return text;}
    public void setText(String text){this.text = text;}

    public Role getRole(){ return role;};
    public void setRole(Role role) {this.role = role;}

    public Instant getCreatedAt() {return createdAt;}
    public void setCreatedAt(Instant createdAt) {this.createdAt=createdAt;}
}