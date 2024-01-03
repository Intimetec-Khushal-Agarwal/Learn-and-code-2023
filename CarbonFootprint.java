package CarbonFootprintAssignment;

import java.util.Scanner;

import javax.mail.MessagingException;
public class CarbonFootprint {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws MessagingException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Account Details");
		System.out.print("Enter the emailId: ");
		String emailId = scanner.next();
		System.out.print("Enter your password: ");
		String password = scanner.next();
		
		FootprintService FootprintCalculator = new FootprintService(emailId, password);
		FootprintCalculator.printCarbonFootprint();
		
	}
}

