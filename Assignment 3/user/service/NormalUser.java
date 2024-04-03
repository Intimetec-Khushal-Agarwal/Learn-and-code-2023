package user.service;

import java.sql.SQLException;

import database.services.DatabaseOperation;
import database.services.UserDefaultData;
import email.service.SendEmail;
import file.service.FileHandling;

public class NormalUser extends ViewUser {
	public  void userChoice(int choice) throws SQLException {
		ViewUser viewData = new ViewUser();
		boolean isUserExists;
		DatabaseOperation operation = new DatabaseOperation();
		
			if(choice ==1 || choice ==2) {
				viewData.userChoice(choice);
			} else if (choice == 3) {
				UserDefaultData user = new UserDefaultData();
				isUserExists = operation.checkUserAlreadyExists(user.getEmail());
				if(isUserExists == true) {
					operation.updateUser(user);
					FileHandling.saveFile(user);
					SendEmail.successNotification(user.getEmail());
				} else {
					System.out.println("No user found for the current email");
					SendEmail.failureNotification(user.getEmail());
				}
		}
	}
	
	public void showMenu() {
		System.out.println("Enter the below choices:\n1.View All User\n2.View User\n3.Update User\n4.Exit");
	}
	
	public void processInput(int choice) throws SQLException {
		if(choice>0 && choice<4) {
			userChoice(choice);
		} else {
			System.out.println("Invalid choice...");
		}
	}
}
