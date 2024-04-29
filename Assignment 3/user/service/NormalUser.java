package user.service;

import java.sql.SQLException;
import connection.service.DatabaseConnectionFactory;
import connection.service.DatabaseOperationService;
import connection.service.DatabaseOperationServiceManager;

public class NormalUser extends ViewUser {
	
	DatabaseOperationService databaseOperationService;
	
	public NormalUser(DatabaseConnectionFactory databaseObjectType) {
		super(databaseObjectType);
		this.databaseOperationService = DatabaseOperationServiceManager.getOperationService(databaseObjectType);
	}

	public  void performUserAction(int choice) throws SQLException {
		
		switch(choice) {
			case 1:
			case 2:
				super.performUserAction(choice);
				break;
			case 3:
				UserUpdationService userUpdationService = new UserUpdationService(databaseOperationService);
				userUpdationService.updateUser();
				break;
			default:
				System.out.println("Invalid Choice");	
		}
	}
	
	public void displayMenu() {
		UserMenu.normalUserMenu();
	}
	
}
