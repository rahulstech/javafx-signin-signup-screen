package rahulstech.javafx.example;

import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.getMainWindow().setTitle("Sign Up");
        password.focusedProperty().addListener((prop,oldV,newV) -> {
            if (!newV)
                removeFloatingMessage(password);
            else
                showPasswordHint(password);
        });
        password.textProperty().addListener((prop,oldV,newV) -> {
            removeFloatingMessage(password);
            showPasswordHint(password);
        });
    }

    private void showPasswordHint(PasswordField which) {
        String text = which.getText();
        if (isEmptyString(text) || text.length() < 8) {
            showFloatingMessage(which,"Password length must be minimum 8 characters",ERROR);
            return;
        }
        Matcher symbolMatcher = Pattern.compile("[\\!@\\#\\$\\%\\^\\&\\*\\(\\)\\-\\+]+").matcher(text);
        Matcher digitMatcher = Pattern.compile("[\\d]+").matcher(text);
        if (!symbolMatcher.find()) {
            showFloatingMessage(which,"Password must contain at least one of !@#$%^&*()-+",WARNING);
        }
        else if (!digitMatcher.find()) {
            showFloatingMessage(which,"Password must contain at least one digit",WARNING);
        }
        else {
            showFloatingMessage(which,"Strong Password",SUCCESS);
        }
    }

    public void onChangePasswordVisibility() {
    }

    public void onChangeConfirmPasswordVisibility() {
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
            service.valueProperty().addListener((prop,oldV,newV) -> onCompleteSignUp(newV));
            service.signup(user);
        }
    }

    public void onClickCancel() {
        App.getMainWindow().close();
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
        showFloatingMessage(which,message,ERROR,false);
    }

    private void showFloatingMessage(TextField which, String message, Helper.FloatingMessageType type) {
        showFloatingMessage(which,message,type,true);
    }

    private void showFloatingMessage(TextField which, String message, Helper.FloatingMessageType type, boolean isErrorInfo) {
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

        if (ERROR == type) {
            if (!isErrorInfo) {
                Helper.showFloatingMessage(root, which, floatingMsg, type,
                        Helper.createHorizontalShakeAnimation(floatingMsg));
            }
            else {
                Helper.showFloatingMessage(root, which, floatingMsg, type);
            }
        }
        else {
            Helper.showFloatingMessage(root,which,floatingMsg, type);
        }
    }
}
