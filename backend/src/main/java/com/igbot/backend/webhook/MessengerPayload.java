package com.igbot.backend.webhook;

import java.util.List;

public class MessengerPayload {
    private String object;
    private List<Entry> entry;

    public String getObject() {return object;}
    public void setObject(String object) {this.object = object;}
    public List<Entry> getEntry() {return entry;}
    public void setEntry(List<Entry> entry) {this.entry = entry;}

    public static class Entry{
        private String id;
        private long time;
        private List<Messaging> messaging;

        public String getId() {return id;}
        public void setId(String id) {this.id = id;}

        public long getTime() {return time;}
        public void setTime(long time){ this.time = time;}

        public List<Messaging> getMessaging() {return messaging;}
        public void setMessaging(List<Messaging> messaging) {this.messaging = messaging;}
    }

    public static class Messaging{
        private Sender sender;
        private Message message;

        public Sender getSender() {return sender;}
        public void setSender(Sender sender) {this.sender = sender;}

        public Message getMessage() {return message;}
        public void setMessage(Message message) {this.message = message;}
    }

    public static class Sender{
        private String id;
        public String getId() {return id;}
        public void setId(String id) {this.id = id;}
    }

    public static class Message{
        private String mid;
        private String text;

        public String getMid() { return mid; }
        public void setMid(String mid) { this.mid = mid; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
