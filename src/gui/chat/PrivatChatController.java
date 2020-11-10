package gui.chat;

import client.ChatApplication;
import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class PrivatChatController {
    private ChatClient chatClient = null;
    private ChatApplication chatApplication;
    private final String correspondent;
    /* ----------------------------- @FXML ----------------------------- */
    @FXML
    private TextField msgField;
    @FXML
    private Label chatTitle;
    @FXML
    private Button send_button;
    @FXML
    private ListView chatPane;
    @FXML
    private ListView userPane;
    @FXML
    private ListView userList;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public PrivatChatController(String correspondent) {
        this.correspondent = correspondent;
    }

    /* ----------------------------- SEND PRIVATE ----------------------------- */
    public void sendPrivateAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            ChatApplication.chatClient.sendPrivateMsg(text, correspondent);
            msgField.clear();
        } else {
            flashTextField(this.msgField);
        }
    }

    /* ----------------------------- SETTERS ----------------------------- */
    public void setChatProperties() {
        this.chatTitle.setText("Private (with " + correspondent + ") " + ChatApplication.title);
        this.send_button.setText("Send to " + correspondent);
    }

    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendPrivateAction();
    }

    /* ----------------------------- FIELD PRESSED ----------------------------- */
    public void messageFieldClicked() {
        msgField.setEffect(null);
    }

    /* ----------------------------- VISUAL EFFECTS ----------------------------- */
    public void flashTextField(TextField t) {
        Lighting errorLighting = new Lighting();
        t.setEffect(errorLighting);
    }

    /* ----------------------------- EXIT ----------------------------- */
    public void closePrivateChat() throws IOException {

        //TODO close private window
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
}
