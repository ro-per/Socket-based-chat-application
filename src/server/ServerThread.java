package server;

import com.sun.istack.internal.Nullable;
import server.messages.Message;
import server.messages.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {

    private static final Logger logger = Logger.getLogger(ServerThread.class.getName());

    private volatile boolean isRunning;
    private final Socket socket;
    private final MessageHandler handler;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private User user;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ServerThread(Socket socket,MessageHandler handler) {
        super("MultiServerThread");
        this.socket = socket;
        this.handler = handler;
    }

    /* ----------------------------- RUN ----------------------------- */
    public void run() {

        try {
            InputStream is = socket.getInputStream();
            input = new ObjectInputStream(is);
            OutputStream os = socket.getOutputStream();
            output = new ObjectOutputStream(os);
            isRunning = true;

            onAuthentication();
            onListening();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /* ----------------------------- METHODS ----------------------------- */
    void onAuthentication() throws IOException, ClassNotFoundException {
        Message firstMessage = (Message) input.readObject();
        handler.processMessage(firstMessage, this);
    }

    void onListening() throws IOException, ClassNotFoundException {
        while (isRunning) {
            Message inMessage = (Message) input.readObject();
            if (inMessage != null) {
                info(inMessage.getType() + " - " + inMessage.getSender() + ": " + inMessage.getText());
                handler.processMessage(inMessage, this);
            }
        }
    }

    void stopThread() throws IOException {
        isRunning = false;
        socket.close();
    }

    void printOnOutputStream(Message msg) throws IOException {
        output.writeObject(msg);
    }


    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}

