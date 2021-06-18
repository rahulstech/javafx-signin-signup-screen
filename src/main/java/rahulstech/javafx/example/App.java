/**
 * Copyright 2021 rahulstch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
