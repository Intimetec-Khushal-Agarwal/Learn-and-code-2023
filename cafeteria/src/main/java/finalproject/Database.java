package finalproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/cafeteria";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void createTables() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String createRolesTable = "CREATE TABLE IF NOT EXISTS roles ("
                    + "role_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "role_name VARCHAR(100) NOT NULL"
                    + ")";
            stmt.execute(createRolesTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "employee_id INT NOT NULL,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "role_id INT,"
                    + "FOREIGN KEY (role_id) REFERENCES roles(role_id)"
                    + ")";
            stmt.execute(createUsersTable);

            String createMenuTypesTable = "CREATE TABLE IF NOT EXISTS menu_types ("
                    + "meal_type_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "meal_type VARCHAR(100) NOT NULL"
                    + ")";
            stmt.execute(createMenuTypesTable);

            String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items ("
                    + "menu_item_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "price DECIMAL(10, 2) NOT NULL,"
                    + "availability_status VARCHAR(50) NOT NULL,"
                    + "rating int,"
                    + "sentiments text,"
                    + "meal_type_id INT,"
                    + "FOREIGN KEY (meal_type_id) REFERENCES menu_types(meal_type_id)"
                    + ")";
            stmt.execute(createMenuItemsTable);

            String createFeedbacksTable = "CREATE TABLE IF NOT EXISTS feedbacks ("
                    + "feedback_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "menu_item_id INT,"
                    + "user_id INT,"
                    + "comment TEXT,"
                    + "rating INT CHECK (rating BETWEEN 1 AND 5),"
                    + "feedback_date DATE NOT NULL,"
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id)"
                    + ")";
            stmt.execute(createFeedbacksTable);

            String createRolloutMenuItemsTable = "CREATE TABLE IF NOT EXISTS rollout_menu_items ("
                    + "rollout_item_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "menu_item_id INT,"
                    + "name VARCHAR(100),"
                    + "price INT,"
                    + "rating INT,"
                    + "rollout_date DATE NOT NULL,"
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id),"
                    + "meal_type_id INT,"
                    + "FOREIGN KEY (meal_type_id) REFERENCES menu_types(meal_type_id),"
                    + "sentiments TEXT"
                    + ")";
            stmt.execute(createRolloutMenuItemsTable);

            String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "notification_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "message TEXT NOT NULL,"
                    + "notification_date DATE NOT NULL"
                    + ")";
            stmt.execute(createNotificationsTable);

            String createRecommendationsTable = "CREATE TABLE IF NOT EXISTS recommendations ("
                    + "recommendation_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "menu_item_id INT,"
                    + "chef_id INT,"
                    + "recommendation_date DATE NOT NULL,"
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id),"
                    + "FOREIGN KEY (chef_id) REFERENCES users(user_id)"
                    + ")";
            stmt.execute(createRecommendationsTable);

            String createReportsTable = "CREATE TABLE IF NOT EXISTS reports ("
                    + "report_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "menu_item_id INT,"
                    + "report_date DATE NOT NULL,"
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id)"
                    + ")";
            stmt.execute(createReportsTable);

            String createUserVoteDetails = "CREATE TABLE IF NOT EXISTS user_vote ("
                    + "user_vote_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "vote_date DATE NOT NULL"
                    + ")";
            stmt.execute(createUserVoteDetails);

            String createSelectedItemTable = "CREATE TABLE IF NOT EXISTS selected_item ("
                    + "selected_item_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT NOT NULL, "
                    + "Name VARCHAR(255) NOT NULL, "
                    + "Rating INT NOT NULL, "
                    + "Comment TEXT, "
                    + "Date DATE NOT NULL, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id)"
                    + ")";
            stmt.execute(createSelectedItemTable);

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
