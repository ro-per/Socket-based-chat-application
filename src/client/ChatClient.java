package client;

import com.sun.istack.internal.Nullable;
import javafx.collections.ObservableList;
import server.User.User;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient {

    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());

    private final User user;
    private final String server;
    private final int port;
    private ClientThread clientThread;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatClient(String username, String server, int port) {
        this.user = new User(username);
        this.server = server;
        this.port = port;
    }

    /* ----------------------------- START ----------------------------- */
    public boolean start() {
        try {
            Socket socket = new Socket(server, port);
            clientThread = new ClientThread(socket, server, user);
            new Thread(clientThread).start();
        } catch (UnknownHostException e) {
            error("Don't know about host " + server);
            System.exit(1);
        } catch (IOException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
        return true;
    }

    public void connectUser(String username) {
        Message message = new Message(MessageType.CONNECT);
        message.setSender(new User(username));
        try {
            info("Trying to connect " + username);
            clientThread.sendToServer(message);

        } catch (IOException e) {
            error("Could not connect with the server.");
        }
    }

    public void sendBroadcastMSG(String text) {
        Message message = new Message(user, MessageType.BROADCAST, text); //BROADCAST does not need receiver
        try {
            info("Broadcasting...");
            clientThread.sendToServer(message);
        } catch (IOException e) {
            error("Could not connect with the server.");
        }
    }

    public void sendPrivateMSG(String text, String receiver) {
        Message message = new Message(user, MessageType.PRIVATE, text, receiver); // PRIVATE has 1 receiver
        try {
            info("Sending private message ...");
            clientThread.sendToServer(message);
        } catch (IOException e) {
            error("Could not connect with the server.");
        }
    }

    public void leave() {
        Message message = new Message(MessageType.DISCONNECT);
        try {
            clientThread.sendToServer(message);
            info("Leaving...");
            clientThread.stop();
        } catch (IOException e) {
            error("Could not connect with the server.");
        }
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
    /* ----------------------------- GETTERS ----------------------------- */

    public ObservableList<String> getMessages() {
        return clientThread.getMessages();
    }
    public ObservableList<String> getUsers() {
        return clientThread.getUsers();
    }


    public User getUser() {
        return user;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public ClientThread getClientThread() {
        return clientThread;
    }



    /* ----------------------------- SETTERS ----------------------------- */

    public void setClientThread(ClientThread clientThread) {
        this.clientThread = clientThread;
    }
}
