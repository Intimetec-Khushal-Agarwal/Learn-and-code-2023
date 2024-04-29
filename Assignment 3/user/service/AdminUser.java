package user.service;

import java.sql.SQLException;
import connection.service.DatabaseConnectionFactory;
import connection.service.DatabaseOperationService;
import connection.service.DatabaseOperationServiceManager;


public class AdminUser extends NormalUser {
    private DatabaseOperationService databaseOperationService;
	
	public AdminUser(DatabaseConnectionFactory databaseConnectionFactory) {
        super(databaseConnectionFactory);
        this.databaseOperationService = DatabaseOperationServiceManager.getOperationService(databaseConnectionFactory);
    }
	
	public void performUserAction(int actionChoice) throws SQLException {
        switch (actionChoice) {
            case 1:
            case 2:
            case 3:
                super.performUserAction(actionChoice);
                break;
            case 4:
            	UserCreationService userCreationService = new UserCreationService(databaseOperationService);
            	userCreationService.createUser();
                break;
            default:
                System.out.println("Invalid input.");
        }
    }
	
	public void displayMenu() {
		UserMenu.adminMenu();
	}
	
}
