package rahulstech.javafx.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    private static Stage mainWindow;

    public static Stage getMainWindow() {
        return mainWindow;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        URL view = getClass().getResource("login.fxml");
        URL style = getClass().getResource("style.css");
        Parent root = FXMLLoader.load(view);
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(style.toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
