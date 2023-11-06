package CarbonFootprintAssignment;

import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class ConnectionAPI {
	
	public static Store getConnection(UserMailData userDetail) throws MessagingException {
	Properties mailProperties = new Properties();
	mailProperties.setProperty("mail.store.protocol", "imaps");
	mailProperties.setProperty("mail.imap.host", "imap.example.com");
	mailProperties.setProperty("mail.imap.port", "993");
	mailProperties.put("mail.imaps.ssl.protocols", "TLSv1.2");
	mailProperties.put("mail.imaps.ssl.ciphersuites", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
	Session sessionInstance = Session.getInstance(mailProperties, null);
	Store connectionStore = sessionInstance.getStore("imaps");
	connectionStore.connect("imap.gmail.com", userDetail.getEmailId(), userDetail.getPassword());
	
	return connectionStore;
	
	}

}
