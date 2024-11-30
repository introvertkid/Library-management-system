package library.entity;

import library.helper.DatabaseHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Document {
    private int documentID;
    private String documentName;
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

    public static int getSpecificDocumentIDFromDB(String documentName) {
        int ans = 0;
        String query = "select documentID from documents where documentName = ?";

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

    public static String getTagsByDocumentID(int id) {
        String ans = "";
        String query = "select tagName from tags " +
                "join document_tag on tags.tagID = document_tag.tagID " +
                "where document_tag.documentID = " + id;

        try (PreparedStatement stmt = DatabaseHelper.getConnection().prepareStatement(query)) {
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                if (ans != "") ans += ", ";
                ans += res.getString("tagName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ans;
    }


    public Document(int documentID, String documentName, String authors, String fileName, String status, String tagName, int quantity) {
        this.documentID = documentID;
        this.documentName = documentName;
        this.fileName = fileName;
        this.authors = authors;
        this.status = status;
        this.tagName = tagName;
        this.quantity = quantity;
    }

    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
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