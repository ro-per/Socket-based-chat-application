package client;

import gui.chat.PrivatChatController;
import gui.chat.PublicChatController;
import gui.login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatApplication extends Application {

    private static Stage stage;
    private static Scene scene;

    private static BorderPane borderPane;
    private static ChatApplication chatApplication;
    public static ChatClient chatClient = null;

    private static LoginController loginController;
    private static PublicChatController publicChatController;

    private static URL loginFXML, publicFXML, privateFXML;

    public static final String title = "Socket-based Chat service";


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
        stage = primaryStage;
        stage.setTitle(title);
        try {
            showLogin();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void showLogin() throws MalformedURLException {
        loadStage(loginFXML);

        stage.setOnShowing(event -> {
            setStagePropertiesToLogin();
        });
        stage.show();
    }

    public static void showPublicChat() throws MalformedURLException {
        publicChatController.setChatClient(chatClient);
        loadStage(publicFXML);



        stage.setOnCloseRequest(event -> {
            try {
                publicChatController.closePublicChat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        stage.show();
    }

    public static void showPrivateChat(String otherUser) throws MalformedURLException {
        PrivatChatController privatChatController = new PrivatChatController(otherUser);
        privatChatController.setChatClient(chatClient);

        loadStage(privateFXML);

        stage.setOnCloseRequest(event -> {
            try {
                privatChatController.closePrivateChat();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        stage.show();
    }


    public static void loadStage(URL fxml) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxml);
        try {
            borderPane = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("IOException in showView()");
            e.printStackTrace();
            System.exit(1);
        }
        scene = new Scene(borderPane);
        stage.setScene(scene);
    }


    public void setStagePropertiesToLogin() {
        stage.setResizable(false);
        stage.setFullScreen(false);
    }

    public static void setStagePropertiesToChat() {
        stage.setResizable(true);
        stage.setMaximized(true);
        stage.setFullScreen(true);


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

            showPublicChat();  // TODO - A

            chatClient.connectUser(userName);

        }



    }
}
