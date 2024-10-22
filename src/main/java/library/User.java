package library;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User
{
    private static int ID;
    private static String username;
    private static String password;
    private static String userFullName;
    private static String role;
    private static String gmail;
    private static String phoneNumber;
    //todo: how about date of birth ?
    private static String avatar;

    public static void loadUserData(ResultSet resultSet)
    {
        try {
            ID = resultSet.getInt("userID");
            username = resultSet.getString("username");
            password = resultSet.getString("hashedPassword");
            userFullName = resultSet.getString("userFullName");
            role = resultSet.getString("role");
            gmail = resultSet.getString("gmail");
            phoneNumber = resultSet.getString("phoneNumber");
            avatar = resultSet.getString("avatar");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int newID) {
        ID = newID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        username = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        password = newPassword;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String newFullName) {
        userFullName = newFullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String newRole) {
        role = newRole;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String newGmail) {
        gmail = newGmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String newPhoneNumber) {
        phoneNumber = newPhoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String newAvatar) {
        avatar = newAvatar;
    }
}
