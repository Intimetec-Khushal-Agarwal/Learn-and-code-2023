package CarbonFootprintAssignment;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

public class FootprintService {

  private String emailId;
  private String password;
  private String source;
  private Map<String, Double> carbonFootprints = new HashMap<>();
  UserMailService userMailService;

  public FootprintService(String emailId, String password) throws MessagingException {
      this.emailId = emailId;
      this.password = password;
      this.source = getEmailSource(emailId);
      initializeUser();
      calculateCarbonFootprintOfMails();
  }

  private String getEmailSource(String emailId) {
      String source = "Unknown";

      if (emailId.contains("@gmail.com")) {
          source = "Gmail";
      } else if (emailId.contains("@outlook.com")) {
          source = "Outlook";
      } else if (emailId.contains("@yahoo.com")) {
          source = "Yahoo";
      }
      return source;
  }

  private void initializeUser() throws MessagingException {
	  userMailService = new UserMailService(emailId, password);
  }

  private void calculateCarbonFootprintOfMails() throws MessagingException {
      carbonFootprints.put("InboxMail", 4.0 * userMailService.getNumberOfMails("Inbox"));
      carbonFootprints.put("SpamMail", 0.3 * userMailService.getNumberOfMails("[Gmail]/Sent Mail"));
      carbonFootprints.put("SentMail", 4.9 * userMailService.getNumberOfMails("[Gmail]/Spam"));
  }

  public void printCarbonFootprint() {
      System.out.println("Source of mail for email id " + emailId + " is: " + source);
      for (Map.Entry<String, Double> entry : carbonFootprints.entrySet()) {
          System.out.println("Carbon Footprint for " + entry.getKey() + " is: " + entry.getValue());
      }
  }
}
