package FileHandling;
import java.io.*;

import DatabaseServices.UserDefaultData;

public class FileHandling {

	public static void saveFile(UserDefaultData user) {
		String fileName = user.getEmail();
		String directoryPath = "D:\\L&C Directory\\" + fileName;
		String filePath = directoryPath + "\\" + fileName + ".txt";

		try {
			File directory = new File(directoryPath);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			try (PrintWriter writer = new PrintWriter(filePath)) {
				writer.println("EmpCode\tName\tEmail\t\t\tCName\tTechnology\tDesignation");
				writer.println(user.getEmployeeCode() + "\t" + user.getName() + "\t" + user.getEmail() + "\t" + user.getCompanyName() + "\t" + user.getTechnology() + "\t" + user.getDesignation());
				System.out.println("User information stored in file successfully ");
			} 
			catch (IOException e) {
				System.err.println("Error writing to file: " + e.getMessage());
			}
		} 
		catch (SecurityException e) {
			System.err.println("Error creating directory: " + e.getMessage());
		}
	}
}

