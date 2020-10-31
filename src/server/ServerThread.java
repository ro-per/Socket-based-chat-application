package server;

import server.exceptions.DuplicateUsernameException;
import server.messages.Message;
import server.messages.MessageType;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;
    private ChatServer chatServer;
    private ObjectInputStream input;
    private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private Boolean canReceive;
    private User user;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ServerThread(Socket socket, ChatServer server) {
        super("MultiServerThread");
        this.socket = socket;
        this.chatServer = server;
        this.canReceive = false;
    }

    /* ----------------------------- RUN ----------------------------- */ // TODO Called when: thread.start
    public void run() {

        try {
            is = socket.getInputStream();
            input = new ObjectInputStream(is);
            os = socket.getOutputStream();
            output = new ObjectOutputStream(os);

            onAuthentication();
            onListening();
            onLeaving();

        } catch (IOException | DuplicateUsernameException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* ----------------------------- METHODS ----------------------------- */
    void onAuthentication() throws IOException, ClassNotFoundException {
        Message firstMessage = (Message) input.readObject();
        Message welcomeMessage = new Message(MessageType.PRIVATE, "Welcome !");

        try {
            chatServer.processMessage(firstMessage);
        } catch (DuplicateUsernameException de) {
            de.printStackTrace();
        }
        this.user = firstMessage.getSender();
        welcomeMessage.setSender(user);
        output.writeObject(welcomeMessage);
        canReceive = true;

    }

    void onListening() throws IOException, DuplicateUsernameException, ClassNotFoundException {
        while (socket.isConnected()) {
            Message inMessage = (Message) input.readObject();
            if (inMessage != null) {
                System.out.println(inMessage.getType() + " - " + inMessage.getSender() + ": " + inMessage.getText());
                chatServer.processMessage(inMessage);
            }
        }
    }

    void onLeaving() throws IOException, DuplicateUsernameException {
        try {
            Message msg = new Message(MessageType.DISCONNECT);
            msg.setSender(user);
            chatServer.processMessage(msg);
        } catch (DuplicateUsernameException de) {
            de.printStackTrace();
        }
        canReceive = false; // TODO DEBUG fault can occur here
        Message leaveMessage = new Message(user, MessageType.BROADCAST, user.getName() + " has left");
        socket.close();
        chatServer.processMessage(leaveMessage);
    }

    void printOnOutputStream(Message msg) throws IOException {
        if (canReceive) output.writeObject(msg);

    }
}

