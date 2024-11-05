package library;

public class Document {
    private int documentID;
    private String documentName;
    private int categoryID;
    private String authors;

    public Document(int documentID, String documentName, int categoryID, String authors) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.categoryID = categoryID;
        this.authors = authors;
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
}