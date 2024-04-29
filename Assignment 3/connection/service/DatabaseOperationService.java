package connection.service;

import java.sql.SQLException;

import database.services.UserDefaultData;

public interface DatabaseOperationService {
	public void insertUser(UserDefaultData user) throws SQLException;
	public void updateUser(UserDefaultData user) throws SQLException;
	public void viewUser(String email) throws SQLException;
	public void viewAllUsers() throws SQLException;
	public boolean checkUserAlreadyExists(String email)throws SQLException;
}
