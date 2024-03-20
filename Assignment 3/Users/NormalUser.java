package Users;

import java.sql.SQLException;

public class NormalUser extends AdminUser {
	public static void userChoice(int choice) throws SQLException {
		AdminUser.userChoice(choice);
	}
}
