package client;

import gui.chat.PrivatChatController;
import gui.chat.PublicChatController;
import gui.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatApplication extends Application {

    private static Stage publicStage;
    private static Scene publicScene;

    private static BorderPane borderPane;
    private static ChatApplication chatApplication;
    public static ChatClient chatClient = null;

    private static LoginController loginController;
    private static PublicChatController publicChatController;

    private static URL loginFXML, publicFXML, privateFXML;

    public static final String title = "Socket-based Chat service";
    public static FXMLLoader fxmlLoader;

    public static String correspondent;

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

    @Override
    public void start(Stage primaryStage) throws IOException {

        //Init stage
        publicStage = primaryStage;
        publicStage.setTitle(title);
        try {
            showLoginOnPublicStage("Welcome to socket chat");
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void showLoginOnPublicStage(String title) throws MalformedURLException {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(loginFXML);
        try{
            borderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(borderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnShowing(event -> {
            publicStage.setResizable(false);
            publicStage.setFullScreen(false);
        });
        publicStage.setTitle(title);
        publicStage.show();
    }

    public static void launchPublicChat() throws MalformedURLException {

        publicChatController.setChatClient(chatClient);

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(publicFXML);
        try{
            borderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(borderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnCloseRequest(event -> {
            try {
                publicChatController.closePublicChat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        publicStage.setTitle("Group Chat");
        publicStage.show();
    }

    public static void launchPrivateChat(String user) throws MalformedURLException {
        Stage st = new Stage();
        Scene sc;
        BorderPane borderPane = null;
        ChatApplication.correspondent=user;
        
        PrivatChatController privatChatController = new PrivatChatController();

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(privateFXML);
        try{
            borderPane = fxmlLoader.load();
            sc = new Scene(borderPane);
            st.setScene(sc);

            st.setOnCloseRequest(event -> {
                try {
                    privatChatController.closePrivateChat();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            st.setTitle("Private Chat");
            st.show();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


   /* public static void loadStage(URL fxml) {

        fxmlLoader.setLocation(fxml);
        try {
            borderPane = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("IOException in showView()");
            e.printStackTrace();
            System.exit(1);
        }
        scene = new Scene(borderPane);
        publicStage.setScene(scene);
    }*/


    public void setStagePropertiesToLogin() {

    }

    public static void setStagePropertiesToChat() {
        publicStage.setResizable(true);
        publicStage.setMaximized(true);
        publicStage.setFullScreen(true);


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
//            chatPane.setItems();
//            userPane.setItems(chatClient.getUsers());



            chatClient.connectUser(userName);

            launchPublicChat();  // TODO - A

        }


    }
}
