package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.User;
import server.messages.Message;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private volatile boolean isRunning = true;
    private Socket socket;
    private String server;
    private ObservableList<String> messages;
    private ObservableList<String> users;
    private User user;
    private static ObjectOutputStream oos;
    private InputStream is;
    private ObjectInputStream input;
    private OutputStream outputStream;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ClientThread(Socket socket, String server, User user) {
        this.socket = socket;
        this.server = server;
        this.user = user;
        this.messages = FXCollections.observableArrayList();
    }

    /* ----------------------------- RUN ----------------------------- */ // TODO Called when: thread.start
    public void run() {
        try {
            outputStream = socket.getOutputStream();
            oos = new ObjectOutputStream(outputStream);
            is = socket.getInputStream();
            input = new ObjectInputStream(is);

            while (isRunning) {
                Message message = (Message) input.readObject();
                Platform.runLater(() -> messages.add(message.getText()));
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public void sendToServer(Message msg) throws IOException {
        msg.setSender(user);
        oos.writeObject(msg);
        oos.flush();
    }


    public void stop() {
        isRunning = false;
    }

}
