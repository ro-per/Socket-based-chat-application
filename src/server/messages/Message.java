package server.messages;

import server.User.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Message implements Serializable {

    private User sender;
    private final MessageType messageType;
    private final String text;
    private String receiver;
    private final Timestamp timestamp;
    private ArrayList<String> activeUsers;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public Message(User sender, MessageType messageType, String text) {
        this.sender = sender;
        this.messageType = messageType;
        this.text = text;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    /* CONNECT / DISCONNECT MESSAGE */
    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.text = "I want to disconnect !!!";
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public Message(User sender, MessageType messageType, String text, String receiver) {
        this.sender = sender;
        this.messageType = messageType;
        this.text = text;
        this.receiver = receiver;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public Message(MessageType messageType, String text) {
        this.messageType = messageType;
        this.text = text;
        this.timestamp = new Timestamp(new Date().getTime());
    }

    /* ----------------------------- GETTERS ----------------------------- */
    public User getSender() {
        return sender;
    }

    public MessageType getType() {
        return messageType;
    }

    public String getReceiverString() {
        return receiver;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getText() {
        switch (messageType) {
            case BROADCAST:
                return "[BROADCAST:" + sender.getName() + "]: " + text;
            case PRIVATE:
                return "[PRIVATE:" + sender.getName() + "]: " + text;
            default:
                return text;
        }
    }

    public ArrayList<String> getActiveUsers() {
        return activeUsers;
    }

    /* ----------------------------- SETTERS ----------------------------- */
    public void setSender(User user) {
        this.sender = user;
    }

    public void setActiveUsers(ArrayList<String> activeUsers) {
        this.activeUsers = activeUsers;
    }

    /* ----------------------------- OVERRIDE ----------------------------- */
    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", messageType=" + messageType +
                ", group=" + receiver +
                ", timestamp=" + timestamp +
                '}';
    }

    /* ----------------------------- EQUALS/HASH ----------------------------- */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return sender.equals(message.sender) &&
                messageType == message.messageType &&
                timestamp.equals(message.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, messageType, timestamp);
    }

}
