package CarbonFootprintAssignment;

import javax.mail.MessagingException;
import javax.mail.Store;

public class UserMailService {
	private Store connectionStore;
	UserInputDetails userData;
	public UserMailService() throws MessagingException {
		final ConnectionService connectionAPI = ConnectionService.getInstance();
		this.connectionStore = connectionAPI.getConnection(userData);
	}

	public int getNumberOfMails(String FolderName) throws MessagingException {
		int mailCount = connectionStore.getFolder(FolderName).getMessageCount();
		return mailCount;
	}
}
