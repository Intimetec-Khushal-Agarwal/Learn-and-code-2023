package CarbonFootprintAssignment;

import java.util.Scanner;
import javax.mail.MessagingException;
import javax.mail.Store;
public class CarbonFootprint {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		UserMailData UserMailData;
		CalculateCarbonFootprint emailCarbonFootprintCalculator;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Account Details");
		System.out.print("Enter the emailId: ");
		String emailId = scanner.next();
		System.out.print("Enter your password: ");
		String password = scanner.next();
		int inboxMailsCount = 0;
		int sentMailsCount = 0;
		int spamMailsCount = 0;
		UserMailData = new UserMailData(emailId, password);
		
		try {
			Store connectionStore = ConnectionAPI.getConnection(UserMailData);
			inboxMailsCount = UserMailData.getNumberOfInboxMails(connectionStore);
			sentMailsCount = UserMailData.getNumberOfSentMails(connectionStore);
			spamMailsCount = UserMailData.getNumberOfSpamMails(connectionStore);
		} 
		catch (MessagingException exception) 
		{
			System.out.println(exception);
			exception.printStackTrace();
		}

		emailCarbonFootprintCalculator = new CalculateCarbonFootprint(emailId, inboxMailsCount,sentMailsCount, spamMailsCount);
		emailCarbonFootprintCalculator.printCarbonFootprint();
		
	}
}

