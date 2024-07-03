package server;

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

            String createFoodPreferenceTable = "CREATE TABLE IF NOT EXISTS food_preference ("
                    + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "food_preference_name VARCHAR(255)"
                    + ")";
            stmt.execute(createFoodPreferenceTable);

            String createFoodTasteTable = "CREATE TABLE IF NOT EXISTS food_taste ("
                    + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "spice_level VARCHAR(255)"
                    + ")";
            stmt.execute(createFoodTasteTable);

            String createFoodTypeTable = "CREATE TABLE IF NOT EXISTS food_type ("
                    + "Id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "food_type_name VARCHAR(255)"
                    + ")";
            stmt.execute(createFoodTypeTable);

            String createRolesTable = "CREATE TABLE IF NOT EXISTS roles ("
                    + "role_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "role_name VARCHAR(100) NOT NULL"
                    + ")";
            stmt.execute(createRolesTable);

            String createMenuTypesTable = "CREATE TABLE IF NOT EXISTS menu_types ("
                    + "meal_type_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "meal_type VARCHAR(100) NOT NULL"
                    + ")";
            stmt.execute(createMenuTypesTable);

            String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items ("
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
            stmt.execute(createMenuItemsTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
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
            stmt.execute(createUsersTable);

            String createDiscardItemsTable = "CREATE TABLE IF NOT EXISTS discard_items ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT, "
                    + "name VARCHAR(100), "
                    + "discard_date DATE, "
                    + "message_id INT, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createDiscardItemsTable);

            String createFeedbacksTable = "CREATE TABLE IF NOT EXISTS feedbacks ("
                    + "feedback_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT, "
                    + "user_id INT, "
                    + "comment TEXT, "
                    + "rating INT, "
                    + "feedback_date DATE NOT NULL, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createFeedbacksTable);

            String createNotifiedMessagesTable = "CREATE TABLE IF NOT EXISTS notifiedMessages ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "messages TEXT"
                    + ")";
            stmt.execute(createNotifiedMessagesTable);

            String createNotificationsTable = "CREATE TABLE IF NOT EXISTS notifications ("
                    + "notification_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "notification_date DATE NOT NULL, "
                    + "menu_item_id INT, "
                    + "message_id INT, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (message_id) REFERENCES notifiedMessages(id) ON DELETE CASCADE "
                    + ")";
            stmt.execute(createNotificationsTable);

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

            String createPreparedMenuTable = "CREATE TABLE IF NOT EXISTS prepared_menu ("
                    + "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT NOT NULL, "
                    + "prepared_date DATE NOT NULL, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE "
                    + ")";
            stmt.execute(createPreparedMenuTable);

            String createRolloutMenuItemsTable = "CREATE TABLE IF NOT EXISTS rollout_menu_items ("
                    + "rollout_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT, "
                    + "rollout_date DATE NOT NULL, "
                    + "meal_type_id INT, "
                    + "vote INT DEFAULT 0, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (meal_type_id) REFERENCES menu_types(meal_type_id) ON DELETE CASCADE "
                    + ")";
            stmt.execute(createRolloutMenuItemsTable);

            String createSelectedItemTable = "CREATE TABLE IF NOT EXISTS selected_item ("
                    + "selected_item_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "menu_item_id INT NOT NULL, "
                    + "Name VARCHAR(255) NOT NULL, "
                    + "Rating INT NOT NULL, "
                    + "Comment TEXT, "
                    + "Date DATE NOT NULL, "
                    + "FOREIGN KEY (menu_item_id) REFERENCES menu_items(menu_item_id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createSelectedItemTable);

            String createUserLoginLogsTable = "CREATE TABLE IF NOT EXISTS user_login_logs ("
                    + "log_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "logout_time TIMESTAMP NULL DEFAULT NULL, "
                    + "operations VARCHAR(255), "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createUserLoginLogsTable);

            String createUserVoteTable = "CREATE TABLE IF NOT EXISTS user_vote ("
                    + "user_vote_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "vote_date DATE NOT NULL, "
                    + "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(createUserVoteTable);

        } catch (SQLException e) {
            System.out.println("Failed to create tables: " + e.getMessage());
        }
    }

    public static void enableEventScheduler() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("SHOW VARIABLES LIKE 'event_scheduler'");
            if (!stmt.getResultSet().next()) {
                return;
            }
            String eventSchedulerStatus = stmt.getResultSet().getString("Value");
            if (!eventSchedulerStatus.equalsIgnoreCase("ON")) {
                String enableScheduler = "SET GLOBAL event_scheduler = ON;";
                stmt.execute(enableScheduler);
            }
        } catch (SQLException e) {
            System.out.println("Failed to enable Event Scheduler: " + e.getMessage());
        }
    }

    public static void createScheduledEvent() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            String createEvent = "CREATE EVENT IF NOT EXISTS daily_cleanup "
                    + "ON SCHEDULE EVERY 1 DAY "
                    + "STARTS CURRENT_TIMESTAMP "
                    + "DO "
                    + "BEGIN "
                    + "DELETE FROM feedbacks WHERE feedback_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM rollout_menu_items WHERE rollout_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM user_vote WHERE vote_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM notifications WHERE notification_date < NOW() - INTERVAL 1 DAY; "
                    + "DELETE FROM prepared_menu WHERE prepared_date < NOW() - INTERVAL 1 MONTH; "
                    + "END;";
            stmt.execute(createEvent);
        } catch (SQLException e) {
            System.out.println("Failed to create scheduled event: " + e.getMessage());
        }
    }
}
