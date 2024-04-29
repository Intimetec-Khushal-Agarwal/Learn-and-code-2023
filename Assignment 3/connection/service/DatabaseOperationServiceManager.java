package connection.service;

import java.sql.Connection;

import database.services.*;
import exception.InvalidUserException;

public class DatabaseOperationServiceManager {
    
    public static Connection connection;
    
    public static DatabaseOperationService getOperationService(DatabaseConnectionFactory databaseConnectionFactory) {
 
        if (databaseConnectionFactory instanceof SqlConnection) {
            connection = databaseConnectionFactory.createConnection();
            return new SqlDatabaseOperationService(connection);
            
        }
        /*else if(databaseOperationObject instanceof MongoDB) {
            return "";
        }*/
        else {
            throw new InvalidUserException("Invalid User Type choice: ");
        }
        
    }
}
