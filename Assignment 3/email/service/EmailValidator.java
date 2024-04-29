package email.service;

import java.util.Scanner;

@SuppressWarnings("resource")
public class EmailValidator {

	public static String validateEmail(String email) {
		Scanner input = new Scanner(System.in);
		String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		boolean result = email.matches(regex);
		
		while (result == false) {
			System.out.println("Enter a valid email address: ");
			email = input.next();
			break;
		}
		return email;
	}
}
