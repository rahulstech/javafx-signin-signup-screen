package rahulstech.javafx.example;

import javafx.animation.*;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.util.Duration;

public class Helper {

    public enum FloatingMessageType {
        ERROR,
        SUCCESS,
        WARNING,
        INFORMATION
    }

    public static Label createFloatingMessage(String message, FloatingMessageType type) {
        Label label = new Label(message);
        label.getStyleClass().add("floating-message");
        if (FloatingMessageType.ERROR == type) {
            label.getStyleClass().add("error");
        }
        else if (FloatingMessageType.SUCCESS == type) {
            label.getStyleClass().add("success");
        }
        else if (FloatingMessageType.WARNING == type) {
            label.getStyleClass().add("warning");
        }
        else if (FloatingMessageType.INFORMATION == type) {
            label.getStyleClass().add("information");
        }
        label.setVisible(false);
        label.setWrapText(true);
        label.setBlendMode(BlendMode.SRC_OVER);

        return label;
    }

    public static void showFloatingMessage(Group container, Control msgFor, Label floatingMsg) {
        double x = msgFor.getLayoutX();
        double y = msgFor.getLayoutY();
        double height = msgFor.getHeight();

        container.getChildren().add(floatingMsg);
        floatingMsg.relocate(x,y+height);
        floatingMsg.setVisible(true);
    }

    public static void showFloatingMessage(Group container, Control msgFor, Label floatingMsg, Animation enterAnimation) {
        showFloatingMessage(container, msgFor, floatingMsg);
        enterAnimation.play();
    }

    public static Animation createHorizontalShakeAnimation(Node node) {
        Timeline timeline = new Timeline();

        KeyValue v1 = new KeyValue(node.translateXProperty(),0);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO,v1);

        KeyValue v2 = new KeyValue(node.translateXProperty(),-25);
        KeyFrame frame2 = new KeyFrame(Duration.millis(100),v2);

        KeyValue v3 = new KeyValue(node.translateXProperty(),25);
        KeyFrame frame3 = new KeyFrame(Duration.millis(150),v3);

        KeyValue v4 = new KeyValue(node.translateXProperty(),-25);
        KeyFrame frame4 = new KeyFrame(Duration.millis(200),v4);

        KeyValue v5 = new KeyValue(node.translateXProperty(),0);
        KeyFrame frame5 = new KeyFrame(Duration.millis(250),v5);

        timeline.getKeyFrames().addAll(frame1,frame2,frame3,frame4,frame5);

        return timeline;
    }

    public static boolean isEmptyString(String s) {
        return  s == null || "".equals(s);
    }
}
