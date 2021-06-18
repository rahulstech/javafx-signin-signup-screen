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

import javafx.animation.*;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import static rahulstech.javafx.example.Helper.FloatingMessageType.*;
import static rahulstech.javafx.example.Helper.FloatingMessageType.SUCCESS;

public class Helper {

    public enum FloatingMessageType {
        ERROR,
        SUCCESS,
        WARNING,
        INFORMATION
    }

    public static Label createFloatingMessage(String message, FloatingMessageType type, String id) {
        Label label = new Label(message);
        label.setId(id);
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

    public static void showFloatingMessage(Group container, Control msgFor, Label floatingMsg, FloatingMessageType type) {

        double x = msgFor.getLayoutX();
        double y = msgFor.getLayoutY();
        double height = msgFor.getHeight();
        double width = msgFor.getWidth();

        container.getChildren().add(floatingMsg);
        floatingMsg.relocate(x,y+height+6);
        floatingMsg.setVisible(true);
        floatingMsg.setMaxWidth(width);

        if (ERROR == type) {
            msgFor.getStyleClass().add("error");
        }
        else if (INFORMATION == type){
            msgFor.getStyleClass().add("information");
        }
        else if (WARNING == type) {
            msgFor.getStyleClass().add("warning");
        }
        else if (SUCCESS == type) {
            msgFor.getStyleClass().add("success");
        }
    }

    public static void showFloatingMessage(Group container, Control msgFor, Label floatingMsg, FloatingMessageType type, Animation enterAnimation) {
        showFloatingMessage(container, msgFor, floatingMsg, type);
        enterAnimation.play();
    }

    public static void removeFloatingLabel(Group container, TextField msgFor, String id) {
        Node floatingMsg = container.lookup("#"+id);
        if (null != floatingMsg) {
            floatingMsg.toBack();
            floatingMsg.setVisible(false);
            container.getChildren().remove(floatingMsg);
        }
        msgFor.getStyleClass().removeAll("error","information","success","warning");
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

    public static void animateButtonToProgressBar(Group root, Control control, double endWidth, double endHeight, Node progressBar) {

        double height = control.getHeight();
        double width = control.getWidth();
        double scaleX = endWidth/width;
        double scaleY = endHeight/height;
        double positionX = control.getLayoutX()+endWidth/2;
        double positionY = control.getLayoutY()-endHeight/4;

        progressBar.setOpacity(0);
        root.getChildren().add(progressBar);
        progressBar.relocate(positionX,positionY);

        Timeline animation = new Timeline();

        KeyValue v1 = new KeyValue(control.scaleXProperty(),control.getScaleX());
        KeyValue v2 = new KeyValue(control.scaleYProperty(),control.getScaleY());
        KeyValue v5 = new KeyValue(control.opacityProperty(), 1);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO,v1,v2,v5);

        KeyValue v6 = new KeyValue(progressBar.opacityProperty(),0);
        KeyValue v8 = new KeyValue(control.opacityProperty(),0);
        KeyFrame frame3 = new KeyFrame(Duration.millis(220),v6,v8);

        KeyValue v3 = new KeyValue(control.scaleXProperty(),scaleX);
        KeyValue v4 = new KeyValue(control.scaleYProperty(),scaleY);
        KeyValue v7 = new KeyValue(progressBar.opacityProperty(), 1);
        KeyFrame frame2 = new KeyFrame(Duration.millis(300),v3,v4,v7);

        animation.getKeyFrames().addAll(frame1,frame2,frame3);
        animation.play();
    }

    public static void animateProgressBarToButton(Group root, Control control, double endWidth, double endHeight, String id) {

        Node progressBar = root.lookup("#"+id);

        double height = control.getHeight();
        double width = control.getWidth();
        double scaleX = endWidth/width;
        double scaleY = endHeight/height;

        Timeline animation = new Timeline();

        KeyValue v4 = new KeyValue(progressBar.opacityProperty(),0);
        KeyValue v5 = new KeyValue(control.opacityProperty(),1);
        KeyFrame frame2 = new KeyFrame(Duration.millis(120),v4,v5);

        KeyValue v6 = new KeyValue(control.scaleXProperty(),scaleX);
        KeyValue v7 = new KeyValue(control.scaleYProperty(),scaleY);
        KeyFrame frame3 = new KeyFrame(Duration.millis(300),v6,v7);

        animation.getKeyFrames().addAll(frame2,frame3);
        animation.setOnFinished(e -> {
            progressBar.toBack();
            progressBar.setVisible(false);
            root.getChildren().remove(progressBar);
        });
        animation.play();
    }

    public static Node createCircularInfiniteProgressBar(double height, double width,
                                                         Color baseColor, Color trackColor, Color thumbColor,
                                                         String id,
                                                         boolean showCenterButton, String centerButtonImage,
                                                         EventHandler<MouseEvent> centerButtonClickHandler) {
        Group progressBar = new Group();
        progressBar.setId(id);

        double centerX = width/2;
        double centerY = height/2;
        double radius = Math.min(width,height)/2-10;
        double trackWidth = 15;

        Circle base = new Circle();
        base.setCenterX(centerX);
        base.setCenterY(centerY);
        base.setRadius(radius-6);
        base.setFill(baseColor); // Color.web("#F06292")

        progressBar.getChildren().add(base);

        Path track = new Path();
        track.getElements().add(new MoveTo(centerX,centerY+radius));
        track.getElements().add(new ArcTo(radius,radius,360,centerX-.1,centerY+radius,true,false));
        track.setStroke(trackColor); // Color.web("#EC407A",.6)
        track.setStrokeWidth(trackWidth);

        progressBar.getChildren().add(track);

        Arc thumb = new Arc();
        thumb.setCenterX(centerX);
        thumb.setCenterY(centerY);
        thumb.setRadiusX(radius);
        thumb.setRadiusY(radius);
        thumb.setStartAngle(0);
        thumb.setLength(16.66*Math.PI);
        thumb.setStroke(thumbColor); // Color.web("#EC407A")
        thumb.setStrokeWidth(trackWidth);

        progressBar.getChildren().add(thumb);

        Timeline anim = new Timeline();

        KeyValue v2 = new KeyValue(thumb.startAngleProperty(),-360);
        KeyFrame frame2 = new KeyFrame(Duration.millis(1800),v2);

        anim.getKeyFrames().addAll(frame2);

        anim.setCycleCount(Timeline.INDEFINITE);
        anim.play();

        if (showCenterButton) {
            double btnCloseWidth = 1.41 * radius - 5;
            double btnCloseHeight = 1.41 * radius - 5;
            double btnPositionX = centerX - btnCloseWidth/2 ;
            double btnPositionY = centerY - btnCloseHeight/2;

            Image btnCloseImage = new Image(centerButtonImage,
                    btnCloseWidth, btnCloseHeight, true, true);
            ImageView btnClose = new ImageView(btnCloseImage);
            progressBar.getChildren().add(btnClose);
            btnClose.relocate(btnPositionX, btnPositionY);
            if (null != centerButtonClickHandler)
                btnClose.setOnMouseClicked(centerButtonClickHandler);
        }
        return progressBar;
    }

    public static void showOperationStatus(Group container, String progressBarId, boolean success, Runnable onAnimFinish) {

        Node progressBar = container.lookup("#"+progressBarId);

        Bounds boundsInParent = progressBar.getBoundsInParent();
        Bounds layoutBounds = progressBar.getLayoutBounds();

        double positionX = boundsInParent.getMinX()+13;
        double positionY = boundsInParent.getMinY()+13;
        Color baseColor = success ? Color.web("#43A047") : Color.web("#f44336");
        String icon = success ? App.class.getResource("done_white.png").toExternalForm()
                : App.class.getResource("close_white.png").toExternalForm();

        Group view = new Group();
        view.setId(progressBarId);

        double centerX = (layoutBounds.getMinX()+layoutBounds.getMaxX())*0.5;
        double centerY = (layoutBounds.getMinY()+layoutBounds.getMaxY())*0.5;
        double radius = Math.min(centerX,centerY)-8;
        double finalRadius = radius+8;

        Circle base = new Circle();
        base.setCenterX(centerX);
        base.setCenterY(centerY);
        base.setRadius(radius-6);
        base.setFill(baseColor);

        view.getChildren().add(base);

        Timeline anim = new Timeline();

        KeyValue v2 = new KeyValue(base.radiusProperty(),finalRadius);
        KeyFrame frame2 = new KeyFrame(Duration.millis(300),v2);

        KeyFrame frame3 = new KeyFrame(Duration.millis(1500));

        anim.getKeyFrames().addAll(frame2,frame3);
        anim.setOnFinished(e -> {
            progressBar.toBack();
            progressBar.setVisible(false);
            container.getChildren().remove(progressBar);
            if (null != onAnimFinish)
                onAnimFinish.run();
        });

        double iconWidth = 1.41*radius-5;
        double iconHeight = 1.41*radius-5;
        double iconX = centerX - iconWidth/2;
        double iconY = centerY - iconHeight/2;

        Image btnCloseImage = new Image(icon,
                iconWidth, iconHeight, true, true);
        ImageView btnClose = new ImageView(btnCloseImage);

        view.getChildren().add(btnClose);
        btnClose.relocate(iconX, iconY);

        container.getChildren().add(view);
        view.relocate(positionX,positionY);

        anim.play();
    }

    public static boolean isEmptyString(String s) {
        return  s == null || "".equals(s);
    }
}
