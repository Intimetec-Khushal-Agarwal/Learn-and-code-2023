package CarbonFootprintAssignment;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class ConnectionService {
	UserInputDetails userEmailData = new UserInputDetails();
	
	private static ConnectionService instance;
	
	public static ConnectionService getInstance() {
		if(instance == null) {
			instance = new ConnectionService();
		}
		return instance;
	}
	
	public Store getConnection() throws MessagingException {
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.store.protocol", "imaps");
		mailProperties.setProperty("mail.imap.host", "imap.example.com");
		mailProperties.setProperty("mail.imap.port", "993");
		mailProperties.setProperty("mail.imaps.ssl.protocols", "TLSv1.2");
		mailProperties.setProperty("mail.imaps.ssl.ciphersuites", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
		Session sessionInstance = Session.getInstance(mailProperties, null);
		Store connection = sessionInstance.getStore("imaps");
		connection.connect("imap.gmail.com", userEmailData.getEmailId(),userEmailData.getAppPassword());
		
		return connection;
	}

}
