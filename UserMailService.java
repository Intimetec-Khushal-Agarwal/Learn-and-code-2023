package CarbonFootprintAssignment;

import javax.mail.MessagingException;
import javax.mail.Store;

public class UserMailService {
	private Store connectionStore;

	public UserMailService(String emailId, String appPassword) throws MessagingException {
		final ConnectionService connectionAPI = ConnectionService.getInstance();
		this.connectionStore = connectionAPI.getConnection(emailId,appPassword);
	}

	public int getNumberOfMails(String FolderName) throws MessagingException {
		int mailCount = connectionStore.getFolder(FolderName).getMessageCount();
		return mailCount;
	}
}
