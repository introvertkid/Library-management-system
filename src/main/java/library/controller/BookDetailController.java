package library.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import library.entity.CommentItem;
import java.util.ArrayList;
import java.util.List;

public class BookDetailController extends Controller {

    @FXML
    private TextFlow details;

    @FXML
    private ImageView bookImage;

    @FXML
    private Button borrowButton;

    @FXML
    private TextArea commentArea;

    @FXML
    private VBox commentList;

    @FXML
    private ScrollPane commentScroll;

    public void initialize() {
        testBookDetail();
        commentArea.setWrapText(true);
        commentList = new VBox();
        commentList.setStyle("-fx-background-color: #FAFAFA;");
        commentScroll.setContent(commentList);
        commentScroll.setStyle("-fx-background-color: #FAFAFA; -fx-border-color: transparent;");


        commentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        Platform.runLater(() -> {
            Node verticalScrollBar = commentScroll.lookup(".scroll-bar:vertical");
            if (verticalScrollBar != null) {
                verticalScrollBar.setStyle("-fx-opacity: 0; -fx-background-color: transparent;");
            }
        });
        List<CommentItem> cmtList = new ArrayList<>();
     cmtList.add( new CommentItem("Lê Sỹ Thái Sơn", "This is a great product!", "/image/UserAvatar/userAvatar.png"));
        cmtList.add( new CommentItem("Thạch Minh Quân", "Very helpful and easy to use.", "/image/UserAvatar/userAvatar.png"));
        cmtList.add(new CommentItem("Phan Đăng Nhật", "Highly recommend this to everyone.", "/image/UserAvatar/userAvatar.png"));
      cmtList.add(new CommentItem("Vũ Cao Phong", "Amazing! Will buy again.", "/image/UserAvatar/userAvatar.png"));
       cmtList.add(new CommentItem(
                "Vũ Nguyễn Trường Minh",
                "This book provided incredible insights into human behavior, relationships, and the challenges people face when dealing with complex situations. I highly recommend this to anyone interested in understanding more about psychological intricacies.",
                "/image/UserAvatar/userAvatar.png"));



        commentList.getChildren().addAll(cmtList);
    }

    public void testBookDetail() {
        String title = "The Swallows";
        String author = "Author: Lisa Lutz";
        String genre = "Genre: Mystery, Thriller";
        String summary = "Summary: A story of intrigue and betrayal unfolds at a private school, "
                + "where secrets are the norm, and students are not as innocent as they appear.";

        String imagePath = "/image/the-swallows-673x1024.jpg";
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        bookImage.setImage(image);
        bookImage.setFitWidth(200);
        bookImage.setPreserveRatio(true);

        Text titleText = new Text(title + "\n\n");
        titleText.setFont(new Font("Arial", 24));
        titleText.setStyle("-fx-font-weight: bold;");

        Text authorText = new Text(author + "\n");
        authorText.setFont(new Font("Arial", 14));

        Text genreText = new Text(genre + "\n");
        genreText.setFont(new Font("Arial", 14));

        Text summaryText = new Text(summary);
        summaryText.setFont(new Font("Arial", 14));

        details.getChildren().clear();
        details.getChildren().addAll(titleText, authorText, genreText, summaryText);
    }

}


