package Users;

import java.sql.SQLException;

import DatabaseServices.DatabaseOperation;
import DatabaseServices.UserDefaultData;

public class ViewUser {

	public static void userChoice(int choice) throws SQLException {
		DatabaseOperation operations = new DatabaseOperation();
		if (choice == 1) {
			operations.viewAllUsers();

		}
		else if(choice == 2) {
			UserDefaultData user = new UserDefaultData();
			operations.viewUser(user.getEmail());
		}
	}
}
