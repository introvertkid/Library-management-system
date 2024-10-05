package main;

import java.sql.*;

public class DatabaseHelper
{
    private static final String URL = "jdbc:mysql://localhost:3306/envi";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "bindz1403";

    private static Connection connection;

    public static void connectToDatabase()
    {
        try
        {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connect to database successfully");
//            conn.close();
        }
        catch (SQLException e)
        {
            System.err.println("Failed to connect to database " + e.getMessage());
        }
    }
}
