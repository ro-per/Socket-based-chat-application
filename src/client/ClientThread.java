package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private static boolean exit;
    private Socket socket;
    private BufferedReader in;
    private static PrintWriter out;
    private String server;
//    private boolean exit = false;
    private ObservableList<String> messages;
    private ObservableList<String> users;
    private String user;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public ClientThread(Socket socket, String server, String user) {
        this.socket = socket;
        this.server = server;
        this.user = user;
        this.messages = FXCollections.observableArrayList();
    }

    /* ----------------------------- RUN ----------------------------- */ // TODO Called when: thread.start
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            out.println(user);

            while (!exit) {
                String inputLine = in.readLine();
                System.out.println(inputLine);
                Platform.runLater(() -> messages.add(inputLine));
            }

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + server);
            System.exit(1);
        }
    }

    public ObservableList<String> getMessages() {
        return messages;
    }

    public static void send(String message) {
        out.println(message);
        out.flush();
    }

    public static void stop(){
        exit = true;
        send("LEAVE");
    }


}
