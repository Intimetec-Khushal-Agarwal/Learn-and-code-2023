package CarbonFootprintAssignment;

import java.util.Scanner;

public class UserInputDetails {

    private static String emailId;
    private static String appPassword;
    private Scanner scanner;

    public UserInputDetails() {
        scanner = new Scanner(System.in);
    }

    public void getUserDetails() {
        System.out.println("Enter Account Details");
        System.out.print("Enter the emailId: ");
        emailId = scanner.next();
        System.out.print("Enter your 16 digits App Password: ");
        appPassword = scanner.next();
    }

    public String getAppPassword() {
        return appPassword;
    }

    public String getEmailId() {
        return emailId;
    }

    public void closeScanner() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
