package CarbonFootprintAssignment;

import java.util.HashMap;
import java.util.Map;

public class CalculateCarbonFootprint {

    private String emailId;
    private String source;
    private int inboxMailsCount;
    private int sentMailsCount;
    private int spamMailsCount;
    private Map<String, Double> carbonFootprints = new HashMap<>();

    public CalculateCarbonFootprint(String emailId, int inboxMails, int sentMails, int spamMails) {
        this.emailId = emailId;
        this.inboxMailsCount = inboxMails;
        this.sentMailsCount = sentMails;
        this.spamMailsCount = spamMails;
        this.source = getEmailSource(emailId);
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


    private void calculateCarbonFootprintOfMails() {
        carbonFootprints.put("InboxMail", 4.0 * inboxMailsCount);
        carbonFootprints.put("SpamMail", 0.3 * spamMailsCount);
        carbonFootprints.put("SentMail", 4.9 * sentMailsCount);
    }

    public void printCarbonFootprint() {
        System.out.println("Source of mail for email id " + emailId + " is: " + source);
        for (Map.Entry<String, Double> entry : carbonFootprints.entrySet()) {
            System.out.println("Carbon Footprint for " + entry.getKey() + " is: " + entry.getValue());
        }
    }
}
