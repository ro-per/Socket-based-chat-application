package server.messages;

import server.ServerThread;
import server.User;
import server.exceptions.PrivacyException;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

public class Message implements Serializable {

    private User sender;
    private final MessageType messageType;
    private final String text;
    private Set<User> group;
    private final Timestamp timestamp;
    private ServerThread serverThread;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public Message(User sender, MessageType messageType, String text) {
        this.sender = sender;
        this.messageType = messageType;
        this.text = text;
        this.group = new HashSet<>();
        this.group.add(sender);
        this.timestamp = new Timestamp(new Date().getTime());
    }

    /* CONNECT / DISCONNECT MESSAGE */
    public Message(MessageType messageType) {
        this.messageType = messageType;
        this.text = "I want to disconnect !!!";
        this.timestamp = new Timestamp(new Date().getTime());
    }

    public Message(User sender, MessageType messageType, String text, Set<User> group) {
        this.sender = sender;
        this.messageType = messageType;
        this.text = text;
        this.group = group;
        this.group.add(sender);
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

    public Set<User> getGroup() {
        return group;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public ServerThread getServerThread() {
        return serverThread;
    }

    /* ----------------------------- SETTERS ----------------------------- */
    public void addMember(User member) throws PrivacyException {
        //Only add users to group chat for GROUP messages
        if (this.messageType != MessageType.GROUP) {
            throw new PrivacyException("Users can only be added to a group message");
        } else {
            this.group.add(member);
        }
    }
    public void setSender(User user) {
        this.sender=user;
    }


    /* ----------------------------- OVERRIDE ----------------------------- */
    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", messageType=" + messageType +
                ", group=" + group +
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
