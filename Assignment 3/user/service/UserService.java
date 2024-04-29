package user.service;

import java.sql.SQLException;
import java.util.Scanner;
import connection.service.DatabaseConnectionFactory;
import connection.service.DatabaseConnectionFactoryManager;
import exception.InvalidUserException;

@SuppressWarnings("resource")
public class UserService {
    public static void main(String args[]) throws SQLException {

        int userChoice = 0;
        String databaseType = "sql";
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your user choice:\n1.Admin\n2.Normal\n3.Viewer\n ");
        userChoice = input.nextInt();
        DatabaseConnectionFactory databaseConnectionFactory = DatabaseConnectionFactoryManager.getDatabaseConnectionFactory(databaseType);

        try {
            ViewUser user = null;
            switch (userChoice) {
                case 1:
                    user = new AdminUser(databaseConnectionFactory);
                    break;
                case 2:
                    user = new NormalUser(databaseConnectionFactory);
                    break;
                case 3:
                    user = new ViewUser(databaseConnectionFactory);
                    break;
                default:
                    throw new InvalidUserException("Invalid User Type choice: " + userChoice);
            }
            
            user.displayMenu();
            int operationChoice = input.nextInt();
            user.performUserAction(operationChoice);
            
        } catch (InvalidUserException error) {
            System.out.println("Something went wrong: " + error.getLocalizedMessage());
        }
    }
}
