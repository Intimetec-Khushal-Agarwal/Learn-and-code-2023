package EmailService;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SendEmail {
	public static final String senderEmail = "shrijiskit@gmail.com";
	public static final String senderPassword = "vkoatomvpsyxmeti";

	public static Session getSession() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.secure", "true");
		props.put("mail.smtp.servie", "gmail");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(senderEmail, senderPassword);
			}
		});
		return session;
	}

	public static void successNotification(String recipientEmail) {
		String subject = "Success Notification";
		String body = "This is to inform that the user has been created or updated in the database successfully";
		sendMessage(subject,body,recipientEmail);
	}

	public static void failureNotification(String recipientEmail) {
		String subject = "Error Notification";
		String body = "This is to inform that the user already exist in the database";
		sendMessage(subject,body,recipientEmail);
	}

	public static void sendMessage(String subject,String body,String recipientEmail ) {
		try {
			MimeMessage message = new MimeMessage(getSession());
			message.setFrom(new InternetAddress(senderEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);

			System.out.println("Email sent successfully!");
		} 
		catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}