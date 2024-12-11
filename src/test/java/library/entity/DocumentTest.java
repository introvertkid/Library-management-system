package library.entity;

import static org.junit.Assert.*;

import library.entity.Document;
import org.junit.Test;

public class DocumentTest {

    @Test
    public void testDocumentConstructorWithAllParams() {
        Document document = new Document(1, "Java Programming", "Minh", "java_programming.pdf", "available", "java", 10);
        assertEquals(1, document.getDocumentID());
        assertEquals("Java Programming", document.getDocumentName());
        assertEquals("Minh", document.getAuthors());
        assertEquals("java_programming.pdf", document.getFileName());
        assertEquals("available", document.getStatus());
        assertEquals("java", document.getTagName());
        assertEquals(10, document.getQuantity());
    }

    @Test
    public void testDocumentConstructorWithFileNameAndStatus() {
        Document document = new Document(1, "Java Programming", "Minh", "java_programming.pdf", "available");
        assertEquals(1, document.getDocumentID());
        assertEquals("Java Programming", document.getDocumentName());
        assertEquals("Minh", document.getAuthors());
        assertEquals("java_programming.pdf", document.getFileName());
        assertEquals("available", document.getStatus());
    }

    @Test
    public void testDocumentConstructorWithTags() {
        Document document = new Document(1, "Java Programming", "Minh", "java_programming.pdf", "available", "java");
        assertEquals(1, document.getDocumentID());
        assertEquals("Java Programming", document.getDocumentName());
        assertEquals("Minh", document.getAuthors());
        assertEquals("java_programming.pdf", document.getFileName());
        assertEquals("available", document.getStatus());
        assertEquals("java", document.getTagName());
    }

    @Test
    public void testDocumentConstructorWithQuantity() {
        Document document = new Document(1, "Java Programming", "Minh", 10, "java");
        assertEquals(1, document.getDocumentID());
        assertEquals("Java Programming", document.getDocumentName());
        assertEquals("Minh", document.getAuthors());
        assertEquals(10, document.getQuantity());
        assertEquals("java", document.getTagName());
    }
}
