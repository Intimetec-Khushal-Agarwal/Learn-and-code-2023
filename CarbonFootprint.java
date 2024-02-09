package CarbonFootprintAssignment;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
public class CarbonFootprint {
	public static void main(String[] args) throws MessagingException {

		UserInputDetails userEmailData = new UserInputDetails();
		EmailService storeConnection = new EmailService();
		CalculateFootprint calculateFootprint = new CalculateFootprint();
		Map<String, Double> carbonFootprints = new HashMap<>();
		
		userEmailData.getUserDetails();
		storeConnection.initializeConnection();
	        carbonFootprints = calculateFootprint.calculateCarbonFootprintOfMails();
		PrintCarbonFootprint.print(carbonFootprints);
		
	}
}

