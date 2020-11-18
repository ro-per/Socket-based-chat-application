package main;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatServer {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());


    private final MessageHandler handler;
    private final int port;

    /* ----------------------------- MAIN ----------------------------- */
    public static void main(String[] args) {
        int portNumber = 1000;
        try {
            portNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException ne) {
            System.out.println("Port could not be parsed, cause: " + ne.getCause());
            System.out.println("Standard port 1000 is used.");
        }
        ChatServer server = new ChatServer(portNumber);
        server.start();
    }

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ChatServer(int port) {
        handler = new MessageHandler();
        this.port = port;
    }

    /* ----------------------------- START ----------------------------- */
    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            info("Server is listening on port " + port);

            while (true) {
                ServerThread serverThread = new ServerThread(serverSocket.accept(), handler);
                serverThread.start();
            }
        } catch (IOException e) {
            error("Could not listen on port " + port);
            System.exit(-1);
        }
    }

    /* ----------------------------- METHODS ----------------------------- */

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }

}
