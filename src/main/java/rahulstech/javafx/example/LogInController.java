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

import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

import static rahulstech.javafx.example.Helper.*;
import static rahulstech.javafx.example.Helper.FloatingMessageType.*;

/**
 * Controller for login
 */
public class LogInController implements Initializable {

    public TextField username;
    public PasswordField password;
    public ImageView btnPasswordVisibility;
    public Group root;
    public Button btnLogin;

    private PasswordVisibilityHelper passwordHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.getMainWindow().setTitle("Log In");
        passwordHelper = new PasswordVisibilityHelper(root,password,btnPasswordVisibility);
    }

    public void onClickForgetPassword() {
        // implement this method to handle forget password
    }

    public void onClickLogIn() {
        String _username = username.getText();
        String _password = password.getText();
        boolean canLogIn = true;

        removeAllErrorFloatingMessage();
        if (isEmptyString(_username)) {
            showErrorFloatingMessage(username,"empty field");
            canLogIn = false;
        }
        if (isEmptyString(_password)) {
            showErrorFloatingMessage(password,"empty field");
            canLogIn = false;
        }
        if (canLogIn){
            UserService service = new UserService();
            service.stateProperty().addListener((prop,oldV,newV) -> {
                if (Worker.State.SCHEDULED == newV) {
                    showProgressBar(service);
                }
                else if (Worker.State.SUCCEEDED == newV) {
                    // it means task executed successfully
                    // but may contain error
                    UserService.Result result = service.getValue();
                    int resultCode = result.getResultCode();
                    showOperationStatusAndHideProgressBar(UserService.SUCCESSFUL == resultCode);
                }
                else if (Worker.State.FAILED == newV) {
                    showOperationStatusAndHideProgressBar(false);
                }
                else if (Worker.State.CANCELLED == newV) {
                    hideProgressBar();
                }
            });
            service.valueProperty().addListener((prop,odlV,newV) -> onLogInTaskComplete(newV));
            service.login(_username,_password);
        }
    }

    public void onClickSignUp() {
        App.loadScreen("signup.fxml");
    }

    public void onChangePasswordVisibility() {
        passwordHelper.toggleVisibility();
    }

    private void onLogInTaskComplete(UserService.Result result) {
        int resultCode = result.getResultCode();
        if (UserService.ERROR_NO_USER == resultCode) {
            showErrorFloatingMessage(username,result.getMessage());
        }
        else if (UserService.ERROR_PASSWORD_MISMATCH == resultCode) {
            showErrorFloatingMessage(password,result.getMessage());
        }
        else {
            handleSuccessfulLogIn(result.getOutput());
        }
    }

    private void handleSuccessfulLogIn(User user) {
        // use this method to store the logged in user
        // currently there is no use for this method
    }

    private void removeAllErrorFloatingMessage() {
        removeFloatingMessage(username,"msgUsername", "error");
        removeFloatingMessage(passwordHelper.currentField(),"msgPassword","error");
        passwordHelper.getVisibleField().getStyleClass().remove("error");
        passwordHelper.getNotVisibleField().getStyleClass().remove("error");
    }

    private void removeFloatingMessage(TextField which,  String floatingMsgId, String removeControlStyleClass) {
        Helper.removeFloatingLabel(root,floatingMsgId);
        which.getStyleClass().removeAll(removeControlStyleClass);
    }

    private void showErrorFloatingMessage(TextField which, String message) {
        Label floatingMsg;
        if (which == username) {
            floatingMsg = createFloatingMessage(message,ERROR,"msgUsername",
                    () -> removeFloatingMessage(which,"msgUsername","error"));

        }
        else if (which == password) {
            floatingMsg = createFloatingMessage(message,ERROR,"msgPassword",
                    () -> {
                removeFloatingMessage(which, "msgPassword","error");
                passwordHelper.getVisibleField().getStyleClass().remove("error");
                passwordHelper.getNotVisibleField().getStyleClass().remove("error");
            });
            passwordHelper.getVisibleField().getStyleClass().add("error");
            passwordHelper.getNotVisibleField().getStyleClass().add("error");
        }
        else
            return;

        Helper.showFloatingMessage(root,which,floatingMsg,createHorizontalShakeAnimation(floatingMsg));
        which.getStyleClass().add("error");
    }

    private void showProgressBar(UserService service) {
        double endHeight = 100;
        double endWidth = 100;
        Node progressBar = Helper.createCircularInfiniteProgressBar(endHeight,endWidth,
                Color.web("#F06292"),Color.web("#EC407A",.6),Color.web("#EC407A"),
                "login-progress",
                true, App.class.getResource("close_white.png").toExternalForm(),
                () -> service.cancel());
        Helper.animateButtonToProgressBar(root,btnLogin,endWidth,endHeight,progressBar);
    }

    private void showOperationStatusAndHideProgressBar(boolean success) {
        Helper.showOperationStatus(root,"login-progress", success, this::hideProgressBar);
    }

    private void hideProgressBar() {
        Helper.animateProgressBarToButton(root,btnLogin,220,44,"login-progress");
    }
}
