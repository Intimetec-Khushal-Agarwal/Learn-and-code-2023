package database.services;

public class UserDefaultData {

	private String name;
	private String email;
	
	public UserDefaultData() {
		this.email = UserDataInput.getUserEmail();
		this.name = UserDataInput.getUserName();
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
