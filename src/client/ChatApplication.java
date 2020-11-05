package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {

    private static Stage primaryStageObj;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStageObj = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ChatView_v2.fxml"));
        Parent root = (Parent) loader.load();
        ClientController controller = loader.<ClientController>getController();

        Scene scene = new Scene(root, 300, 275);

        String title = "Socket-based Chat service";
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(false);
//        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setOnCloseRequest(event -> {
            try {
                controller.exit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        primaryStage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }

}
