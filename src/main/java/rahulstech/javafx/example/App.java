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

    public static void loadScreen(String fxml) {
        try {
            URL url = App.class.getResource(fxml);
            Parent parent = FXMLLoader.load(url);
            Scene scene = mainWindow.getScene();
            if (null == scene) {
                URL style = App.class.getResource("style.css");
                scene = new Scene(parent);
                scene.getStylesheets().add(style.toExternalForm());
                mainWindow.setScene(scene);
            }
            else {
                scene.setRoot(parent);
                mainWindow.sizeToScene();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainWindow = primaryStage;
        primaryStage.setResizable(false);
        loadScreen("login.fxml");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
