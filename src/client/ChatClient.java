package client;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements ChatClientInterface {
    private String user;
    private String server;
    private int port;
    private ClientThread clientThread;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatClient(String user, String server, int port) {
        this.user = user;
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
            System.err.println("Don't know about host " + server);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    server);
            System.exit(1);
        }
        return true;
    }

    /* ----------------------------- METHODS ----------------------------- */
    @Override
    public void send(String message) throws IOException {
        ClientThread.send(message);
    }

    /* ----------------------------- GETTERS ----------------------------- */
    @Override
    public ObservableList<String> getMessages() {
        return clientThread.getMessages();
    }

    public String getUser() {
        return user;
    }

    public String getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    /* ----------------------------- SETTERS ----------------------------- */
    public void setUser(String user) {
        this.user = user;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
