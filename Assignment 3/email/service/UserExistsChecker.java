package email.service;

import java.sql.SQLException;

import connection.service.DatabaseOperationService;

public class UserExistsChecker {
    
	public static boolean checkUserExists(DatabaseOperationService databaseOperationService, String email) throws SQLException {
        return databaseOperationService.checkUserAlreadyExists(email);
    }
}
