package library.entity;

import library.helper.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

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

    public static int getSpecificDocumentIDFromDB(String documentName) {
        int ans = 0;
        String query = "select * from documents where documentName = ?";

        try (PreparedStatement statement = DatabaseHelper.getConnection().prepareStatement(query)) {
            statement.setString(1, documentName);

            ResultSet res = statement.executeQuery();
            if (res.next()) {
                ans = res.getInt("documentID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ans;
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