package library.entity;

import javafx.scene.image.Image;

public class Book {
    private Image thumbnail;
    private String title;
    private String authors;
    private Image qrCode;

    public Book(Image thumbnail, String title, String authors, Image qrCode) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.authors = authors;
        this.qrCode = qrCode;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public Image getQrCode() {
        return qrCode;
    }
}
