package user.service;

public class UserMenu {
	
	public static void viewerMenu() {

		System.out.println("Enter the below choices:\n"
				+ "1.View All User\n"
				+ "2.View User\n"
				+ "3.Exit");
	}
	
	public static void normalUserMenu() {

		System.out.println("Enter the below choices:\n"
				+ "1.View All User\n"
				+ "2.View User\n"
				+ "3.Update User\n"
				+ "4.Exit");
	}
	
	public static void adminMenu() {
		System.out.println("Enter the below choices:\n"
				+ "1.View All Users\n"
				+ "2.View User\n"
				+ "3.Update User\n"
				+ "4.Create User\n"
				+ "5.Exit");
	}
}
