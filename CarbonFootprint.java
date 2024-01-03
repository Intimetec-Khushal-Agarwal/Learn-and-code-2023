package CarbonFootprintAssignment;

import javax.mail.MessagingException;
public class CarbonFootprint {
	public static void main(String[] args) throws MessagingException {
		
		UserInputDetails userData = new UserInputDetails();
		
		FootprintService FootprintCalculator = new FootprintService(userData);
		FootprintCalculator.printCarbonFootprint();
		
	}
}

