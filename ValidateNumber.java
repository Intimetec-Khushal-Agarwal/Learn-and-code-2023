package Assignment2;

import java.util.Scanner;

public class ValidateNumber {

    private static final int REQUIRED_DIGITS = 4;
    private static final Scanner inputScanner = new Scanner(System.in);
    
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
