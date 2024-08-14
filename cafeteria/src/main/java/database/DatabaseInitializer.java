package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void createTables() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {

            stmt.execute(createFoodPreferenceTable());
            stmt.execute(createFoodTasteTable());
            stmt.execute(createFoodTypeTable());
            stmt.execute(createRolesTable());
            stmt.execute(createMenuTypesTable());
            stmt.execute(createMenuItemsTable());
            stmt.execute(createUsersTable());
            stmt.execute(createDiscardItemsTable());
            stmt.execute(createFeedbacksTable());
            stmt.execute(createNotifiedMessagesTable());
            stmt.execute(createNotificationsTable());
            stmt.execute(createReportsTable());
            stmt.execute(createUserVoteDetails());
            stmt.execute(createPreparedMenuTable());
            stmt.execute(createRolloutMenuItemsTable());
            stmt.execute(createSelectedItemTable());
            stmt.execute(createUserLoginLogsTable());
            stmt.execute(createUserVoteTable());

        } catch (SQLException e) {
            System.out.println("Failed to create tables: " + e.getMessage());
        }
    }

    private static String createFoodPreferenceTable() {
        return "CREATE TABLE IF NOT EXISTS food_preference ("
                + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "food_preference_name VARCHAR(255)"
                + ")";
    }

    private static String createFoodTasteTable() {
        return "CREATE TABLE IF NOT EXISTS food_taste ("
                + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "spice_level VARCHAR(255)"
                + ")";
    }

    private static String createFoodTypeTable() {
        return "CREATE TABLE IF NOT EXISTS food_type ("
                + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "food_type_name VARCHAR(255)"
                + ")";
    }

    private static String createRolesTable() {
        return "CREATE TABLE IF NOT EXISTS roles ("
                + "role_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "role_name VARCHAR(100) NOT NULL"
                + ")";
    }

    private static String createMenuTypesTable() {
        return "CREATE TABLE IF NOT EXISTS menu_types ("
                + "meal_type_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "meal_type VARCHAR(100) NOT NULL"
                + ")";
    }

    private static String createMenuItemsTable() {
        return "CREATE TABLE IF NOT EXISTS menu_items ("
                + "menu_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "price DECIMAL(10,2) NOT NULL, "
                + "availability_status VARCHAR(50) NOT NULL, "
                + "meal_type_id INT, "
                + "rating FLOAT, "
                + "sentiments VARCHAR(255) DEFAULT 'Good', "
                + "sentiment_score DOUBLE, "
                + "food_preference_id INT, "
                + "food_taste_id INT, "
                + "food_type_id INT, "
                + "sweetTooth TINYINT(1), "
                + "FOREIGN KEY (meal_type_id) REFERENCES menu_types(meal_type_id) ON DELETE SET NULL, "
                + "FOREIGN KEY (food_preference_id) REFERENCES food_preference(Id) ON DELETE SET NULL, "
                + "FOREIGN KEY (food_taste_id) REFERENCES food_taste(Id) ON DELETE SET NULL, "
                + "FOREIGN KEY (food_type_id) REFERENCES food_type(Id) ON DELETE SET NULL "
                + ")";
    }

    private static String createUsersTable() {
        return "CREATE TABLE IF NOT EXISTS users ("
                + "user_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100) NOT NULL, "
                + "role_id INT, "
                + "food_preference_id INT, "
                + "food_taste_id INT, "
                + "food_type_id INT, "
                + "sweetTooth TINYINT(1), "
                + "FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (food_preference_id) REFERENCES food_preference(Id) ON DELETE CASCADE, "
                + "FOREIGN KEY (food_taste_id) REFERENCES food_taste(Id) ON DELETE CASCADE, "
                + "FOREIGN KEY (food_type_id) REFERENCES food_type(Id) ON DELETE CASCADE"
                + ")";
    }

    private static String createDiscardItemsTable() {
        return "CREATE TABLE IF NOT EXISTS discard_items ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT, "
                + "name VARCHAR(100), "
                + "discard_date DATE, "
                + "message_id INT, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE"
                + ")";
    }

    private static String createFeedbacksTable() {
        return "CREATE TABLE IF NOT EXISTS feedbacks ("
                + "feedback_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT, "
                + "user_id INT, "
                + "comment TEXT, "
                + "rating INT, "
                + "feedback_date DATE NOT NULL, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ")";
    }

    private static String createNotifiedMessagesTable() {
        return "CREATE TABLE IF NOT EXISTS notifiedMessages ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "messages TEXT"
                + ")";
    }

    private static String createNotificationsTable() {
        return "CREATE TABLE IF NOT EXISTS notifications ("
                + "notification_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "notification_date DATE NOT NULL, "
                + "menu_item_id INT, "
                + "message_id INT, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (message_id) REFERENCES notifiedMessages(id) ON DELETE CASCADE "
                + ")";
    }

    private static String createReportsTable() {
        return "CREATE TABLE IF NOT EXISTS reports ("
                + "report_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT, "
                + "report_date DATE NOT NULL, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id)"
                + ")";
    }

    private static String createUserVoteDetails() {
        return "CREATE TABLE IF NOT EXISTS user_vote ("
                + "user_vote_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "vote_date DATE NOT NULL"
                + ")";
    }

    private static String createPreparedMenuTable() {
        return "CREATE TABLE IF NOT EXISTS prepared_menu ("
                + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT NOT NULL, "
                + "prepared_date DATE NOT NULL, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE "
                + ")";
    }

    private static String createRolloutMenuItemsTable() {
        return "CREATE TABLE IF NOT EXISTS rollout_menu_items ("
                + "rollout_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT, "
                + "rollout_date DATE NOT NULL, "
                + "meal_type_id INT, "
                + "vote INT DEFAULT 0, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                + "FOREIGN KEY (meal_type_id) REFERENCES menu_types(meal_type_id) ON DELETE CASCADE "
                + ")";
    }

    private static String createSelectedItemTable() {
        return "CREATE TABLE IF NOT EXISTS selected_item ("
                + "selected_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "menu_item_id INT NOT NULL, "
                + "Name VARCHAR(255) NOT NULL, "
                + "Rating INT NOT NULL, "
                + "Comment TEXT, "
                + "Date DATE NOT NULL, "
                + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE"
                + ")";
    }

    private static String createUserLoginLogsTable() {
        return "CREATE TABLE IF NOT EXISTS user_login_logs ("
                + "log_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "logout_time TIMESTAMP NULL DEFAULT NULL, "
                + "operations VARCHAR(255), "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ")";
    }

    private static String createUserVoteTable() {
        return "CREATE TABLE IF NOT EXISTS user_vote ("
                + "user_vote_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                + "user_id INT NOT NULL, "
                + "vote_date DATE NOT NULL, "
                + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                + ")";
    }
}
