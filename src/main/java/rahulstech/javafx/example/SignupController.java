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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rahulstech.javafx.example.Helper.FloatingMessageType.*;
import static rahulstech.javafx.example.Helper.isEmptyString;
import static rahulstech.javafx.example.UserService.ERROR_USERNAME;
import static rahulstech.javafx.example.UserService.SUCCESSFUL;

public class SignupController implements Initializable {

    public TextField username;
    public PasswordField password;
    public PasswordField confirmPassword;
    public TextField inputGivenName;
    public TextField inputFamilyName;
    public Group root;
    public Button btnSignUp;
    public ImageView btnPasswordVisibility;
    public ImageView btnConfirmPasswordVisibility;

    private PasswordVisibilityHelper passwordHelper;
    private PasswordVisibilityHelper confPasswordHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.getMainWindow().setTitle("Sign Up");
        passwordHelper = new PasswordVisibilityHelper(root,password,btnPasswordVisibility);
        confPasswordHelper = new PasswordVisibilityHelper(root,confirmPassword,btnConfirmPasswordVisibility);
        username.focusedProperty().addListener((prop,oldV,newV) -> {
            if (newV)
                showUsernameHint();
            else
                removeUsernameHint();
        });
        passwordHelper.addFocusChangeListener((prop,oldV,newV) -> {
            if (!newV)
                removePasswordHint();
            else
                showPasswordHint();
        });
        password.textProperty().addListener((prop,oldV,newV) -> {
            removePasswordHint();
            showPasswordHint();
        });
        confPasswordHelper.addFocusChangeListener((prop,oldV,newV) -> {
            if (!newV)
                removeConfirmPasswordHint();
            else
                showConfirmPasswordHint();
        });
    }

    private void removeUsernameHint() {
        Helper.removeFloatingLabel(root,username,"msgUsernameHint");
        username.getStyleClass().removeAll("information");
    }

    private void showUsernameHint() {
        String message = "Use english alphanumeric (A-Z,a-z,0-9) minimum 6 length";
        Label floatingMsg = Helper.createFloatingMessage(message,INFORMATION,"msgUsernameHint");
        Helper.showFloatingMessage(root,username,floatingMsg,INFORMATION);
        username.getStyleClass().add("information");
    }

    private void removePasswordHint() {
        TextField field = passwordHelper.currentField();
        Helper.removeFloatingLabel(root,field,"msgPasswordHint");
        field.getStyleClass().removeAll("error","information","success","warning");
    }

    private void showPasswordHint() {
        TextField field = passwordHelper.currentField();
        String text = field.getText();
        Matcher symbolMatcher = Pattern.compile("[\\!@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+]+").matcher(text);
        Matcher digitMatcher = Pattern.compile("[\\d]+").matcher(text);

        Helper.FloatingMessageType type = INFORMATION;
        StringBuilder builder = new StringBuilder();
        if (isEmptyString(text) || text.length() < 8) {
            builder.append("\u2022 at least 8 character long\n");
            type = WARNING;
        }
        if (!symbolMatcher.find()) {
            builder.append("\u2022 contain at least one of the special characters !@#$%^&*()-+\n");
            type = ERROR;
        }
        if (!digitMatcher.find()) {
            builder.append("\u2022 contain at least one digit\n");
            type = ERROR;
        }

        String message;
        if (builder.length() == 0) {
            message = "Strong Password";
            type = SUCCESS;
        }
        else {
            message = "Password must:\n"+builder.toString();
            if (isEmptyString(text)) type = INFORMATION;
        }

        Label floatingMsg = Helper.createFloatingMessage(message,type,"msgPasswordHint");
        Helper.showFloatingMessage(root,field,floatingMsg,type);
    }

    private void removeConfirmPasswordHint() {
        TextField field = confPasswordHelper.currentField();
        Helper.removeFloatingLabel(root,field,"msgConfirmPasswordHint");
        field.getStyleClass().removeAll("error","information","warning","success");
    }

    private void showConfirmPasswordHint() {
        TextField field = confPasswordHelper.currentField();
        Label floatingMsg = Helper.createFloatingMessage("Retype password here",INFORMATION,"msgConfirmPasswordHint");
        Helper.showFloatingMessage(root,field,floatingMsg,INFORMATION);
    }

    public void onChangePasswordVisibility() {
        passwordHelper.toggleVisibility();
    }

    public void onChangeConfirmPasswordVisibility() {
        confPasswordHelper.toggleVisibility();
    }

    public void onClickSignUp() {
        String _username = username.getText();
        String _password = password.getText();
        String _confirmPassword = confirmPassword.getText();
        String givenName = inputGivenName.getText();
        String familyName = inputFamilyName.getText();
        boolean valid = true;

        removeAllFloatingMessage();
        if (isEmptyString(_username)) {
            showErrorFloatingMessage(username,"empty field");
            valid = false;
        }
        else if (!_username.matches("[A-Za-z\\d]{6,}")) {
            showErrorFloatingMessage(username,"invalid username");
            valid = false;
        }
        if (isEmptyString(_password)) {
            showErrorFloatingMessage(password,"empty field");
            valid = false;
        }
        if (isEmptyString(_confirmPassword)) {
            showErrorFloatingMessage(confirmPassword,"empty field");
            valid = false;
        }
        if (!isEmptyString(_password) && !isEmptyString(_confirmPassword) && !_password.equals(_confirmPassword)) {
            showErrorFloatingMessage(confirmPassword,"password not confirmed");
            valid = false;
        }
        if (isEmptyString(givenName)) {
            showErrorFloatingMessage(inputGivenName,"empty field");
            valid = false;
        }
        if (isEmptyString(familyName)) {
            showErrorFloatingMessage(inputFamilyName,"empty field");
            valid = false;
        }

        if (valid) {
            User user = new User();
            user.setUsername(_username);
            user.setPassword(_password);
            user.setGivenName(givenName);
            user.setFamilyName(familyName);

            UserService service = new UserService();
            service.stateProperty().addListener((prop,oldV,newV) -> {
                if ( Worker.State.SCHEDULED == newV) {
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
            service.valueProperty().addListener((prop,oldV,newV) -> onCompleteSignUp(newV));
            service.signup(user);
        }
    }

    public void onClickLogIn() {
        App.loadScreen("login.fxml");
    }

    private void onCompleteSignUp(UserService.Result result) {
        int resultCode = result.getResultCode();
        if (ERROR_USERNAME == resultCode) {
            showErrorFloatingMessage(username,result.getMessage());
        }
        else if (SUCCESSFUL == resultCode){
            handleSuccessfulSignUp(result.getOutput());
        }
    }

    private void handleSuccessfulSignUp(User user) {
        // TODO: implement handleSuccessfulSignUp
    }

    private void removeAllFloatingMessage() {
        removeFloatingMessage(username);
        removeFloatingMessage(password);
        removeFloatingMessage(confirmPassword);
        removeFloatingMessage(inputGivenName);
        removeFloatingMessage(inputFamilyName);
    }

    private void removeFloatingMessage(TextField which) {
        if (which == username) {
            Helper.removeFloatingLabel(root,which,"msgUsername");
        }
        else if (which == password) {
            Helper.removeFloatingLabel(root,which,"msgPassword");
        }
        else if (which == confirmPassword) {
            Helper.removeFloatingLabel(root,which,"msgConfirmPassword");
        }
        else if (which == inputGivenName) {
            Helper.removeFloatingLabel(root,which,"msgGivenName");
        }
        else if (which == inputFamilyName) {
            Helper.removeFloatingLabel(root,which,"msgFamilyName");
        }
        which.getStyleClass().removeAll("error","success","warning","information");
    }

    private void showErrorFloatingMessage(TextField which, String message) {
        Helper.FloatingMessageType type = ERROR;
        Label floatingMsg;
        if (which == username) {
            floatingMsg = Helper.createFloatingMessage(message,type,"msgUsername");
        }
        else if (which == password) {
            floatingMsg = Helper.createFloatingMessage(message,type,"msgPassword");
        }
        else if (which == confirmPassword) {
            floatingMsg = Helper.createFloatingMessage(message,type,"msgConfirmPassword");
        }
        else if (which == inputGivenName) {
            floatingMsg = Helper.createFloatingMessage(message,type,"msgGivenName");
        }
        else if (which == inputFamilyName) {
            floatingMsg = Helper.createFloatingMessage(message,type,"msgFamilyName");
        }
        else return;

        Helper.showFloatingMessage(root, which, floatingMsg, type,
                Helper.createHorizontalShakeAnimation(floatingMsg));
    }

    private void showProgressBar(UserService service) {
        double endHeight = 100;
        double endWidth = 100;
        Node progressBar =  Helper.createCircularInfiniteProgressBar(endHeight,endWidth,
                Color.web("#F06292"),Color.web("#EC407A",.6),Color.web("#EC407A"),
                "signup-progress",
                true, App.class.getResource("close_white.png").toExternalForm(),
                e -> service.cancel());
        Helper.animateButtonToProgressBar(root,btnSignUp,endWidth,endHeight,progressBar);
    }

    private void showOperationStatusAndHideProgressBar(boolean success) {
        Helper.showOperationStatus(root,"signup-progress", success, this::hideProgressBar);
    }

    private void hideProgressBar() {
        Helper.animateProgressBarToButton(root,btnSignUp,220,44,"signup-progress");
    }
}
