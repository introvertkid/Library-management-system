package library.entity;

import javafx.scene.image.Image;

public class Book {
    private Image thumbnail;
    private String title;
    private String authors;
    private String description;
    private Image qrCode;

    public Book(Image thumbnail, String title, String authors, Image qrCode) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.authors = authors;
        this.qrCode = qrCode;
    }

    public Book(Image thumbnail, String title, String authors, Image qrCode, String description) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.authors = authors;
        this.qrCode = qrCode;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
