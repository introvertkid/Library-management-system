package library.entity;

import javafx.scene.image.Image;

public class Book {
    private Image thumbnail;
    private String title;
    private String authors;
    private String description;
    private Image qrCode;
    private String publishedDate;
    private String categories;
    private String averageRating;

    public Book(Image thumbnail, String title, String authors, Image qrCode, String description, String publishedDate, String categories, String averageRating) {
        this.thumbnail = thumbnail;
        this.title = title;
        this.authors = authors;
        this.qrCode = qrCode;
        this.description = description;
        this.publishedDate = publishedDate;
        this.categories = categories;
        this.averageRating = averageRating;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getCategories() {
        return categories;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getAverageRating() {
        return averageRating;
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
