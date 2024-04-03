package user.service;

import java.sql.SQLException;

import database.services.DatabaseOperation;
import database.services.UserDefaultData;

public class ViewUser {

	public void userChoice(int choice) throws SQLException {
			DatabaseOperation operations = new DatabaseOperation();
			if (choice == 1) {
				operations.viewAllUsers();
			} else if(choice == 2) {
				UserDefaultData user = new UserDefaultData();
				operations.viewUser(user.getEmail());
			}	
	}
	
	public void showMenu() {
		System.out.println("Enter the below choices:\n1.View All User\n2.View User\n3.Exit");
	}
	
	public void processInput(int choice) throws SQLException {
		if(choice>0 && choice<3) {
			userChoice(choice);
		}
		else {
			System.out.println("Invalid choice...");
		}
	}
}
