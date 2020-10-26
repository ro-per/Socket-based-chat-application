package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket = null;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private Boolean canReceive;
    private String user;
    private boolean leaving;
    /* ----------------------------- MESSAGES ----------------------------- */
    static final String ERROR_DUPLICATE_USERNAME = "Username is already taken !";

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ServerThread(Socket socket, ChatServer server) {
        super("MultiServerThread");
        this.socket = socket;
        this.server = server;
        this.canReceive = false;
    }

    /* ----------------------------- RUN ----------------------------- */ // TODO Called when: thread.start
    public void run() {

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            onAuthentication();
            onStartUp();
            onSendMessage();
            onLeaving();

        } catch (IOException e) {
            e.printStackTrace();
            server.removeUser(user, this);
        }
    }

    /* ----------------------------- METHODS ----------------------------- */
    void onAuthentication() throws IOException {
        user = in.readLine();

        while (!server.isDuplicateUserName(user)) {
            out.println(ERROR_DUPLICATE_USERNAME);
            user = in.readLine();
        }

        server.addUser(user);
        server.broadcast(enterMessage(user));

        //TODO Romeo update user list
        updateConnectedUsers();
    }

    void onStartUp() {
        canReceive = true;
        out.println(welcomeMessage(user));
    }

    void onSendMessage() throws IOException {
        String msg;
        do {
            msg = in.readLine();
            if (msg.contains("LEAVE")) {
                leaving = true;
            } else {
                server.broadcast(formatMessage(user, msg));
            }
        } while (!leaving);

    }

    void onLeaving() throws IOException {
        //TODO Romeo update users

        server.removeUser(user, this);
        socket.close();
        server.broadcast(leaveMessage(user));
    }

    void send(String msg) {
        if (canReceive) out.println(msg);

    }

    void updateConnectedUsers() {
        if (server.hasConnectedUsers()) {
            out.println("Connected users:" + server.getUsers());
        } else {
            out.println("No other users connected");
        }
    }

    /* ----------------------------- MESSAGE 'BUILDERS' ----------------------------- */
    private String formatMessage(String user, String msg) {
        return "[" + user + "]: " + msg;
    }

    private String welcomeMessage(String user) {
        return "Welcome " + user;
    }

    private String enterMessage(String user) {
        return user + " has entered the chat";
    }

    private String leaveMessage(String user) {
        return user + " has left the chat";
    }
}

