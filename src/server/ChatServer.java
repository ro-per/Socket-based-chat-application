package server;

import server.exceptions.DuplicateUsernameException;
import server.messages.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private Set<User> allUsers = new HashSet<>();
    private Set<ServerThread> allServerThreads = new HashSet<>();
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
                allServerThreads.add(serverThread);
                serverThread.start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    /* ----------------------------- METHODS ----------------------------- */

    public void processMessage(Message msg) throws DuplicateUsernameException, IOException {
        switch (msg.getType()) {
            case GROUP:
                sendGroupMessage(msg);
                break;
            case PRIVATE:
                sendPrivateMessage(msg);
                break;
            case BROADCAST:
                sendBroadcast(msg);
                break;
            case CONNECT:
                connectUser(msg.getSender());
                break;
            case DISCONNECT:
                disconnectUser(msg.getSender(), msg.getServerThread());
                break;
        }
    }

    private void sendGroupMessage(Message msg) {
        //map.get(u) --> serverthread
    }

    private void sendPrivateMessage(Message msg) {

    }

    private void sendBroadcast(Message msg) throws IOException {
        for (ServerThread thread : allServerThreads) { // each client has own server thread
            thread.printOnOutputStream(msg);
        }
        System.out.println("Broadcast: " + msg);
    }


    public void connectUser(User user) throws DuplicateUsernameException {
        if (allUsers.contains(user)) {
            throw new DuplicateUsernameException("User already exists");
        } else {
            allUsers.add(user);
        }
    }

    public void disconnectUser(User user, ServerThread serverThread) {
        boolean removed = allUsers.remove(user);
        if (removed) {
            allServerThreads.remove(serverThread);
        }
    }

    private Set<User> getAllUsers() {
        return this.allUsers;
    }

    boolean hasConnectedUsers() {
        return !allServerThreads.isEmpty();
    }

}
