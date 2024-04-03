package user.service;

import java.sql.SQLException;
import java.util.Scanner;
import exception.InvalidUserException;
@SuppressWarnings("resource")

public class UserService {
	public static void main(String args[]) throws SQLException {
		
		int choice = 0;
		Scanner input = new Scanner(System.in);
		System.out.println("Enter your user choice:\n1.Admin\n2.Normal\n3.Viewer\n ");
		choice = input.nextInt();
		
		try {
			ViewUser service = getService(choice);
			service.showMenu();
			int operationChoice = input.nextInt();
			service.processInput(operationChoice);
		} 
		catch(InvalidUserException error) {
			System.out.println("Something went wrong: "+error.getLocalizedMessage());
		}
	}
	
	private static ViewUser getService(int choice) {
		ViewUser service = null; 
		if(choice == 1) {
			service = new AdminUser();
		} else if(choice == 2) {
			service = new NormalUser();
		} else if(choice == 3) {
			service = new ViewUser();
		} else {
			throw new InvalidUserException("Invalid User Type choice: "+choice);
		}
		return service;
	}
}
	