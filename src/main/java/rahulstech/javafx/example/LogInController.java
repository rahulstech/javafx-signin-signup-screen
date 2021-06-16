package rahulstech.javafx.example;

import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

import static rahulstech.javafx.example.Helper.*;
import static rahulstech.javafx.example.Helper.FloatingMessageType.*;

public class LogInController implements Initializable {

    public TextField username;
    public PasswordField password;
    public ImageView btnPasswordVisibility;
    public Group root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void onClickLogIn() {
        String _username = username.getText();
        String _password = password.getText();
        boolean canLogIn = true;

        removeFloatingMessage(password);
        removeFloatingMessage(username);
        if (isEmptyString(_username)) {
            showFloatingMessage(username,"empty field",ERROR);
            canLogIn = false;
        }
        if (isEmptyString(_password)) {
            showFloatingMessage(password,"empty field",ERROR);
            canLogIn = false;
        }
        if (canLogIn){
            UserService service = new UserService();
            service.valueProperty().addListener((prop,odlV,newV) -> onLogInTaskComplete(newV));
            service.login(_username,_password);
        }
    }

    public void onClickCancel() {
        App.getMainWindow().close();
    }

    public void onClickForgetPassword() {
    }

    public void onClickSignUp() {
        App.loadScreen("signup.fxml");
    }

    public void onChangePasswordVisibility() {
    }

    private void onLogInTaskComplete(UserService.Result result) {
        int resultCode = result.getResultCode();
        removeFloatingMessage(username);
        removeFloatingMessage(password);
        if (UserService.ERROR_NO_USER == resultCode) {
            showFloatingMessage(username,result.getMessage(),ERROR);
        }
        else if (UserService.ERROR_PASSWORD_MISMATCH == resultCode) {
            showFloatingMessage(password,result.getMessage(),ERROR);
        }
        else {
            handleSuccessfulLogIn(result.getOutput());
        }
    }

    private void handleSuccessfulLogIn(User user) {

    }

    private void removeFloatingMessage(TextField which) {
        if (which == username) {
            Helper.removeFloatingLabel(root, which,"msgUsername");
        }
        else if (which == password ) {
            Helper.removeFloatingLabel(root,which, "msgPassword");
        }
    }

    private void showFloatingMessage(TextField which, String message, FloatingMessageType type) {
        Label floatingMsg;
        if (which == username) {
            floatingMsg = createFloatingMessage(message,type,"msgUsername");
        }
        else if (which == password) {
            floatingMsg = createFloatingMessage(message,type,"msgPassword");
        }
        else
            return;

        if (ERROR == type) {
            Helper.showFloatingMessage(root,which,floatingMsg,type,createHorizontalShakeAnimation(floatingMsg));
        }
        else {
            Helper.showFloatingMessage(root,which,floatingMsg, type);
        }
    }
}
