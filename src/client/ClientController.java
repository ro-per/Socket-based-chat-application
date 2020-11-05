package client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import server.User.User;
import server.messages.Message;

import java.io.IOException;

public class ClientController {
    private ChatClient client = null;
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
    private Button connectButton;
    @FXML
    private ListView chatPane;
    @FXML
    private ListView userPane;

    @FXML
    private ListView userList;

    /* ----------------------------- ERROR MESSAGES ----------------------------- */
    static final String ERROR_EMPTY_USER = "Required !";
    static final String ERROR_EMPTY_SERVER = "Required ! !";
    static final String ERROR_EMPTY_PORT = "Required ! ! !";
    static final String ERROR_FORMAT_PORT = "Only numbers allowed !";

    /* ----------------------------- METHODS ----------------------------- */
    public void connectButtonAction() throws IOException {
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
//            disableConnectButton(true);
        }

    }

    private void connectToServer(String userName, String serverName, int portNumber) throws IOException {
        client = new ChatClient(userName, serverName, portNumber);
        if (client.start()) {
            chatPane.setItems(client.getMessages());
            userPane.setItems(client.getUsers());
            client.connectUser(userName);
        }
    }

    public void sendBroadcastAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            client.sendBroadcastMSG(text);
            msgField.clear();
        } else {
            flashTextField(this.msgField);
        }
    }

    public void sendPrivateAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            client.sendPrivateMSG(text, "testuser"); //TODO HARDCODED
            msgField.clear();
        } else {
            flashTextField(this.msgField);
        }
    }

    public void exit() throws IOException {
        //Only  perform leave is chatclient is started
        if (client != null) {
            client.leave();
        }


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

    public void setUserList(Message msg) {
        Platform.runLater(() -> {
            ObservableList<String> users = FXCollections.observableList(msg.getActiveUsers()); //Arraylist
            userList.setItems(users);
        });
    }

    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendBroadcastAction();
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

    private void disableConnectButton(boolean b) {
        this.userField.setDisable(b);
        this.serverField.setDisable(b);
        this.portField.setDisable(b);
        this.connectButton.setDisable(b);
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
