package library;

public class Document {
    private int documentID;
    private String documentName;
    private int tagID;
    private String tagName;
    private String authors;
    private String status;
    private String fileName;
    private int quantity;

    public Document(String documentName, String authors, String tagName, int quantity, int documentID) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.tagName = tagName;
        this.authors = authors;
        this.quantity = quantity;
    }

    public Document(int documentID, String documentName, String authors, String fileName, String status) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.fileName = fileName;
        this.authors = authors;
        this.status = status;
    }

    public Document(int documentID, String documentName, String authors, String fileName, String status, String tagName) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.fileName = fileName;
        this.authors = authors;
        this.status = status;
        this.tagName = tagName;
    }

    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getTagID() {
        return tagID;
    }

    public String getAuthors() {
        return authors;
    }

    public String getTagName() {
        return tagName;
    }

    public String getStatus() {
        return status;
    }

    public String getFileName() {
        return fileName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}