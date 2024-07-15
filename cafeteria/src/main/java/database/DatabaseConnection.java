package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import serverconstant.DatabaseConstant;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DatabaseConstant.DB_URL, DatabaseConstant.DB_USER, DatabaseConstant.DB_PASSWORD);
    }
}
