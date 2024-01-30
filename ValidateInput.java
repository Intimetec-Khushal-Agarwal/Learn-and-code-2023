package Assignment2;

import java.util.Scanner;

public class ValidateInput {
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final int REQUIRED_DIGITS = 4;

    public static int userInput() {
        int number = 0;
        System.out.println("Enter a 4-digit number: ");
        number = inputScanner.nextInt();

        if (countNumberOfDigits(number) != REQUIRED_DIGITS) {
            number = validateAndAdjustNumber(number);
        }

        return number;
    }

    public static int validateAndAdjustNumber(int number) {
        if (countNumberOfDigits(number) == REQUIRED_DIGITS) {
            return number;
        }

        while (countNumberOfDigits(number) > REQUIRED_DIGITS) {
            System.out.println("Enter the correct number: ");
            number = inputScanner.nextInt();
        }

        while (countNumberOfDigits(number) < REQUIRED_DIGITS) {
            number *= 10;
        }
        return number;
    }

    public static int countNumberOfDigits(int number) {
        int count = 0;

        if (number == 0) {
            return count;
        }
        while (number != 0) {
            count++;
            number /= 10;
        }
        return count;
    }
}
