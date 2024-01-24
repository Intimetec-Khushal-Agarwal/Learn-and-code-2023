package Assignment2;

import java.util.Scanner;

public class ValidateInput {
	private static final Scanner inputScanner = new Scanner(System.in);
	private static final int Required_Digits = 4;
	
	public static int userInput(){
		
		int number = 0;
		System.out.println("Enter a 4 digit number: ");
	    number = inputScanner.nextInt();
	    
	    if(countNumberOfDigts(number)!=Required_Digits) {
	    	number = validateAndAdjustNumber(number);
	    }
	    
		return number;
	}
		
	public static int validateAndAdjustNumber(int number){
		
		if(countNumberOfDigts(number)== Required_Digits) {
			return number;
		}
		
		while(countNumberOfDigts(number)>Required_Digits){
		   System.out.println("Enter the correct number: ");
		   number = inputScanner.nextInt();
	    }
		
		while(countNumberOfDigts(number)<Required_Digits){
	    	number *=10;
	    }
		return number;
	}
	

	public static int countNumberOfDigts(int number){
	    int count = 0;
	    
	    if(number == 0){
	        return count;
	    }
	    while(number!=0){
	        count++;
	        number /= 10;
	    }
	    return count;
	}
}
