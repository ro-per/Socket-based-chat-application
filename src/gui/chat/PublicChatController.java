package gui.chat;

import client.ChatApplication;
import client.ChatClient;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.MalformedURLException;

public class PublicChatController {
    private ChatClient chatClient = null;
    private ChatApplication chatApplication;

    /* ----------------------------- @FXML ----------------------------- */
    @FXML
    private TextField msgField;
    @FXML
    private Label chatTitle;
    @FXML
    private ListView chatPane;
    @FXML
    private ListView userPane;
    @FXML
    private ListView userList;

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public PublicChatController() {
    }


    /* ----------------------------- SEND BROADCAST ----------------------------- */
    public void sendBroadcastAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            ChatApplication.chatClient.sendBroadcastMsg(text);
            msgField.clear();
        } else {
            flashTextField(this.msgField);
        }
    }


    /* ----------------------------- SETTERS ----------------------------- */

    public void initialize(){
        chatPane.setItems(ChatApplication.chatClient.getMessages());
        userPane.setItems(ChatApplication.chatClient.getUsers());

    }

    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendBroadcastAction();
    }

    @FXML
    public void clickUserName(MouseEvent ae) throws MalformedURLException {

        //TODO !!!
        /*Object selectedUser = userPane.getSelectionModel().getSelectedItem();
        System.out.println(selectedUser.toString());*/

//        showPrivateChat("romeo");


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
    public void closePublicChat() throws IOException {
        //Only  perform leave is chatclient is started
        if (chatClient != null) {
            chatClient.leave();
        }


        Platform.exit();
        System.exit(0);
    }

    public void setChatClient(ChatClient chatClient) {
        this.chatClient=chatClient;
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
