package com.group16.example.edures;

public class Query {
    private String Receiver;
    private String Sender;
    private String ID;
    private String Type;
    private String Detail;

    public String getReceiver() {
        return Receiver;
    }

    public String getSender() {
        return Sender;
    }

    public String getID() {

        return ID;
    }

    public String getType() {
        return Type;
    }

    public String getDetail() {
        return Detail;
    }

    public Query()
    {

    }

    public Query(String receiver, String sender, String ID, String type, String detail) {
        Receiver = receiver;
        Sender = sender;
        this.ID = ID;
        Type = type;
        Detail = detail;
    }
}
