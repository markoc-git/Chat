package com.example.socialnetwork.Modal;

public class Chat {
    String message;
    String receiver;
    String sender;

    public Chat(String message, String receiver, String sender) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

}
