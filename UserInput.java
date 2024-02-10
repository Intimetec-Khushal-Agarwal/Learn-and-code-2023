package Assignment2;

import java.util.Scanner;

public class UserInput {
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final int REQUIRED_DIGITS = 4;

    public static int getUserInput() {
        int number = 0;
        System.out.println("Enter a 4-digit number: ");
        number = inputScanner.nextInt();

        if (ValidateNumber.countNumberOfDigits(number) != REQUIRED_DIGITS) {
            number = ValidateNumber.validateAndAdjustNumber(number);
        }

        return number;
    }
}
