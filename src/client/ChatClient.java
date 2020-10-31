package client;

import javafx.collections.ObservableList;
import server.User;
import server.messages.Message;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private final User user;
    private final String server;
    private final int port;
    private ClientThread clientThread;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatClient(String user_name, String server, int port) {
        this.user = new User(user_name);
        this.server = server;
        this.port = port;
    }

    /* ----------------------------- START ----------------------------- */
    public boolean start() {
        try {
            Socket socket = new Socket(server, port);
            clientThread = new ClientThread(socket, server, user);
            new Thread(clientThread).start();                               //TODO USES CONNECT MESSAGE
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + server);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    server);
            System.exit(1);
        }
        return true;
    }

    /* ----------------------------- GETTERS ----------------------------- */

    public ObservableList<String> getMessages() {
        return clientThread.getMessages();
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
