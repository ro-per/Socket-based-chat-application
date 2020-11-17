package client;

import gui.chat.PrivatChatController;
import gui.chat.PublicChatController;
import gui.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ChatApplication extends Application {

    private static Stage publicStage;
    private static Scene publicScene;

    private static Stage privateStage;
    private static Scene privateScene;

    private static BorderPane publicBorderPane;
    private static BorderPane privateBorderPane;


    private static ChatApplication chatApplication;
    public static ChatClient chatClient = null;

    private static LoginController loginController;
    private static PublicChatController publicChatController;
    private static PrivatChatController privatChatController;

    private static URL loginFXML, publicFXML, privateFXML;

    public static final String title = "Socket-based Chat service";
    public static FXMLLoader fxmlLoader;

    public static String correspondent = null;

    public ChatApplication() throws MalformedURLException {
        //Stage attributes


        //Controllers
        publicChatController = new PublicChatController();
        loginController = new LoginController();

        //Application
        if (chatApplication == null) {
            chatApplication = this;
        }
        //FXML paths
        String gui_path = "file:src/gui/";
        loginFXML = new URL(gui_path + "login/LoginForm.fxml");
        publicFXML = new URL(gui_path + "chat/PublicChat.fxml");
        privateFXML = new URL(gui_path + "chat/PrivateChat.fxml");
    }

    public static boolean askOpenNewChat(String newUser) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, newUser + " wants to send you a msg, open chat ?", ButtonType.YES, ButtonType.NO);
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return ButtonType.YES.equals(result);
    }

    public static boolean askCloseCurrentChat(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close this private chat with " + correspondent + "?", ButtonType.YES, ButtonType.NO);
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        if(ButtonType.NO.equals(result)){
            event.consume();
        }
        return ButtonType.YES.equals(result);
    }


    @Override
    public void start(Stage primaryStage) {

        //Init stage
        publicStage = primaryStage;
        publicStage.setTitle(title);
        try {
            showLogin("Welcome to socket chat");
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    public static void showLogin(String title) {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(loginFXML);
        try {
            publicBorderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(publicBorderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnShowing(event -> {
            publicStage.setResizable(false);
            publicStage.setFullScreen(false);
        });
        publicStage.setTitle(title);
        publicStage.show();
    }

    public static void launchPublicChat() {
        publicChatController.setChatClient(chatClient);

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(publicFXML);
        try {
            publicBorderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(publicBorderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnCloseRequest(event -> {
            try {
                publicChatController.closePublicChat(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        publicStage.setTitle("Group Chat");
        publicStage.show();
    }

    public static void closePrivateChat() {
        if (privateStage != null) {
            privateStage.close();
            correspondent = null;
        }
        chatClient.resetPrivateChat();

    }

    public static void launchPrivateChat(String user) {

        privateStage = new Stage();

        correspondent = user;

        PrivatChatController privatChatController = new PrivatChatController();

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(privateFXML);
        try {
            privateBorderPane = fxmlLoader.load();
            privateScene = new Scene(privateBorderPane);
            privateStage.setScene(privateScene);

            privateStage.setOnCloseRequest(event -> {
                try {
                    boolean b = askCloseCurrentChat(event);

                    if( b) {
                        privatChatController.closePrivateChat();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            privateStage.setTitle("Private Chat wit " + user);
            privateStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void resetPrivateChat() {
        if (privateStage != null) {
            privateStage.close();
        }
        correspondent = null;
        chatClient.resetPrivateChat();

    }


    public static ChatApplication getApplication() {
        return chatApplication;
    }

    public static ChatClient getChatClient() {
        return chatClient;
    }

    public static void connectToServer(String userName, String serverName, int portNumber) throws IOException {
        chatClient = new ChatClient(userName, serverName, portNumber);
        if (chatClient.start()) {
            chatClient.connectUser(userName);
            launchPublicChat();
        }
    }
}
