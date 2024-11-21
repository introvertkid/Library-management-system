package library;

public class Document {
    private int documentID;
    private String documentName;
    private int categoryID;
    private String authors;
    private String status;
    private String fileName;
    private int quantity;

    public Document(String documentName, String authors, String category, int quantity, int documentID) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.categoryID = categoryID;
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

    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public String getAuthors() {
        return authors;
    }

    public String getStatus() {return status;}

    public String getFileName() {return fileName;}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}