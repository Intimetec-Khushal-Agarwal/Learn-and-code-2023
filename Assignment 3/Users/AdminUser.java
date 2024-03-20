package Users;

import java.sql.SQLException;
import java.util.Scanner;
import DatabaseServices.DatabaseOperation;
import DatabaseServices.UserDefaultData;
import EmailService.SendEmail;
import FileHandling.FileHandling;

public class AdminUser extends ViewUser {
	@SuppressWarnings("resource")
	public static void userChoice(int choice) throws SQLException {	
		DatabaseOperation operation = new DatabaseOperation();
		boolean isUserExists;

		if(choice == 1|| choice==2) {
			ViewUser.userChoice(choice);
		}
		
		else if (choice == 3) {
			UserDefaultData user = new UserDefaultData();
			isUserExists = operation.checkIfUserAlreadyExists(user.getEmail());

			if(isUserExists == true) {
				operation.updateUser(user);
				FileHandling.saveFile(user);
				SendEmail.successNotification(user.getEmail());
			}

			else {
				System.out.println("No user found for the current email");
				SendEmail.failureNotification(user.getEmail());
			}
		}

		else if(choice == 4) {
			Scanner input = new Scanner(System.in);
			System.out.println("Enter user details");
			UserDefaultData user = new UserDefaultData();
			System.out.println("Enter your name");
			user.setName(input.nextLine());
			isUserExists = operation.checkIfUserAlreadyExists(user.getEmail());

			if(isUserExists == true) {
				System.out.println("User already exists in DB");
				SendEmail.failureNotification(user.getEmail());
			}
			else {
				operation.insertUser(user);
				FileHandling.saveFile(user);
				SendEmail.successNotification(user.getEmail());
			}
		}
	}
}
