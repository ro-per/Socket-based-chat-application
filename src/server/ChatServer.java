package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer implements ChatServerInterface {
    private Set<String> users = new HashSet<>();
    private Set<ServerThread> serverThreads = new HashSet<>();
    private final int port;

    /* ----------------------------- MAIN ----------------------------- */
    public static void main(String[] args) {
        int portNumber = 1000;
        ChatServer server = new ChatServer(portNumber);
        server.start();
    }

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatServer(int port) {
        this.port = port;
    }

    /* ----------------------------- START ----------------------------- */
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port " + port);

            while (true) {
                ServerThread serverThread = new ServerThread(serverSocket.accept(), this);
                serverThreads.add(serverThread);
                serverThread.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    /* ----------------------------- METHODS ----------------------------- */
    @Override
    public void broadcast(String msg) {
        for (ServerThread thread : serverThreads) { // each client has own server thread
            thread.send(msg);
        }
        System.out.println(this+ ": "+msg);
    }

    @Override
    public void addUser(String user) {
        users.add(user);
    }

    @Override
    public void removeUser(String user, ServerThread serverThread) {
        boolean removed = users.remove(user);
        if (removed) {
            serverThreads.remove(serverThread);
        }
    }

    Set<String> getUsers() {
        return this.users;
    }

    @Override
    public boolean isDuplicateUserName(String user) {
        return !users.contains(user);
    }

    boolean hasConnectedUsers() {
        return !serverThreads.isEmpty();
    }


}
