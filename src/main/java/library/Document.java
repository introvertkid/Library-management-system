package library;

public class Document {
    private int documentID;
    private String documentName;
    private String category;
    private String authors;
    private int quantity;

    public Document(int documentID, String documentName, String authors, String category, int quantity) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.category = category;
        this.authors = authors;
        this.quantity = quantity;
    }

    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthors() {
        return authors;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}