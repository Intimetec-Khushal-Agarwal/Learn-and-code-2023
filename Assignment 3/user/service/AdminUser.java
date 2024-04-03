package user.service;

import java.sql.SQLException;
import java.util.Scanner;
import database.services.DatabaseOperation;
import database.services.UserDefaultData;
import email.service.SendEmail;
import file.service.FileHandling;

public class AdminUser extends NormalUser {
	@SuppressWarnings("resource")
	public  void userChoice(int choice) throws SQLException {	
		DatabaseOperation operation = new DatabaseOperation();
		NormalUser viewData = new NormalUser();
		boolean isUserExists;

			if(choice == 1|| choice==2 || choice ==3) {
				viewData.userChoice(choice);
			} else if(choice == 4) {
				Scanner input = new Scanner(System.in);
				System.out.println("Enter user details");
				UserDefaultData user = new UserDefaultData();
				System.out.println("Enter your name");
				user.setName(input.nextLine());
				isUserExists = operation.checkUserAlreadyExists(user.getEmail());
				
				if(isUserExists == true) {
					System.out.println("User already exists in DB");
					SendEmail.failureNotification(user.getEmail());
				} else {
					operation.insertUser(user);
					FileHandling.saveFile(user);
					SendEmail.successNotification(user.getEmail());
				}
			}
		}
	
	public void showMenu() {
		System.out.println("Enter the below choices:\n1.View All Users\n2.View User\n3.Update User\n4.Create User\n5.Exit");
	}
	
	public void processInput(int choice) throws SQLException {
		if(choice>0 && choice<5) {
			userChoice(choice);
		} else {
			System.out.println("Invalid choice...");
		}
	}
}
