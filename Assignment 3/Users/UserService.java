package Users;

import java.sql.SQLException;
import java.util.Scanner;
public class UserService {
	public static void main(String args[]) throws SQLException {
		int choice = 0;
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		int databaseOperation=0;

		do {
			System.out.println("Enter your user choice:\n1.Admin\n2.Normal\n3.Viewer\n4.Exit ");
			choice = input.nextInt();
			switch(choice){
			case 1:
				System.out.println("Enter the below choices:\n1.View All Users\n2.View User\n3.Update User\n4.Create User\n5.Exit");
				databaseOperation = input.nextInt();

				if(databaseOperation==5) {
					System.out.println("Program has been closed successfully");
					return;
				}

				while(databaseOperation <=0 && databaseOperation >4) {
					System.out.println("Invalid choice. Try again");
					databaseOperation = input.nextInt();
				}

				AdminUser.userChoice(databaseOperation);
				break;
			case 2:
				System.out.println("Enter the below choices:\n1.View All User\n2.View User\n3.Update User\n4.Exit");
				databaseOperation = input.nextInt();

				if(databaseOperation==4) {
					System.out.println("Program has been closed successfully");
					return;
				}

				while(databaseOperation <=0 && databaseOperation >3) {
					System.out.println("Invalid choice. Try again");
					databaseOperation = input.nextInt();
				}

				NormalUser.userChoice(databaseOperation);
				break;
			case 3 :
				System.out.println("Enter the below choices:\n1.View All User\n2.View User\n3.Exit");
				databaseOperation = input.nextInt();

				if(databaseOperation==3) {
					System.out.println("Program has been closed successfully");
					return;
				}

				while(databaseOperation <=0 && databaseOperation >2) {
					System.out.println("Invalid choice. Try again");
					databaseOperation = input.nextInt();
				}

				ViewUser.userChoice(databaseOperation);
				break;

			case 4 :
				System.out.println("Execution has been closed successfully!....");
				break;
			}
		}
		while(choice!=4);
	}
}
