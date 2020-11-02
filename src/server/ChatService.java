package server;

import com.sun.istack.internal.Nullable;
import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());
    private final UserManager manager;

    public ChatService() {
        manager = new UserManager();
    }

    public void sendGroupMessage(Message msg) {
        //TODO ROMEO make it possibel to send messages between a group of users instead of to all users
        // use the manager class to get the threads and users
        //Set<ServerThread> threads = manager.getServerThreadByUsers()
    }

    // send message to yourself
    public void sendMessageToSender(Message msg) throws IOException {
        User user = msg.getSender();
        ServerThread thread = manager.getServerThread(user);
        thread.printOnOutputStream(msg);
    }

    public void sendBroadcast(Message msg) throws IOException {
        Collection<ServerThread> threads = manager.getAllServerThreads();
        if (!threads.isEmpty()) {
            for (ServerThread thread : manager.getAllServerThreads()) { // each client has own server thread
                thread.printOnOutputStream(msg);
            }
            info(msg.getSender() + " is broadcasting: " + msg);
        }
    }


    public void connectUser(User user, ServerThread thread) throws IOException {
        info(user.getName() + " is connecting to the server.");
        try {
            manager.connectUser(user, thread);

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
            manager.disconnectUser(user);

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
