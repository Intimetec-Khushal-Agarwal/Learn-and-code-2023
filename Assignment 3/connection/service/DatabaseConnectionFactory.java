package connection.service;

import java.sql.Connection;

public interface DatabaseConnectionFactory {
    
    Connection createConnection();
}
