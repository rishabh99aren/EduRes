package com.group16.example.edures;

public class Comment {

    private String sender;
    private String text;

    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Comment(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }
}
