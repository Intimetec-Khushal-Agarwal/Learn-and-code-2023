package CarbonFootprintAssignment;

import javax.mail.*;

public class UserMailData {

	private String emailId;
	private String password;

//  constructor
	public UserMailData(String emailId, String appPassword) {
		this.emailId = emailId;
		this.password = appPassword;
	}

//  get Method
	public String getEmailId() {
		return emailId;
	}

	public String getPassword() {
		return password;
	}

//   get the number of inbox emails
	public int getNumberOfInboxMails(Store connection) throws MessagingException {
		Folder inbox = connection.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);
		int numberOfInboxEmails = inbox.getMessageCount();
		inbox.close(false);
		return numberOfInboxEmails;
	}
	
//  get the number of sent mails
	public int getNumberOfSentMails(Store connection) throws MessagingException {
		Folder sent = connection.getFolder("[Gmail]/Sent Mail");
		sent.open(Folder.READ_ONLY);
		int numberOfSentEmails = sent.getMessageCount();
		sent.close(false);
		return numberOfSentEmails;
	}

// get the number of spam mails
	public int getNumberOfSpamMails(Store connection) throws MessagingException {
		Folder spam = connection.getFolder("[Gmail]/Spam");
		spam.open(Folder.READ_ONLY);
		int numberOfSpamEmails = spam.getMessageCount();
		spam.close(false);
		return numberOfSpamEmails;
	}

	
}
