package CarbonFootprintAssignment;

import javax.mail.MessagingException;
import javax.mail.Store;

public class EmailService {
	
	private static Store connection;

	public void initializeConnection() throws MessagingException {
		final ConnectionService connectionAPI = ConnectionService.getInstance();
		connection = connectionAPI.getConnection();
	}
	
	public int getNumberOfMails(String FolderName) throws MessagingException {
		int mailCount = connection.getFolder(FolderName).getMessageCount();
		return mailCount;
	}
}
