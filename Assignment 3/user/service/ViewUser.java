package user.service;

import java.sql.SQLException;

import connection.service.DatabaseConnectionFactory;
import connection.service.DatabaseOperationService;
import connection.service.DatabaseOperationServiceManager;
import database.services.UserDataInput;

public class ViewUser {
	DatabaseOperationService dbOperationService;
	
	public ViewUser(DatabaseConnectionFactory databaseObjectType) {
		this.dbOperationService = DatabaseOperationServiceManager.getOperationService(databaseObjectType);
	}

	public void performUserAction(int choice) throws SQLException {
		
		switch(choice) {
			case 1:
				dbOperationService.viewAllUsers();
				break;
			case 2:
				dbOperationService.viewUser(UserDataInput.getUserEmail());
				break;
		}
	}
	
	public void displayMenu() {
		UserMenu.viewerMenu();
	}

}
