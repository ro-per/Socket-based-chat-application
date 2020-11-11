package client;

import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.user.User;
import server.messages.Message;
import server.messages.MessageType;

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


                switch (message.getType()) {
                    case CONNECT:
                        Platform.runLater(() -> users.addAll(message.getContent()));
                        break;
                    case DISCONNECT:
                        Platform.runLater(() -> users.removeAll(message.getContent()));
                        break;
                    case PRIVATE:
                        if (message.getSender().toString() == "Server") {
                            Platform.runLater(() -> users.addAll(message.getActiveUsers()));

                        } else {
                            ChatApplication.launchPrivateChat(message.getSender().toString());

                            Platform.runLater(() -> messagesPrivate.add(message.getContent()));
                        }
                        break;
                    case BROADCAST:
                        Platform.runLater(() -> messagesPublic.add(message.getContent()));
                        break;

                    case ERROR:
                        Platform.runLater(() -> {
                            try {
                                ChatApplication.showLoginOnPublicStage(message.getContent());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            error("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
    }

    public void addSelf(String username) {
        System.out.println("test");
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
