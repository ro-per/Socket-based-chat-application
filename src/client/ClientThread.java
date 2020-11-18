package client;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.messages.Message;
import server.user.User;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread implements Runnable {

    private final Logger logger = Logger.getLogger(ClientThread.class.getName());

    private volatile boolean isRunning = true;
    private final Socket socket;
    private final String server;
    private ObservableList<String> messagesPublic;
    private ObservableList<String> messagesPrivate;
    private ObservableList<String> users;
    private final User user;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    /*  -------------------------------- CONSTRUCTOR -------------------------------- */
    public ClientThread(Socket socket, String server, User user) {
        this.socket = socket;
        this.server = server;
        this.user = user;
        this.messagesPublic = FXCollections.observableArrayList();
        this.messagesPrivate = FXCollections.observableArrayList();
        this.users = FXCollections.observableArrayList();
    }

    /*  -------------------------------- run -------------------------------- */

    public void run() {
        try {
            OutputStream socketOutputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(socketOutputStream);
            InputStream socketInputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(socketInputStream);

            while (isRunning) {
                Message message = (Message) objectInputStream.readObject();
                //IF message = joined controller.setUserList(message)

                String serverUserName = "Server";
                String content = message.getContent();
                String sender = message.getSender().toString();
                boolean senderIsServer = message.getSender().toString().equals(serverUserName);
                boolean senderIsCorrespondent = message.getSender().toString().equals(ChatApplication.correspondent);

                Platform.runLater(() -> {
                    switch (message.getType()) {
                        case USER_CONNECTED:
                            users.addAll(content);
                            break;
                        case USER_DISCONNECTED:
                            users.removeAll(content);
                            break;
                        case REQUEST_PRIVATE:
                            boolean b = ChatApplication.askOpenNewChat(sender);
                            if (b) {
                                ChatApplication.resetPrivateChat();
                                ChatApplication.launchPrivateChat(message.getSender().toString());
                            } else {
                                ChatApplication.chatClient.sendPrivateMsg("I do not want to talk", sender);
                            }
                            break;
                        case PRIVATE:
                            messagesPrivate.add(content);
                            break;
                        case BROADCAST:
                            messagesPublic.add(content);
                            break;
                        case ERROR_LOGIN:
                            ChatApplication.showLogin(content);
                            break;
                        case USER_LIST:
                            users.addAll(message.getActiveUsers());
                    }
                });
            }
        } catch (IOException |
                ClassNotFoundException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }


    }

    /*  -------------------------------- METHODS -------------------------------- */
    public void putOnStream(Message msg) throws IOException {
        msg.setSender(user);
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
    }

    public void stop() {
        isRunning = false;
    }

    public void clearPrivateMessages() {
        messagesPrivate.clear();
    }

    public void addPrivateMessage(Message message) {
        messagesPrivate.add(message.getContent());
    }

    /*  -------------------------------- GETTERS -------------------------------- */
    public ObservableList<String> getMessagesPublic() {
        return messagesPublic;
    }

    public ObservableList<String> getMessagesPrivate() {
        return messagesPrivate;
    }

    public ObservableList<String> getUsers() {
        return users;
    }
    /*  -------------------------------- SETTERS -------------------------------- */


    /*  -------------------------------- LOGGER -------------------------------- */
    private void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}









   /* public void run() {
        try {
            OutputStream socketOutputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(socketOutputStream);
            InputStream socketInputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(socketInputStream);

            while (isRunning) {
                Message message = (Message) objectInputStream.readObject();
                //IF message = joined controller.setUserList(message)

                String serverUserName = "Server";
                String content = message.getContent();
                String sender = message.getSender().toString();
                boolean senderIsServer = message.getSender().toString().equals(serverUserName);
                boolean senderIsCorrespondent = message.getSender().toString().equals(ChatApplication.correspondent);

                switch (message.getType()) {
                    case USER_CONNECTED:
                        Platform.runLater(() -> users.addAll(content));
                        break;
                    case USER_DISCONNECTED:
                        Platform.runLater(() -> users.removeAll(content));
                        break;
                    case REQUEST_PRIVATE:
                        Platform.runLater(() -> {
                            boolean b = ChatApplication.askOpenNewChat(sender);
                            if (b) {
                                ChatApplication.closePrivateChat();
                                ChatApplication.launchPrivateChat(message.getSender().toString());
                            } else {
                                ChatApplication.chatClient.sendPrivateMsg("I do not want to talk", sender);
                            }
                        });
                        break;
                    case PRIVATE:
                        Platform.runLater(() -> messagesPrivate.add(content));
                        break;

                    case BROADCAST:
                        Platform.runLater(() -> messagesPublic.add(content));
                        break;

                    case ERROR_LOGIN:
                        Platform.runLater(() -> ChatApplication.showLogin(content));
                        break;
                }
            }
        } catch (IOException |
                ClassNotFoundException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }

    }*/
