package user.service;

import java.sql.SQLException;

import connection.service.DatabaseOperationService;
import database.services.UserDefaultData;
import email.service.EmailNotificationService;
import email.service.UserExistsChecker;
import file.service.UserFileService;

public class UserUpdationService {
	
DatabaseOperationService databaseOperationService;
	
	public UserUpdationService(DatabaseOperationService databaseOperationService) {
		this.databaseOperationService = databaseOperationService;
	}
	
	public void updateUser() throws SQLException {
		UserDefaultData user = new UserDefaultData();
		boolean userExists = UserExistsChecker.checkUserExists(databaseOperationService, user.getEmail());
	
		if(userExists) {
			databaseOperationService.updateUser(user);
			UserFileService.saveUserDataToFile(user);
			EmailNotificationService.successNotification(user.getEmail());
		} else {
			System.out.println("No user found for the current email");
			EmailNotificationService.failureNotification(user.getEmail());
		}
	}

}
