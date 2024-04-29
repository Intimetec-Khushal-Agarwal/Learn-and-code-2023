package database.services;
import java.sql.Connection;
import java.sql.DriverManager;

import connection.service.DatabaseConnectionFactory;

public class SqlConnection implements DatabaseConnectionFactory {
	
	public static Connection connection;
	
	public Connection createConnection() {
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

}
