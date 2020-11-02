package server;

import com.sun.istack.internal.Nullable;
import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());

    private ChatServerController serverController;
    private Map<User, ServerThread> allServerThreads = new HashMap<>();
    private final int port;

    /* ----------------------------- MAIN ----------------------------- */
    public static void main(String[] args) {
        int portNumber = 1000;
        ChatServer server = new ChatServer(portNumber);
        server.start();
    }

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatServer(int port) {
        serverController = new ChatServerController();
        this.port = port;
    }

    /* ----------------------------- START ----------------------------- */
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            info("Server listening on port " + port);

            while (true) {
                ServerThread serverThread = new ServerThread(serverSocket.accept(), this);
                serverThread.start();
            }
        } catch (IOException e) {
            error("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    /* ----------------------------- METHODS ----------------------------- */

    public void processMessage(Message msg, ServerThread thread) throws IOException {
        switch (msg.getType()) {
            case GROUP:
                sendGroupMessage(msg);
                break;
            case PRIVATE:
                sendPrivateMessage(msg);
                break;
            case BROADCAST:
                sendBroadcast(msg);
                break;
            case CONNECT:
                connectUser(msg.getSender(), thread);
                break;
            case DISCONNECT:
                disconnectUser(msg.getSender(), thread);
                break;
        }
    }

    private void sendGroupMessage(Message msg) {
        //map.get(u) --> serverthread
    }

    // send message to yourself
    private void sendPrivateMessage(Message msg) throws IOException {
        ServerThread thread = allServerThreads.get(msg.getSender());
        thread.printOnOutputStream(msg);
    }

    private void sendBroadcast(Message msg) throws IOException {
        for (ServerThread thread : allServerThreads.values()) { // each client has own server thread
            thread.printOnOutputStream(msg);
        }
        info(msg.getSender() + " is broadcasting: " + msg);
    }


    public void connectUser(User user, ServerThread thread) throws IOException {
        info(user.getName() + " is connecting to the server.");
        try {
            serverController.connectUser(user.getName());
            allServerThreads.put(user, thread);
            thread.printOnOutputStream(new Message(MessageType.PRIVATE, "Welcome to the chat!"));
            info(user.getName() + " is connected to server.");
        } catch (DuplicateUsernameException e) {
            thread.printOnOutputStream(new Message(MessageType.PRIVATE, "Username is already been used."));
            info(user.getName() + " failed to connect to server.");
        }
    }

    public void disconnectUser(User user, ServerThread thread) {
        try {
            info(user.getName() + " is leaving the chat.");
            serverController.disconnectUser(user.getName());
            allServerThreads.remove(thread);
            Message leaveMessage = new Message(MessageType.BROADCAST, user.getName() + " has left the chat");
            leaveMessage.setSender(user);
            sendBroadcast(leaveMessage);
            info(user.getName() + " left.");
            thread.stopThread();
        } catch (UserNotFoundException | IOException e) {
            error(e.getMessage());
        }

    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

}
