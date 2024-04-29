package user.service;

import java.sql.SQLException;
import connection.service.DatabaseOperationService;
import database.services.UserDefaultData;
import email.service.EmailNotificationService;
import email.service.UserExistsChecker;
import file.service.UserFileService;

public class UserCreationService {
	DatabaseOperationService databaseOperationService;
	
	public UserCreationService(DatabaseOperationService databaseOperationService) {
		this.databaseOperationService = databaseOperationService;
	}

	public void createUser() throws SQLException {
		
		UserDefaultData user = new UserDefaultData();
        boolean userExists = UserExistsChecker.checkUserExists(databaseOperationService, user.getEmail());

        if (userExists) {
            System.out.println("User already exists in the database.");
            EmailNotificationService.failureNotification(user.getEmail());
        } else {
            databaseOperationService.insertUser(user);
            UserFileService.saveUserDataToFile(user);
            EmailNotificationService.successNotification(user.getEmail());
        }
    }
}
