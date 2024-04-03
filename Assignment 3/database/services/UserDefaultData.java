package database.services;

import java.util.Scanner;

import email.service.EmailValidation;

@SuppressWarnings("resource")
public class UserDefaultData {

	private String name;
	private String email;

	public UserDefaultData() {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter you Email");
		this.email = EmailValidation.checkEmail(input.next());
	}

	public String getCompanyName() {
		return "ITT";
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public int getEmployeeCode() {
		return (int) (1234 + (Math.random()*1000+ 3000));
	}

	public String getDesignation() {
		return "JSE";
	}

	public String getTechnology() {
		return "Salesforce";
	}
}
