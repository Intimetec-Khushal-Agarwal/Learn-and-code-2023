package CarbonFootprintAssignment;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

public class CalculateFootprint {
    
    private Map<String, Double> carbonFootprints = new HashMap<>();
    private EmailService emailService = new EmailService();
    
    public Map<String, Double> calculateCarbonFootprintOfMails() throws MessagingException {
        carbonFootprints.put("InboxMail", Constants.INBOX_MAIL_MULTIPLIER * emailService.getNumberOfMails("Inbox"));
        carbonFootprints.put("SpamMail", Constants.SPAM_MAIL_MULTIPLIER * emailService.getNumberOfMails("[Gmail]/Sent Mail"));
        carbonFootprints.put("SentMail", Constants.SENT_MAIL_MULTIPLIER * emailService.getNumberOfMails("[Gmail]/Spam"));
        
        return carbonFootprints;
    }
}
