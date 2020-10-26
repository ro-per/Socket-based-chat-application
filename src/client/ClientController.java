package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

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

        if (client.start()) {
            chatPane.setItems(client.getMessages());
        }
    }

    public void sendMessage() throws IOException {
        String msg = msgField.getText();
        if (!msg.isEmpty()) {
            msgField.clear();
            ClientThread.send(msg);
        } else {
            msgField.setText(ERROR_EMPTY_MESSAGE);
            flashTextField(this.msgField);
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
    /* ----------------------------- CLEAR SCREEN ----------------------------- */

    private void clearUserList() {
        /** TODO : clear the user list */

    }

    private void clearMessageList() {
        /** TODO : clear the chat list */

    }


    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendMessage();
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


    public void exit() throws IOException {
        //serverthread.onleaving

        ClientThread.stop();

        Platform.exit();
        System.exit(0);
    }
}
