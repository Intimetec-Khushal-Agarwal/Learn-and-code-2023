package CarbonFootprintAssignment;

import java.util.Scanner;

public class UserInputDetails {

	private String emailId;
	private String appPassword;
	

	@SuppressWarnings("resource")
	public UserInputDetails() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Account Details");
		System.out.print("Enter the emailId: ");
		this.emailId = scanner.next();
		System.out.print("Enter your password: ");
		this.appPassword = scanner.next();
	}
	public String getAppPassword() {
		return appPassword;
	}

	public String getEmailId() {
		return emailId;
	}
}
