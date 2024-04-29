package database.services;

import java.util.Scanner;

import email.service.EmailValidator;

public class UserDataInput {
	
	public static Scanner input = new Scanner(System.in);

    public static String getUserEmail() {
        System.out.println("Enter your Email:");
        String email = EmailValidator.validateEmail(input.next());
        return email;
    }
    
    public static String getUserName() {
    	System.out.println("Enter your name: ");
    	String name = input.next();
    	return name;
    }
}
