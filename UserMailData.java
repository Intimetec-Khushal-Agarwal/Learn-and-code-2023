package CarbonFootprintAssignment;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

public class UserMailData {
	private String emailId;
	private String password;

	public UserMailData(String emailId, String appPassword) {
		this.emailId = emailId;
		this.password = appPassword;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getPassword() {
		return password;
	}

	public int getNumberOfInboxMails(Store connection) throws MessagingException {
		Folder inbox = connection.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);
		int numberOfInboxEmails = inbox.getMessageCount();
		inbox.close(false);
		return numberOfInboxEmails;
	}
	
	public int getNumberOfSentMails(Store connection) throws MessagingException {
		Folder sent = connection.getFolder("[Gmail]/Sent Mail");
		sent.open(Folder.READ_ONLY);
		int numberOfSentEmails = sent.getMessageCount();
		sent.close(false);
		return numberOfSentEmails;
	}

	public int getNumberOfSpamMails(Store connection) throws MessagingException {
		Folder spam = connection.getFolder("[Gmail]/Spam");
		spam.open(Folder.READ_ONLY);
		int numberOfSpamEmails = spam.getMessageCount();
		spam.close(false);
		return numberOfSpamEmails;
	}
}
