package rahulstech.javafx.example;

import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static rahulstech.javafx.example.Helper.*;
import static rahulstech.javafx.example.Helper.FloatingMessageType.ERROR;

public class LogInController implements Initializable {

    public TextField username;
    public PasswordField password;
    public ImageView btnPasswordVisibility;
    public Group root;

    private Optional<Node> msgUsername = Optional.empty();
    private Optional<Node> msgPassword = Optional.empty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void onClickLogIn() {
        String _username = username.getText();
        String _password = password.getText();
        boolean canLogIn = true;

        Node n1 = msgUsername.orElseGet(() -> null);
        if (null != n1) root.getChildren().remove(n1);
        Node n2 = msgPassword.orElseGet(() -> null);
        if (null != n2)root.getChildren().remove(n2);
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
            service.valueProperty().addListener((prop,odlV,newV) -> {
                if (UserService.LOGIN == newV.getTaskCode()) {
                    onLogInTaskComplete(newV);
                }
            });
            service.login(_username,_password);
        }
    }

    public void onClickCancel() {
        App.getMainWindow().close();
    }

    public void onClickForgetPassword() {
    }

    public void onClickSignUp() {
    }

    public void onChangePasswordVisibility() {
    }

    private void onLogInTaskComplete(UserService.Result result) {
        int resultCode = result.getResultCode();
        Node n1 = msgUsername.orElseGet(() -> null);
        if (null != n1) root.getChildren().remove(n1);
        Node n2 = msgPassword.orElseGet(() -> null);
        if (null != n2)root.getChildren().remove(n2);
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
        System.out.println("remove -> "+which.getId());
        if (which == username) {

            msgUsername.ifPresent(n -> {
                System.out.println("msgUsername -> "+msgUsername.isPresent());
                root.getChildren().remove(n);
            });
        }
        else if (which == password) {

            msgPassword.ifPresent(n -> {
                System.out.println("msgPassword -> "+msgPassword.isPresent());
                root.getChildren().remove(n);
            });
        }
    }

    private void showFloatingMessage(TextField which, String message, FloatingMessageType type) {
        Label floatingMsg = createFloatingMessage(message,type);
        if (which == username)
            msgUsername = Optional.of(floatingMsg);
        else if (which == password)
            msgPassword = Optional.of(floatingMsg);

        if (ERROR == type) {
            Helper.showFloatingMessage(root,which,floatingMsg,createHorizontalShakeAnimation(floatingMsg));
        }
        else {
            Helper.showFloatingMessage(root,which,floatingMsg);
        }
    }
}
