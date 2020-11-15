package server;

import com.sun.istack.internal.Nullable;
import server.user.User;
import server.user.UserManager;
import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());
    private final UserManager manager;

    private User serverUser = new User("Server");

    public ChatService() {
        manager = new UserManager();
    }


    public void connectUser(User user, ServerThread thread) throws IOException {
        info(user.getName() + " is connecting to the server.");
        try {
            //REGISTER USER IN MANAGER
            manager.connectUser(user, thread);

            //NOITFY OTHER USERS
            sendUserConnectedMessage(user);

        } catch (DuplicateUsernameException e) {
            Message msg = new Message(serverUser, MessageType.ERROR_LOGIN, "Username is already been used.");

            info(user.getName() + " failed to connect to server.");
        }
    }

    public void disconnectUser(User user, ServerThread thread) {
        info(user.getName() + " is leaving the chat.");
        try {
            manager.disconnectUser(user);

            //NOTIFY OTHER USERS
            sendUserDisconnectedMessage(user);


            thread.stopThread();

        } catch (UserNotFoundException | IOException e) {
            error(e.getMessage());
        }

    }
    /* -------------------- SENDING MESSAGES -------------------- */

    public void sendPrivateMessage(Message msg) throws IOException {
        ServerThread thread = manager.getServerThreadByUsername(msg.getReceiverString());
        thread.printOnOutputStream(msg);

    }

    public void sendPublicMessage(Message msg) throws IOException {
        Collection<ServerThread> threads = manager.getServerThreads();

        if (!threads.isEmpty()) {
            for (ServerThread thread : manager.getServerThreads()) { // each client has own server thread
                thread.printOnOutputStream(msg);
            }
            info(msg.getSender() + " is broadcasting: " + msg);
        }
    }

    public void sendUserConnectedMessage(User user) throws IOException {
        //ADD USER TO USER LISTS
        Message msg1 = new Message(MessageType.USER_CONNECTED, user.getName());
        msg1.setSender(serverUser);
        sendPublicMessage(msg1);
    }

    public void sendUserDisconnectedMessage(User user) throws IOException {
        Message msg1 = new Message(MessageType.USER_DISCONNECTED, user.getName());
        msg1.setSender(serverUser);
        sendPublicMessage(msg1);
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

}
