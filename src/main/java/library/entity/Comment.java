package library.entity;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Comment extends VBox {

    public Comment(String userName, String comment, String avatarPath) {
        // Avatar
        ImageView avatar = new ImageView(new Image(avatarPath));
        avatar.setFitWidth(50);
        avatar.setFitHeight(50);


        Circle clip = new Circle(25, 25, 25);
        avatar.setClip(clip);
        // Full name
        Label userNameLabel = new Label(userName);
        userNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-wrap-text: true;");
        userNameLabel.setMaxWidth(200);

        // Comment
        TextFlow commentTextFlow = new TextFlow();
        commentTextFlow.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");
        commentTextFlow.setPadding(new Insets(5, 0, 0, 0));

        Text commentText = new Text(comment);
        commentTextFlow.getChildren().add(commentText);

        // Avatar and user full name are aligned horizontally
        HBox userInfo = new HBox(15, avatar, userNameLabel);
        userInfo.setPadding(new Insets(5, 10, 5, 10));
        userInfo.setStyle("-fx-alignment: center-left;");

        Line separator = new Line(0, 0, 274, 0);
        separator.setStyle("-fx-stroke: #ddd;");

        this.getChildren().addAll(userInfo, commentTextFlow, separator);
        this.setPadding(new Insets(10));
        this.setSpacing(8);
        this.setPrefWidth(274);
        this.setStyle("-fx-background-color: #FAFAFA;");
    }
}
