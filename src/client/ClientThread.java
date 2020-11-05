package client;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.User.User;
import server.messages.Message;
import server.messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientThread implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientThread.class.getName());

    private volatile boolean isRunning = true;
    private final Socket socket;
    private final String server;
    private ObservableList<String> messages;
    private ObservableList<String> users;
    private final User user;
    private static ObjectOutputStream oos;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ClientThread(Socket socket, String server, User user) {
        this.socket = socket;
        this.server = server;
        this.user = user;
        this.messages = FXCollections.observableArrayList();
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

                Platform.runLater(() -> messages.add(message.getText()));

                if (message.getType() == MessageType.BROADCAST) {
                    Platform.runLater(() -> users.addAll(message.getActiveUsers()));
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
    }

    public ObservableList<String> getMessages() {
        return messages;
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
