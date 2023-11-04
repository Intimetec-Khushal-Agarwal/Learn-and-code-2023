package CarbonFootprintAssignment;

import java.util.Scanner;
import javax.mail.MessagingException;
import javax.mail.Store;
import java.util.Properties;
import javax.mail.Session;
public class CarbonFootprint {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		UserMailData UserMailData;
		CalculateCarbonFootprint emailCarbonFootprintCalculator;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Account Details");
		System.out.print("Enter the emailId: ");
		String emailId = scanner.next();
		System.out.print("Enter your email password: ");
		String password = scanner.next();
		int inboxMailsCount = 0;
		int sentMailsCount = 0;
		int spamMailsCount = 0;
		UserMailData = new UserMailData(emailId, password);
		try {
			
			Properties mailProperties = new Properties();
			mailProperties.setProperty("mail.store.protocol", "imaps");
			mailProperties.setProperty("mail.imap.host", "imap.example.com");
			mailProperties.setProperty("mail.imap.port", "993");
			mailProperties.put("mail.imaps.ssl.protocols", "TLSv1.2");
			mailProperties.put("mail.imaps.ssl.ciphersuites", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");

			Session sessionInstance = Session.getInstance(mailProperties, null);
			Store connectionStore = sessionInstance.getStore("imaps");
			connectionStore.connect("imap.gmail.com", UserMailData.getEmailId(), UserMailData.getPassword());
			
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

