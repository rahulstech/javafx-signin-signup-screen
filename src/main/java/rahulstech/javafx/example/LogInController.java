package rahulstech.javafx.example;

import javafx.concurrent.Worker;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

import static rahulstech.javafx.example.Helper.*;
import static rahulstech.javafx.example.Helper.FloatingMessageType.*;

public class LogInController implements Initializable {

    public TextField username;
    public PasswordField password;
    public ImageView btnPasswordVisibility;
    public Group root;
    public Button btnLogin;

    private Node progressBar = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        App.getMainWindow().setTitle("Log In");
    }

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
            service.stateProperty().addListener((prop,oldV,newV) -> {
                if (Worker.State.RUNNING == newV || Worker.State.SCHEDULED == newV) {
                    showProgressBar(service);
                }
                else if (Worker.State.FAILED == newV || Worker.State.SUCCEEDED == newV
                        || Worker.State.CANCELLED == newV) {
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
       // TODO: implement password visibility change method
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
        // TODO: implement handleSuccessfulLogIn
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

    private void showProgressBar(UserService service) {
        double endHeight = 100;
        double endWidth = 100;
        this.progressBar =  Helper.createCircularInfiniteProgressBar(endHeight,endWidth,
                Color.web("#F06292"),Color.web("#EC407A",.6),Color.web("#EC407A"),
                true, App.class.getResource("close_white.png").toExternalForm(),
                e -> service.cancel());
        Helper.animateButtonToProgressBar(root,btnLogin,endWidth,endHeight,progressBar);
    }

    private void hideProgressBar() {
        if (null == progressBar) return;
        Node progressBar = this.progressBar;
        Helper.animateProgressBarToButton(root,btnLogin,220,44,progressBar);
        this.progressBar = null;
    }
}
