package library.entity;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class Comment extends VBox {
    private int commentID;
    private int documentID;
    private int userID;
    private String content;
    private String userFullName;
    private String avatar;

    // Constructor
    public Comment() {}

    public Comment(int commentID, int documentID, int userID, String content) {
        this.commentID = commentID;
        this.documentID = documentID;
        this.userID = userID;
        this.content = content;
    }

    // Getters and Setters
    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Comment(String userName, String comment, String avatarPath) {
        // Avatar
        ImageView avatar = new ImageView(new Image(avatarPath));
        avatar.setFitWidth(30);
        avatar.setFitHeight(30);

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