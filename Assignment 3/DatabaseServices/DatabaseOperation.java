package DatabaseServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseOperation {
	
	private static Connection connection;
	
	static {
		connection = establishConnection();
	}
	
	private static Connection establishConnection(){
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/khushal","root","Root");
		}
		catch (Exception exception) {
			System.out.println(exception);
		}
		return connection;
	}

	public void insertUser(UserDefaultData user) throws SQLException {
		String query = "INSERT INTO users (EmployeeCode, Name, Email, ComapanyName, Designation, Technology) VALUES (?, ?, ?, ?, ?, ?)";

		PreparedStatement pstmt = connection.prepareStatement(query);
		pstmt.setInt(1, user.getEmployeeCode());
		pstmt.setString(2, user.getName());
		pstmt.setString(3, user.getEmail());
		pstmt.setString(4, user.getCompanyName());
		pstmt.setString(5, user.getDesignation());
		pstmt.setString(6, user.getTechnology());
		pstmt.execute();
		System.out.println("User Inserted successfully in the database !....");
	}

	public void updateUser(UserDefaultData user) throws SQLException {
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		System.out.println("Enter name to update");
		user.setName(input.nextLine());
		String sql = "update Users set Name='"+user.getName()+"' where Email = '"+user.getEmail()+"'"; 

		PreparedStatement pstmt = connection.prepareStatement(sql);
		pstmt.execute();
		System.out.println("User updated successfully !....");

	}

	public void viewUser(String email) throws SQLException {
		String sql = "Select * from Users where email = '"+email+"'";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		ResultSet resultSet = pstmt.executeQuery();
		if(resultSet.next()) {
			int EmployeeCode = resultSet.getInt("EmployeeCode");
			String Name = resultSet.getString("Name");
			String Designation = resultSet.getString("Designation");
			String CompanyName = resultSet.getString("ComapanyName");
			String Technology = resultSet.getString("Technology");

			System.out.println("EmpCode\tName\t\tEmail\t\t\tCName\tTechnology\tDesignation");
			System.out.println(EmployeeCode + "\t" + Name + "\t" + email + "\t" + CompanyName + "\t" + Technology + "\t" + Designation);
		}
	}

	public void viewAllUsers() throws SQLException {
		String sql = "Select * from Users";

		PreparedStatement pstmt = connection.prepareStatement(sql);
		ResultSet resultSet = pstmt.executeQuery();
		System.out.println("EmpCode\tName\t\tEmail\t\t\tCName\tTechnology\tDesignation");

		while(resultSet.next()) {

			int EmployeeCode = resultSet.getInt("EmployeeCode");
			String Name = resultSet.getString("Name");
			String Email = resultSet.getString("Email");
			String Designation = resultSet.getString("Designation");
			String ComapanyName = resultSet.getString("ComapanyName");
			String Technology = resultSet.getString("Technology");

			System.out.println(EmployeeCode + "\t" + Name + "\t" + Email + "\t" + ComapanyName + "\t" + Technology + "\t" + Designation);
		}
	}
	
	public boolean checkIfUserAlreadyExists(String mail) throws SQLException {

		Statement statement = connection.createStatement();
		ResultSet resultSet =  statement.executeQuery("select * from users where email = '"+mail+"'");
		boolean isUserExists = resultSet.next();
		return isUserExists;

	}
}
