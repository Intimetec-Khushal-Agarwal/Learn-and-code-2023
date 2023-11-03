package CarbonFootprintAssignment;

public class CalculateCarbonFootprint {

	private String emailId;
	private String source;
	private int inboxMailsCount;
	private int sentMailsCount;
	private int spamMailsCount;
	private static double carbonFootprintInboxMail = 4.0;
	private static double carbonFootprintSpamMail = 0.3;
	private static double carbonFootprintsentMail = 4.9;

	public CalculateCarbonFootprint(String emailId, int inboxMails, int sentMails, int spamMails) {
		this.emailId = emailId;
		this.inboxMailsCount = inboxMails;
		this.sentMailsCount = sentMails;
		this.spamMailsCount = spamMails;
		this.source = getEmailSource(emailId);
		getCarbonFootprintOfMails(emailId, inboxMailsCount, sentMailsCount, spamMailsCount);
	}

	private static String getEmailSource(String emailId) {
		if (emailId.contains("@gmail.com")) {
			return "Gmail";
		} else if (emailId.contains("@outlook.com")) {
			return "Outlook";
		} else if (emailId.contains("@yahoo.com")) {
			return "Yahoo";
		}

		return "";
	}
	
	private static void getCarbonFootprintOfMails(String emailId, int inboxMails, int sentMails,
			int spamMails) {
		
		carbonFootprintInboxMail = carbonFootprintInboxMail*inboxMails;
		carbonFootprintSpamMail = carbonFootprintSpamMail*spamMails;
		carbonFootprintSpamMail = carbonFootprintsentMail*sentMails;
	}

	

	public void printCarbonFootprint() {
		System.out.println("Source of mail for email id " + emailId + "is : " + source);
		System.out.println("Carbon Footprint for Inbox Mail is: "+ carbonFootprintInboxMail);
		System.out.println("Carbon Footprint for Spam Mail is: "+ carbonFootprintSpamMail);
		System.out.println("Carbon Footprint for Sent Mail is: "+ carbonFootprintSpamMail);
		
	}
}