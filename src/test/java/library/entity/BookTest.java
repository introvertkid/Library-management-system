package library.entity;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class BookTest {

    @Test
    public void testConstructorAndGetters() {
        String title = "minhdeptrai";
        String authors = "minhdeptrai";
        String description = "minhdeptrai";
        String publishedDate = "2024-12-11";
        String categories = "Fiction";
        String averageRating = "4.5";
        String bookLink = "http://example.com/book";

        Book book = new Book(null, title, authors, null, description, publishedDate, categories, averageRating, bookLink);

        assertEquals(null, book.getThumbnail());
        assertEquals(title, book.getTitle());
        assertEquals(authors, book.getAuthors());
        assertEquals(null, book.getQrCode());
        assertEquals(description, book.getDescription());
        assertEquals(publishedDate, book.getPublishedDate());
        assertEquals(categories, book.getCategories());
        assertEquals(averageRating, book.getAverageRating());
        assertEquals(bookLink, book.getBookLink());
    }

    @Test
    public void testSetters() {
        String title = "minhdeptrai";
        String authors = "minhdeptrai";
        String description = "minhdeptrai";
        String publishedDate = "2024-12-11";
        String categories = "Fiction";
        String averageRating = "4.5";
        String bookLink = "http://example.com/book";

        Book book = new Book(null, title, authors, null, description, publishedDate, categories, averageRating, bookLink);

        String newDescription = "Updated description";
        String newPublishedDate = "2025-01-01";
        String newBookLink = "http://example.com/newbook";

        book.setDescription(newDescription);
        book.setPublishedDate(newPublishedDate);
        book.setBookLink(newBookLink);

        assertEquals(newDescription, book.getDescription());
        assertEquals(newPublishedDate, book.getPublishedDate());
        assertEquals(newBookLink, book.getBookLink());
    }
}
