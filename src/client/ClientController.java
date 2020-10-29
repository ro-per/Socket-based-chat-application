package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ClientController {
    private ChatClient client;
    /* ----------------------------- @FXML ----------------------------- */
    @FXML
    private TextField userField;
    @FXML
    private TextField serverField;
    @FXML
    private TextField portField;
    @FXML
    private TextField msgField;
    @FXML
    private ListView chatPane;
    @FXML
    private ListView userPane;
    /* ----------------------------- ERROR MESSAGES ----------------------------- */
    static final String ERROR_EMPTY_MESSAGE = "Cannot send empty message !";
    static final String ERROR_EMPTY_USER = "Required !";
    static final String ERROR_EMPTY_SERVER = "Required ! !";
    static final String ERROR_EMPTY_PORT = "Required ! ! !";
    static final String ERROR_FORMAT_PORT = "Only numbers allowed !";

    /* ----------------------------- METHODS ----------------------------- */
    public void connectButtonAction() {
        String userNameString = this.userField.getText();
        String serverString = serverField.getText();
        String portString = portField.getText();
        boolean correct = true;

        if (userNameString.isEmpty()) {
            this.userField.setText(ERROR_EMPTY_USER);
            flashTextField(this.userField);
            correct = false;
        }

        if (serverString.isEmpty()) {
            this.serverField.setText(ERROR_EMPTY_SERVER);
            flashTextField(this.serverField);
            correct = false;
        }

        if (portString.isEmpty()) {
            this.portField.setText(ERROR_EMPTY_PORT);
            flashTextField(this.portField);
            correct = false;
        } else if (!isInteger(portString)) {
            this.portField.setText(ERROR_FORMAT_PORT);
            flashTextField(this.portField);
            correct = false;
        }

        if (correct) {
            int port = Integer.parseInt(portString);
            connectToServer(userNameString, serverString, port);
        }
    }

    private void connectToServer(String userName, String serverName, int portNumber) {
        client = new ChatClient(userName, serverName, portNumber);
        if (client.start()) {                                               //TODO USES CONNECT MESSAGE
            chatPane.setItems(client.getMessages());
        }
    }

    public void sendButtonAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            // type = radiobutton.getType //TODO
            Message message = new Message(MessageType.BROADCAST, text);     //TODO message type choosing
            client.getClientThread().sendToServer(message);                 //TODO USES PRIVATE/GROUP/BROADCAST MESSAGE
            msgField.clear();
        } else {
            msgField.setText(ERROR_EMPTY_MESSAGE);
            flashTextField(this.msgField);
        }
    }

    public void exit() throws IOException, InterruptedException {
//        ClientThread.stopThread(); //TODO USES DISCONNECT MESSAGE

        Platform.exit();
        System.exit(0);
    }

    /* ----------------------------- METHODS ----------------------------- */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendButtonAction();
    }

    /* ----------------------------- FIELD PRESSED ----------------------------- */
    public void userNameClicked() {
        userField.clear();
        userField.setEffect(null);
    }

    public void serverClicked() {
        serverField.clear();
        serverField.setEffect(null);
    }

    public void portClicked() {
        portField.clear();
        portField.setEffect(null);
    }

    public void messageFieldClicked() {
        msgField.setEffect(null);
    }

    /* ----------------------------- VISUAL EFFECTS ----------------------------- */
    public void flashTextField(TextField t) {
        Lighting errorLighting = new Lighting();
        t.setEffect(errorLighting);
    }


/*
    public void setUserList(Message msg) {
        System.out.println("setUserList() method Enter");
        Platform.runLater(() -> {
            ObservableList<User> users = FXCollections.observableList(msg.getUsers());
            userList.setItems(users);
            userList.setCellFactory(new CellRenderer());
            setOnlineLabel(String.valueOf(msg.getUserlist().size()));
        });
        System.out.println("setUserList() method Exit");
    }*/

}
