package client;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.user.User;
import server.messages.Message;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());

    private volatile boolean isRunning = true;
    private final Socket socket;
    private final String server;
    private ObservableList<String> messagesPublic;
    private ObservableList<String> messagesPrivate;
    private ObservableList<String> users;
    private final User user;
    private static ObjectOutputStream oos;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ClientThread(Socket socket, String server, User user) {
        this.socket = socket;
        this.server = server;
        this.user = user;
        this.messagesPublic = FXCollections.observableArrayList();
        this.messagesPrivate = FXCollections.observableArrayList();
        this.users = FXCollections.observableArrayList();
    }

    /* ----------------------------- RUN ----------------------------- */
    public void run() {
        try {
            OutputStream outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            InputStream is = socket.getInputStream();
            ObjectInputStream input = new ObjectInputStream(is);

            while (isRunning) {
                Message message = (Message) input.readObject();
                //IF message = joined controller.setUserList(message)

                String serverUserName = "Server";
                boolean senderIsServer = message.getSender().toString().equals(serverUserName);
                boolean senderIsCorrespondent = message.getSender().toString().equals(ChatApplication.correspondent);

                switch (message.getType()) {


                    case USER_CONNECTED:
                        // SERVER SENDING connect MESSAGE = NOTIFY NEW USER ARRIVED
                        if (senderIsServer) {
                            Platform.runLater(() -> users.addAll(message.getContent()));
                        }
                        // connect MESSAGE NOT FROM SERVER = ERROR
                        else {
                            //TODO send error message
                        }
                        break;
                    case USER_DISCONNECTED:
                        // SERVER SENDING disconnect MESSAGE = NOTIFY USER LEFT
                        if (senderIsServer) {
                            Platform.runLater(() -> users.removeAll(message.getContent()));
                        }
                        // disconnect MESSAGE NOT FROM SERVER = ERROR
                        else {
                            //TODO send error message
                        }
                        break;
                    case PRIVATE:
                        // SERVER SENDING private MESSAGE = NOTIFY NEW USER ARRIVED
                        if (senderIsServer) {
                            Platform.runLater(() -> users.addAll(message.getActiveUsers()));
                        } else {
                            Platform.runLater(() -> {
                                // PRIVATE MESSAGE FROM OTHER PERSON THAN CORRESPONDENT
                                if (!senderIsCorrespondent) {
                                    ChatApplication.closePrivateChat();
                                    ChatApplication.launchPrivateChat(message.getSender().toString());
                                }
                                messagesPrivate.add(message.getContent()); // ALSO EXECUTED IF SENDER==CORRESPONDENT
                            });
                        }
                        break;
                    case BROADCAST:
                        // BROADCAST = public message
                        Platform.runLater(() -> messagesPublic.add(message.getContent()));
                        break;

                    case ERROR_LOGIN:
                        // ERROR = loginError
                        Platform.runLater(() -> {
                            ChatApplication.showLogin(message.getContent());
                        });
                        break;
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
    }

    public ObservableList<String> getMessagesPublic() {
        return messagesPublic;
    }

    public ObservableList<String> getMessagesPrivate() {
        return messagesPublic;
    }

    public ObservableList<String> getUsers() {
        return users;
    }


    public void sendToServer(Message msg) throws IOException {
        msg.setSender(user);
        oos.writeObject(msg);
        oos.flush();
    }


    public void stop() {
        isRunning = false;
    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

}
