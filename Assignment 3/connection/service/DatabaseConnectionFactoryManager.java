package connection.service;

import database.services.*;

public class DatabaseConnectionFactoryManager {
    
    public static DatabaseConnectionFactory getDatabaseConnectionFactory(String databaseType) {
        switch (databaseType) {
            case "sql":
                return new SqlConnection();
            case "PostgreSQL":
                //return new PostgreSQLConnector();
            case "MongoDB":
                //return new MongoDBConnector();    
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}
